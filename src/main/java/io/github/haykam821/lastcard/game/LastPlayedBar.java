package io.github.haykam821.lastcard.game;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.world.BossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.common.widget.BossBarWidget;

public class LastPlayedBar {
	private static final BossEvent.BossBarOverlay STYLE = BossEvent.BossBarOverlay.PROGRESS;

	private final LastCardActivePhase phase;
	private final BossBarWidget bar;

	public LastPlayedBar(LastCardActivePhase phase, GlobalWidgets widgets) {
		this.phase = phase;
		this.bar = widgets.addBossBar(this.getTitle(), this.getColor(), STYLE);
	}

	public void update() {
		this.bar.setTitle(this.getTitle());
		this.bar.setStyle(this.getColor(), STYLE);
	}

	private Component getTitle() {
		Card previousCard = this.phase.getDeck().getPreviousCard();
		if (previousCard == null) {
			return Component.translatable("text.lastcard.last_played.none");
		}

		return Component.translatable("text.lastcard.last_played", previousCard.getFullName()).withStyle(ChatFormatting.GOLD);
	}

	private BossEvent.BossBarColor getColor() {
		CardColor color = this.phase.getDeck().getPreviousColor();
		if (color == null) {
			return BossEvent.BossBarColor.WHITE;
		}

		return color.getBossBarColor();
	}
}
