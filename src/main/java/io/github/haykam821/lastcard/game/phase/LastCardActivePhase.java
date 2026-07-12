package io.github.haykam821.lastcard.game.phase;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.google.common.collect.Lists;

import io.github.haykam821.lastcard.Main;
import io.github.haykam821.lastcard.card.CardDeck;
import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.card.display.pile.PileCardDisplay;
import io.github.haykam821.lastcard.card.display.pile.PrivatePileCardDisplay;
import io.github.haykam821.lastcard.game.LastCardConfig;
import io.github.haykam821.lastcard.game.LastPlayedBar;
import io.github.haykam821.lastcard.game.map.Chair;
import io.github.haykam821.lastcard.game.map.LastCardMap;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import io.github.haykam821.lastcard.game.player.PlayerEntry;
import io.github.haykam821.lastcard.game.player.VirtualPlayerEntry;
import io.github.haykam821.lastcard.turn.TurnManager;
import io.github.haykam821.lastcard.util.PlaySound;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.scores.Team;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameType;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.api.game.GameActivity;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeamConfig;
import xyz.nucleoid.plasmid.api.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.api.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

import static io.github.haykam821.lastcard.util.PlaySound.PLAY_CARD_ID;

public class LastCardActivePhase implements PlayerEntryGetter, GameActivityEvents.Destroy, GameActivityEvents.Enable, GameActivityEvents.Tick, GamePlayerEvents.Accept, PlayerDamageEvent, PlayerDeathEvent, GamePlayerEvents.Remove, BlockUseEvent {
	private static final GameTeamKey PLAYERS_KEY = new GameTeamKey("players");
	private static final GameTeam PLAYERS_TEAM = new GameTeam(PLAYERS_KEY, GameTeamConfig.builder()
		.setCollision(Team.CollisionRule.NEVER)
		.setNameTagVisibility(Team.Visibility.NEVER)
		.build());

	private final GameSpace gameSpace;
	private final ServerLevel level;
	private final LastCardConfig config;
	private final LastCardMap map;
	private final TeamManager teams;
	private final LastPlayedBar bar;
	private final List<AbstractPlayerEntry> players;
	private final CardDeck deck = new CardDeck();
	private final TurnManager turnManager;
	private final CardDisplay privatePileDisplay;
	private final CardDisplay publicPileDisplay;
	private final Queue<ServerPlayer> displayAddQueue = new ArrayDeque<>();
	private boolean singleplayer;
	private int ticksUntilClose = -1;

	public LastCardActivePhase(GameSpace gameSpace, ServerLevel level, LastCardConfig config, LastCardMap map, TeamManager teams, GlobalWidgets widgets) {
		this.gameSpace = gameSpace;
		this.level = level;
		this.config = config;
		this.map = map;

		this.teams = teams;
		this.bar = new LastPlayedBar(this, widgets);

		int playerCount = this.gameSpace.getPlayers().participants().size();
		this.players = new ArrayList<>(playerCount);
		this.singleplayer = playerCount == 1;

		TemplateRegion pileCardDisplay = this.map.getPileCardDisplay();
		Vec3 particleOrigin = pileCardDisplay.getBounds().centerBottom();

		this.privatePileDisplay = new PrivatePileCardDisplay(this.getDeck(), this, pileCardDisplay);
		this.publicPileDisplay = new PileCardDisplay(this.getDeck(), this, pileCardDisplay);

		this.turnManager = new TurnManager(this, particleOrigin, this.privatePileDisplay, this.publicPileDisplay);
	}

	public static void open(GameSpace gameSpace, ServerLevel level, LastCardConfig config, LastCardMap map) {
		gameSpace.setActivity(activity -> {
			TeamManager teams = TeamManager.addTo(activity);
			teams.addTeam(PLAYERS_TEAM);

			GlobalWidgets widgets = GlobalWidgets.addTo(activity);
			LastCardActivePhase phase = new LastCardActivePhase(gameSpace, level, config, map, teams, widgets);

			LastCardActivePhase.setRules(activity);
			activity.allow(GameRuleType.DISMOUNT_VEHICLE);

			// Listeners
			activity.listen(GameActivityEvents.DESTROY, phase);
			activity.listen(GameActivityEvents.ENABLE, phase);
			activity.listen(GameActivityEvents.TICK, phase);
			activity.listen(GamePlayerEvents.ACCEPT, phase);
			activity.listen(GamePlayerEvents.OFFER, JoinOffer::acceptSpectators);
			activity.listen(PlayerDamageEvent.EVENT, phase);
			activity.listen(PlayerDeathEvent.EVENT, phase);
			activity.listen(GamePlayerEvents.REMOVE, phase);
			activity.listen(BlockUseEvent.EVENT, phase);
		});
	}

