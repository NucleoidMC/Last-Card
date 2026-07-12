package io.github.haykam821.lastcard.turn.action;

import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.network.chat.Component;

public class DrawTurnAction implements TurnAction {
	private final int value;

	public DrawTurnAction(int value) {
		this.value = value;
	}

	@Override
	public void run(AbstractPlayerEntry player) {
		LastCardActivePhase phase = player.getPhase();

		for (int index = 0; index < this.value; index++) {
			player.draw();
		}

		this.sendDrawMessage(phase, player);
		phase.getTurnManager().cycleTurn();
	}

	private void sendDrawMessage(LastCardActivePhase phase, AbstractPlayerEntry player) {
		Component cardDrewMessage = player.getCardDrewMessage(this.value);
		Component cardDrewManyYouMessage = player.getCardDrewManyYouMessage(this.value);

		phase.sendMessageWithException(cardDrewMessage, player, cardDrewManyYouMessage);
	}
}
