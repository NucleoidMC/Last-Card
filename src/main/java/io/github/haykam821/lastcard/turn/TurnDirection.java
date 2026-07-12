package io.github.haykam821.lastcard.turn;

import net.minecraft.network.chat.Component;

public enum TurnDirection {
	CLOCKWISE("clockwise", 1),
	COUNTERCLOCKWISE("counterclockwise", -1);

	private final Component name;
	private final int multiplier;

	private TurnDirection(String key, int multiplier) {
		this.name = Component.translatable("text.lastcard.turn.direction." + key);
		this.multiplier = multiplier;
	}

	public Component getName() {
		return this.name;
	}

	public int multiply(int value) {
		return value * this.multiplier;
	}

	public TurnDirection getOpposite() {
		return this == CLOCKWISE ? COUNTERCLOCKWISE : CLOCKWISE;
	}
}
