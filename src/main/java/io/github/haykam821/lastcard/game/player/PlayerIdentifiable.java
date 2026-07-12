package io.github.haykam821.lastcard.game.player;

import io.github.haykam821.lastcard.card.Card;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public abstract class PlayerIdentifiable {
	// Display
	public abstract Component getName();

	public abstract ItemStack createHeadStack();

	public final Component getHologramText() {
		return Component.empty()
			.append(this.getTurnName())
			.append(CommonComponents.NEW_LINE)
			.append(this.getCardStatus());
	}

	// Card information
	public abstract boolean hasTurn();

	public abstract int getCardCount();

	// Player entity
	public abstract ServerPlayer getPlayer();

	public final boolean isPlayer(ServerPlayer player) {
		return player != null && player == this.getPlayer();
	}

	// Messages
	public final Component getWinMessage() {
		return Component.translatable("text.lastcard.win", this.getName()).withStyle(ChatFormatting.GOLD);
	}

	public final Component getNextTurnMessage() {
		return Component.translatable("text.lastcard.turn.next", this.getName()).withStyle(ChatFormatting.GOLD);
	}

	protected final Component getTurnName() {
		Component name = this.getName().copy().withStyle(ChatFormatting.BOLD);
		return this.hasTurn() ? Component.translatable("text.lastcard.status.player_turn", name).withStyle(ChatFormatting.AQUA) : name;
	}

	protected final Component getCardStatus() {
		int cards = this.getCardCount();
		String key = "text.lastcard.status.cards" + (cards == 1 ? ".single" : "");

		return Component.translatable(key, cards);
	}

	public final Component getCardDrewMessage(int count) {
		MutableComponent text;

		if (count == 1) {
			text = Component.translatable("text.lastcard.card_drew", this.getName());
		} else if (count > 1) {
			text = Component.translatable("text.lastcard.card_drew.many", this.getName(), count);
		} else {
			throw new IllegalStateException("Cannot get negative card drew message");
		}

		return text.withStyle(ChatFormatting.GOLD);
	}

	protected final Component getCardDrewYouMessage(Card card) {
		return Component.translatable("text.lastcard.card_drew.you", card.getFullName()).withStyle(ChatFormatting.GOLD);
	}

	public final Component getCardDrewManyYouMessage(int count) {
		return Component.translatable("text.lastcard.card_drew.many.you", count).withStyle(ChatFormatting.GOLD);
	}
}
