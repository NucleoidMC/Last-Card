package io.github.haykam821.lastcard.game.player;

import io.github.haykam821.lastcard.card.Card;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class PlayerIdentifiable {
	// Display
	public abstract Text getName();

	public abstract ItemStack createHeadStack();

	public final Text getHologramText() {
		return Text.empty()
			.append(this.getTurnName())
			.append(ScreenTexts.LINE_BREAK)
			.append(this.getCardStatus());
	}

	// Card information
	public abstract boolean hasTurn();

	public abstract int getCardCount();

	// Player entity
	public abstract ServerPlayerEntity getPlayer();

	public final boolean isPlayer(ServerPlayerEntity player) {
		return player != null && player == this.getPlayer();
	}

	// Messages
	public final Text getWinMessage() {
		return Text.translatable("text.lastcard.win", this.getName()).formatted(Formatting.GOLD);
	}

	public final Text getNextTurnMessage() {
		return Text.translatable("text.lastcard.turn.next", this.getName()).formatted(Formatting.GOLD);
	}

	protected final Text getTurnName() {
		Text name = this.getName().copy().formatted(Formatting.BOLD);
		return this.hasTurn() ? Text.translatable("text.lastcard.status.player_turn", name).formatted(Formatting.AQUA) : name;
	}

	protected final Text getCardStatus() {
		int cards = this.getCardCount();
		String key = "text.lastcard.status.cards" + (cards == 1 ? ".single" : "");

		return Text.translatable(key, cards);
	}

	public final Text getCardDrewMessage(int count) {
		MutableText text;

		if (count == 1) {
			text = Text.translatable("text.lastcard.card_drew", this.getName());
		} else if (count > 1) {
			text = Text.translatable("text.lastcard.card_drew.many", this.getName(), count);
		} else {
			throw new IllegalStateException("Cannot get negative card drew message");
		}

		return text.formatted(Formatting.GOLD);
	}

	protected final Text getCardDrewYouMessage(Card card) {
		return Text.translatable("text.lastcard.card_drew.you", card.getFullName()).formatted(Formatting.GOLD);
	}

	public final Text getCardDrewManyYouMessage(int count) {
		return Text.translatable("text.lastcard.card_drew.many.you", count).formatted(Formatting.GOLD);
	}
}
