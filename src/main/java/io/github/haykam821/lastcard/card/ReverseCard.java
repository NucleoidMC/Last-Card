package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import io.github.haykam821.lastcard.turn.TurnDirection;
import io.github.haykam821.lastcard.util.PlaySound;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class ReverseCard extends SymbolCard {
	public ReverseCard(ColorSelector selector) {
		super(selector);
	}

	@Override
	public Component getName() {
		return Component.translatable("text.lastcard.card.reverse");
	}

	@Override
	public boolean isMatching(Card card, CardColor color) {
		// Allow reverse cards to match
		if (card instanceof ReverseCard) {
			return true;
		}

		return super.isMatching(card, color);
	}

	@Override
	public void play(AbstractPlayerEntry player) {
		super.play(player);

		TurnDirection direction = player.getPhase().getTurnManager().reverseDirection();
		player.getPhase().sendMessage(this.getTurnDirectionMessage(direction));
	}

	private Component getTurnDirectionMessage(TurnDirection direction) {
		return Component.translatable("text.lastcard.turn.direction_changed", direction.getName()).withStyle(ChatFormatting.GOLD);
	}

	@Override
	public DrawableCanvas getSymbol() {
		return CardTemplates.REVERSE_SYMBOL;
	}
}
