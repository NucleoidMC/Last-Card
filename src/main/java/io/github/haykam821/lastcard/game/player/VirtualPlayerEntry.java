package io.github.haykam821.lastcard.game.player;

import org.apache.commons.lang3.RandomStringUtils;

import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import io.github.haykam821.lastcard.turn.action.TurnAction;
import io.github.haykam821.lastcard.turn.action.VirtualTurnAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import xyz.nucleoid.map_templates.TemplateRegion;

public class VirtualPlayerEntry extends AbstractPlayerEntry {
	private final String key = RandomStringUtils.randomAlphabetic(6);
	private final Component name = Component.literal(this.key).withStyle(ChatFormatting.GRAY);

	public VirtualPlayerEntry(LastCardActivePhase phase, TemplateRegion chair, TemplateRegion publicDisplay) {
		super(phase, chair, publicDisplay);
	}

	@Override
	public void spawn() {
		return;
	}

	@Override
	public Component getName() {
		return this.name;
	}

	@Override
	public ServerPlayer getPlayer() {
		return null;
	}

	@Override
	public ItemStack createHeadStack() {
		return new ItemStack(Items.COMMAND_BLOCK);
	}

	@Override
	public TurnAction getTurnAction() {
		return VirtualTurnAction.INSTANCE;
	}

	@Override
	public String toString() {
		return "VirtualPlayerEntry[" + this.key + "]";
	}
}