	// Listeners
	@Override
	public void onDestroy(GameCloseReason reason) {
		this.privatePileDisplay.destroy();
		this.publicPileDisplay.destroy();
	}

	@Override
	public void onEnable() {
		// Randomly assign chairs to players
		List<ServerPlayer> players = Lists.newArrayList(gameSpace.getPlayers().participants());
		Collections.shuffle(players);

		int index = 0;

		for (ServerPlayer player : players) {
			TemplateRegion chair = this.map.getChair(index);
			TemplateRegion privateCardDisplay = this.map.getPrivateCardDisplay(index);
			TemplateRegion publicCardDisplay = this.map.getPublicCardDisplay(index);

			AbstractPlayerEntry entry = new PlayerEntry(this, player, chair, privateCardDisplay, publicCardDisplay);

			this.players.add(entry);
			this.publicPileDisplay.add(player);

			teams.addPlayerTo(player, PLAYERS_KEY);

			entry.spawn();
			index += 1;
		}

		RandomSource random = this.level.getRandom();
		int virtualPlayers = this.config.getVirtualPlayers().getCount(random, this.singleplayer);

		for (int i = 0; i < virtualPlayers; i++) {
			TemplateRegion chair = this.map.getChair(index);
			TemplateRegion publicCardDisplay = this.map.getPublicCardDisplay(index);

			AbstractPlayerEntry entry = new VirtualPlayerEntry(this, chair, publicCardDisplay);
			this.players.add(entry);

			entry.spawn();
			index += 1;
		}

		for (ServerPlayer player : this.gameSpace.getPlayers().spectators()) {
			this.spawn(player);
			player.setGameMode(GameType.SPECTATOR);
		}

		// Sort players by turn order
		this.players.sort(Chair.TURN_ORDER_COMPARATOR);
		this.turnManager.setTurn(this.players.get(0));

		this.updatePileDisplay();
		
		for (AbstractPlayerEntry player : this.players) {
			for (ServerPlayer viewer : this.gameSpace.getPlayers()) {
				player.addDisplay(viewer);
			}

			player.attachDisplays();
		}

		this.turnManager.sendNextTurnEffects();
		this.updateBar();
	}

	@Override
	public void onTick() {
		this.turnManager.tick();

		// Decrease ticks until game end to zero
		if (this.isGameEnding()) {
			if (this.ticksUntilClose == 0) {
				this.gameSpace.close(GameCloseReason.FINISHED);
			}

			this.ticksUntilClose -= 1;
			return;
		}

		for (ServerPlayer player : this.gameSpace.getPlayers()) {
			if (!this.map.contains(player)) {
				this.spawn(player);
			}
		}

		for (AbstractPlayerEntry player : this.players) {
			player.tick();
		}

		while (!this.displayAddQueue.isEmpty()) {
			ServerPlayer viewer = this.displayAddQueue.poll();
			this.publicPileDisplay.add(viewer);

			for (AbstractPlayerEntry player : this.players) {
				player.addDisplay(viewer);
			}
		}

		// End early if there are not enough players to continue
		if (this.shouldEndEarly()) {
			this.endWithMessage(this.getEndingMessage());
		}
	}

	@Override
	public JoinAcceptorResult onAcceptPlayers(JoinAcceptor acceptor) {
		return this.map.getWaitingSpawn().acceptPlayers(acceptor, this.level, GameType.SPECTATOR).thenRunForEach(player -> {
			this.displayAddQueue.add(player);
		});
	}

	@Override
	public EventResult onDamage(ServerPlayer player, DamageSource source, float amount) {
		return EventResult.DENY;
	}

	@Override
	public EventResult onDeath(ServerPlayer player, DamageSource source) {
		this.spawn(player);
		return EventResult.DENY;
	}

	@Override
	public void onRemovePlayer(ServerPlayer player) {
		if (this.isGameEnding()) return;

		AbstractPlayerEntry entry = this.getPlayerEntry(player);
		if (entry == null) return;

		this.privatePileDisplay.remove(player);
		this.publicPileDisplay.remove(player);

		for (ServerPlayer viewer : this.gameSpace.getPlayers()) {
			entry.removeDisplay(viewer);
		}

		entry.destroyDisplays();

		// Skip turn
		if (this.turnManager.hasTurn(entry)) {
			this.turnManager.cycleTurn();
		}
		this.players.remove(entry);
	}

