package io.github.haykam821.lastcard.game.player;

import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.card.display.player.PrivateCardDisplay;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import xyz.nucleoid.map_templates.TemplateRegion;

public class PlayerEntry extends AbstractPlayerEntry {
	private final ServerPlayer player;
	private final CardDisplay privateDisplay;

	public PlayerEntry(LastCardActivePhase phase, ServerPlayer player, TemplateRegion chair, TemplateRegion privateDisplay, TemplateRegion publicDisplay) {
		super(phase, chair, publicDisplay);

		this.player = player;
		this.privateDisplay = new PrivateCardDisplay(this, privateDisplay);
	}

	@Override
	public void spawn() {
		this.getChair().teleport(this.player);
	}

	@Override
	public Component getName() {
		return this.player.getDisplayName();
	}

	@Override
	public ServerPlayer getPlayer() {
		return this.player;
	}

	@Override
	protected CardDisplay getDisplayViewableBy(ServerPlayer viewer) {
		return this.isPlayer(viewer) ? this.privateDisplay : super.getDisplayViewableBy(viewer);
	}

	@Override
	public void destroyDisplays() {
		super.destroyDisplays();
		this.privateDisplay.destroy();
	}

	@Override
	public ItemStack createHeadStack() {
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
		ResolvableProfile component = ResolvableProfile.createResolved(this.player.getGameProfile());
		stack.set(DataComponents.PROFILE, component);

		return stack;
	}

	@Override
	public void updateDisplays() {
		super.updateDisplays();
		this.privateDisplay.update();
	}

	@Override
	public String toString() {
		return "PlayerEntry{player=" + this.player + ", chair=" + this.getChair() + "}";
	}
}
