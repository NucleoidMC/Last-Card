package io.github.haykam821.lastcard.card.color;

import java.util.List;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class WildColorSelector implements ColorSelector {
	protected static final ColorSelector INSTANCE = new WildColorSelector();
	protected static final Iterable<CardColor> SELECTABLE_COLORS = List.of(CardColor.values());

	@Override
	public CardColor select(double x, double y) {
		if (x > 0.5) {
			if (y > 0.5) {
				return CardColor.YELLOW;
			} else {
				return CardColor.BLUE;
			}
		} else {
			if (y > 0.5) {
				return CardColor.GREEN;
			} else {
				return CardColor.RED;
			}
		}
	}

	@Override
	public Iterable<CardColor> getSelectableColors() {
		return SELECTABLE_COLORS;
	}

	@Override
	public boolean isMatching(CardColor color) {
		return true;
	}

	@Override
	public Component getName() {
		return Component.translatable("text.lastcard.card.color.selector.wild");
	}

	@Override
	public Item getItem() {
		return Items.WOOL.white();
	}

	@Override
	public ChatFormatting getFormatting() {
		return ChatFormatting.WHITE;
	}

	@Override
	public BossEvent.BossBarColor getBossBarColor() {
		return BossEvent.BossBarColor.WHITE;
	}

	@Override
	public DrawableCanvas getTemplate() {
		return CardTemplates.WILD_FRONT;
	}

	@Override
	public CanvasColor getCanvasTextColor() {
		return CanvasColor.BLACK_NORMAL;
	}
}
