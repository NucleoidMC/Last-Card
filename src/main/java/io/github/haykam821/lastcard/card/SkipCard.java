package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import io.github.haykam821.lastcard.turn.action.SkipTurnAction;
import net.minecraft.network.chat.Component;

public class SkipCard extends SymbolCard {
	public SkipCard(ColorSelector selector) {
		super(selector);
	}

	@Override
	public Component getName() {
		return Component.translatable("text.lastcard.card.skip");
	}

	@Override
	public boolean isMatching(Card card, CardColor color) {
		// Allow skip cards to match
		if (card instanceof SkipCard) {
			return true;
		}

		return super.isMatching(card, color);
	}

	@Override
	public void play(AbstractPlayerEntry player) {
		super.play(player);
		player.getPhase().getTurnManager().setNextTurnAction(SkipTurnAction.INSTANCE);
	}

	@Override
	public DrawableCanvas getSymbol() {
		return CardTemplates.SKIP_SYMBOL;
	}
}
