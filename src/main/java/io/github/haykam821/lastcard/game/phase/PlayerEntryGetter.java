package io.github.haykam821.lastcard.game.phase;

import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerEntryGetter {
	public AbstractPlayerEntry getPlayerEntry(ServerPlayer player);

	public AbstractPlayerEntry getTurn();
}
