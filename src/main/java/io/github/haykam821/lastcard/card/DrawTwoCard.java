package io.github.haykam821.lastcard.card;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import net.minecraft.network.chat.Component;

public class DrawTwoCard extends DrawCard {
	public DrawTwoCard(ColorSelector selector) {
		super(selector, 2);
	}

	@Override
	public Component getName() {
		return Component.translatable("text.lastcard.card.draw_two");
	}

	@Override
	public DrawableCanvas getSymbol() {
		return CardTemplates.DRAW_TWO_SYMBOL;
	}
}
