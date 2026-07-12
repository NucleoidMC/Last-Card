package io.github.haykam821.lastcard.turn.action;

import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import io.github.haykam821.lastcard.util.PlaySound;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SkipTurnAction implements TurnAction {
	public static final TurnAction INSTANCE = new SkipTurnAction();

	private SkipTurnAction() {
		return;
	}

	@Override
	public void run(AbstractPlayerEntry player) {
		LastCardActivePhase phase = player.getPhase();
		for (AbstractPlayerEntry playerEntry : phase.getPlayers()) {
			PlaySound.playSound(playerEntry.getPlayer(), SoundEvent.createVariableRangeEvent(PlaySound.TURN_SKIPPED_ID), SoundSource.PLAYERS, 1, 1);
		}
		phase.sendMessageWithException(this.getTurnSkippedMessage(player), player, this.getTurnSkippedYouMessage());
		phase.getTurnManager().cycleTurn();
	}

	private Component getTurnSkippedMessage(AbstractPlayerEntry player) {
		return Component.translatable("text.lastcard.turn.skipped", player.getName()).withStyle(ChatFormatting.GOLD);
	}

	private Component getTurnSkippedYouMessage() {
		return Component.translatable("text.lastcard.turn.skipped.you").withStyle(ChatFormatting.GOLD);
	}
}