	@Override
	public InteractionResult onUse(ServerPlayer player, InteractionHand hand, BlockHitResult hitResult) {
		AbstractPlayerEntry entry = this.getPlayerEntry(player);
		
		if (entry != null) {
			Chair chair = entry.getChair();

			if (chair.isAt(hitResult.getBlockPos())) {
				chair.teleport(player);
			}
		}

		return InteractionResult.FAIL;
	}

	// Utilities
	public void spawn(ServerPlayer player) {
		AbstractPlayerEntry entry = this.getPlayerEntry(player);
		
		if (entry == null) {
			this.map.getWaitingSpawn().teleport(player);
		} else {
			entry.spawn();
		}
	}

	public void updateBar() {
		this.bar.update();
	}

	private boolean shouldEndEarly() {
		if (this.singleplayer) {
			return this.players.isEmpty();
		} else {
			return this.players.size() <= 1;
		}
	}

	private Component getEndingMessage() {
		if (this.players.isEmpty()) {
			return Component.translatable("text.lastcard.no_winners").withStyle(ChatFormatting.GOLD);
		}

		AbstractPlayerEntry winner = this.players.iterator().next();
		return winner.getWinMessage();
	}

	public void endWithWinner(AbstractPlayerEntry player) {
		this.endWithMessage(player.getWinMessage());
	}
	
	private void endWithMessage(Component message) {
		this.sendMessage(message);
		this.ticksUntilClose = this.config.getTicksUntilClose().sample(this.level.getRandom());
		for (AbstractPlayerEntry playerEntry : this.players) {
			PlaySound.playSound(playerEntry.getPlayer(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1, 1);
		}
	}

	public boolean isGameEnding() {
		return this.ticksUntilClose >= 0;
	}

	@Override
	public AbstractPlayerEntry getPlayerEntry(ServerPlayer player) {
		for (AbstractPlayerEntry entry : this.players) {
			if (entry.isPlayer(player)) {
				return entry;
			}
		}
		return null;
	}

	public AbstractPlayerEntry getPlayerEntry(int index) {
		return this.players.get(Math.floorMod(index, this.players.size()));
	}

	public ServerLevel getLevel() {
		return this.level;
	}

	public LastCardConfig getConfig() {
		return this.config;
	}

	public LastCardMap getMap() {
		return this.map;
	}

	public void sendMessage(Component message) {
		this.gameSpace.getPlayers().sendMessage(message);
	}

	public void sendMessageWithException(Component message, AbstractPlayerEntry exception, Component exceptionMessage) {
		for (ServerPlayer player : this.gameSpace.getPlayers()) {
			if (exception.isPlayer(player)) {
				player.sendSystemMessage(exceptionMessage, false);
			} else {
				player.sendSystemMessage(message, false);
			}
		}
	}

	public List<AbstractPlayerEntry> getPlayers() {
		return this.players;
	}

	public CardDeck getDeck() {
		return this.deck;
	}

	public TurnManager getTurnManager() {
		return this.turnManager;
	}

	@Override
	public AbstractPlayerEntry getTurn() {
		return this.turnManager.getTurn();
	}

	public void updatePileDisplay() {
		this.privatePileDisplay.update();
		this.publicPileDisplay.update();
	}

	protected static void setRules(GameActivity activity) {
		activity.deny(GameRuleType.BLOCK_DROPS);
		activity.deny(GameRuleType.BREAK_BLOCKS);
		activity.deny(GameRuleType.CRAFTING);
		activity.deny(GameRuleType.DISMOUNT_VEHICLE);
		activity.deny(GameRuleType.FALL_DAMAGE);
		activity.deny(GameRuleType.FLUID_FLOW);
		activity.deny(GameRuleType.HUNGER);
		activity.deny(GameRuleType.INTERACTION);
		activity.deny(GameRuleType.MODIFY_ARMOR);
		activity.deny(GameRuleType.PICKUP_ITEMS);
		activity.deny(GameRuleType.PLACE_BLOCKS);
		activity.deny(GameRuleType.PORTALS);
		activity.deny(GameRuleType.PVP);
		activity.deny(GameRuleType.THROW_ITEMS);
		activity.deny(Main.WITHER_ROSE_WITHER_EFFECT);
	}
}