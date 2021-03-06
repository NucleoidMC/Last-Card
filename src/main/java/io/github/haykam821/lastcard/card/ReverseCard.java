package io.github.haykam821.lastcard.card;

import io.github.haykam821.lastcard.game.player.PlayerEntry;
import io.github.haykam821.lastcard.turn.TurnDirection;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ReverseCard extends Card {
	public ReverseCard(CardColor color) {
		super(color);
	}

	@Override
	public Text getName() {
		return new TranslatableText("text.lastcard.card.reverse");
	}

	@Override
	public boolean isMatching(Card card) {
		// Allow reverse cards to match
		if (card instanceof ReverseCard) {
			return true;
		}

		return super.isMatching(card);
	}

	@Override
	public void play(PlayerEntry player) {
		super.play(player);

		TurnDirection direction = player.getPhase().getTurnManager().reverseDirection();
		player.getPhase().sendMessage(this.getTurnDirectionMessage(direction));
	}

	private Text getTurnDirectionMessage(TurnDirection direction) {
		return new TranslatableText("text.lastcard.turn.direction_changed", direction.getName()).formatted(Formatting.GOLD);
	}
}
