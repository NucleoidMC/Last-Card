package io.github.haykam821.lastcard.game.phase;

import io.github.haykam821.lastcard.game.LastCardConfig;
import io.github.haykam821.lastcard.game.map.LastCardMap;
import io.github.haykam821.lastcard.game.map.LastCardMapBuilder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameType;
import xyz.nucleoid.fantasy.RuntimeLevelConfig;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class LastCardWaitingPhase implements GamePlayerEvents.Accept, GameActivityEvents.Tick, PlayerDamageEvent, PlayerDeathEvent, GameActivityEvents.RequestStart {
	private final GameSpace gameSpace;
	private final ServerLevel level;

	private final LastCardConfig config;
	private final LastCardMap map;

	public LastCardWaitingPhase(GameSpace gameSpace, ServerLevel level, LastCardConfig config, LastCardMap map) {
		this.gameSpace = gameSpace;
		this.level = level;

		this.config = config;
		this.map = map;
	}

	public static GameOpenProcedure open(GameOpenContext<LastCardConfig> context) {
		LastCardConfig config = context.config();

		MinecraftServer server = context.server();
		RandomSource random = server.overworld().getRandom();

		LastCardMapBuilder mapBuilder = new LastCardMapBuilder(config);
		LastCardMap map = mapBuilder.create(server);

		RuntimeLevelConfig worldConfig = new RuntimeLevelConfig()
			//.setTimeOfDay(config.getTimeOfDay().sample(random))
			.setGenerator(map.createGenerator(server));

		return context.openWithLevel(worldConfig, (activity, level) -> {
			LastCardWaitingPhase phase = new LastCardWaitingPhase(activity.getGameSpace(), level, config, map);
			GameWaitingLobby.addTo(activity, config.getPlayerConfig());

			LastCardActivePhase.setRules(activity);
			activity.deny(GameRuleType.MODIFY_INVENTORY);

			// Listeners
			activity.listen(GamePlayerEvents.ACCEPT, phase);
			activity.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
			activity.listen(GameActivityEvents.TICK, phase);
			activity.listen(PlayerDamageEvent.EVENT, phase);
			activity.listen(PlayerDeathEvent.EVENT, phase);
			activity.listen(GameActivityEvents.REQUEST_START, phase);
		});
	}

	@Override
	public JoinAcceptorResult onAcceptPlayers(JoinAcceptor acceptor) {
		return this.map.getWaitingSpawn().acceptPlayers(acceptor, this.level, GameType.ADVENTURE);
	}

	@Override
	public void onTick() {
		for (ServerPlayer player : this.gameSpace.getPlayers()) {
			if (!this.map.contains(player)) {
				this.spawn(player);
			}
		}
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
	public GameResult onRequestStart() {
		LastCardActivePhase.open(this.gameSpace, this.level, this.config, this.map);
		return GameResult.ok();
	}

	private void spawn(ServerPlayer player) {
		this.map.getWaitingSpawn().teleport(player);
	}
}