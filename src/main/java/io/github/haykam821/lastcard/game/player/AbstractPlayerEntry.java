package io.github.haykam821.lastcard.game.player;

import java.util.ArrayList;
import java.util.List;

import io.github.haykam821.lastcard.card.Card;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.card.display.player.PublicCardDisplay;
import io.github.haykam821.lastcard.game.map.Chair;
import io.github.haykam821.lastcard.game.map.StatusHologram;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import io.github.haykam821.lastcard.turn.action.TurnAction;
import io.github.haykam821.lastcard.util.PlaySound;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import xyz.nucleoid.map_templates.TemplateRegion;

import static io.github.haykam821.lastcard.util.PlaySound.DRAW_CARD_ID;
import static io.github.haykam821.lastcard.util.PlaySound.PLAY_CARD_ID;

public abstract class AbstractPlayerEntry extends PlayerIdentifiable {
	private final LastCardActivePhase phase;
	private final List<Card> cards;

	private final Chair chair;
	private final StatusHologram statusHologram;
	private final CardDisplay publicDisplay;

	private boolean dirtyDisplays = false;

	public AbstractPlayerEntry(LastCardActivePhase phase, TemplateRegion chair, TemplateRegion publicDisplay) {
		this.phase = phase;

		int initialHandCount = phase.getConfig().getInitialHandCount().sample(phase.getLevel().getRandom());
		this.cards = new ArrayList<>(initialHandCount);

		for (int index = 0; index < initialHandCount; index++) {
			this.cards.add(this.phase.getDeck().draw());
		}

		this.chair = new Chair(chair, phase.getConfig().getChairBlock());
		this.statusHologram = new StatusHologram(this);

		this.publicDisplay = new PublicCardDisplay(this, publicDisplay);
	}

	public abstract void spawn();

	public void tick() {
		this.statusHologram.tick();
	}

	@Override
	public final boolean hasTurn() {
		return this.phase.getTurnManager().hasTurn(this);
	}

	@Override
	public final int getCardCount() {
		return this.cards.size();
	}

	public final Chair getChair() {
		return this.chair;
	}

	public final LastCardActivePhase getPhase() {
		return this.phase;
	}

	public final Iterable<Card> getCards() {
		return this.cards;
	}

	public final void playCard(Card card, CardColor color) {
		if (card.canPlay(this)) {
			this.discardCard(card, color);
			card.play(this);
			for (AbstractPlayerEntry player : this.phase.getPlayers()) {
				PlaySound.playSound(player.getPlayer(), SoundEvent.createVariableRangeEvent(PLAY_CARD_ID), SoundSource.PLAYERS, 1, 1);
			}
			if (this.cards.isEmpty()) {
				this.updateDisplays();
				this.phase.updatePileDisplay();

				this.phase.endWithWinner(this);
			} else {
				this.phase.getTurnManager().cycleTurn();
			}
		}
	}

	public final boolean hasPlayableCard() {	
		for (Card card : this.getCards()) {
			if (card.canPlay(this)) {
				return true;
			}
		}

		return false;
	}

	private final void discardCard(Card card, CardColor color) {
		this.phase.getDeck().discard(card, color);

		this.cards.remove(card);
		this.markDirtyDisplays();
	}

	/**
	 * Draws a single card from the deck into this player's hand.
	 * @return the drawn card
	 */
	public final Card draw() {
		Card card = this.phase.getDeck().draw();

		this.cards.add(card);
		this.markDirtyDisplays();
		for (AbstractPlayerEntry player : this.phase.getPlayers()) {
			PlaySound.playSound(player.getPlayer(), SoundEvent.createVariableRangeEvent(DRAW_CARD_ID), SoundSource.PLAYERS, 1, 1);
		}
		return card;
	}

	public final void drawForTurn() {
		if (this.hasTurn() && !this.hasPlayableCard()) {
			Card card = this.draw();

			this.phase.sendMessageWithException(this.getCardDrewMessage(1), this, this.getCardDrewYouMessage(card));
			this.phase.getTurnManager().cycleTurn();
		}
	}

	// Displays
	protected CardDisplay getDisplayViewableBy(ServerPlayer viewer) {
		return this.publicDisplay;
	}

	public final void addDisplay(ServerPlayer viewer) {
		this.getDisplayViewableBy(viewer).add(viewer);
	}

	public final void removeDisplay(ServerPlayer viewer) {
		this.getDisplayViewableBy(viewer).remove(viewer);
	}

	public void attachDisplays() {
		this.statusHologram.attach(this.phase.getLevel(), this.chair.getStatusHologramPos());
		this.updateDisplays();
	}

	public void destroyDisplays() {
		this.statusHologram.destroy();
		this.publicDisplay.destroy();
	}

	public void updateDisplays() {
		this.statusHologram.update();
		this.publicDisplay.update();
	}

	public final void updateDirtyDisplays() {
		if (this.dirtyDisplays) {
			this.updateDisplays();
			this.dirtyDisplays = false;
		}
	}

	public final void markDirtyDisplays() {
		this.dirtyDisplays = true;
	}

	/**
	 * {@return a behavior that this player should perform during their turn instead of normal selection}
	 */
	public TurnAction getTurnAction() {
		return null;
	}
}
