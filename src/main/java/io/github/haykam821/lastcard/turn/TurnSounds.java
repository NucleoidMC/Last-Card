package io.github.haykam821.lastcard.turn;

import io.github.haykam821.lastcard.util.PlaySound;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class TurnSounds {
	private static final SoundEvent TURN_SOUND = SoundEvents.NOTE_BLOCK_CHIME.value();
	private static final SoundSource TURN_SOUND_CATEGORY = SoundSource.PLAYERS;

	private static final float TURN_SOUND_VOLUME = 0.3f;

	private static final float TURN_SOUND_BASE_PITCH = 1.2f;
	private static final float TURN_SOUND_PITCH_STEP = 0.3f;

	private static void playTurnSound(ServerPlayer player, float pitch) {
		PlaySound.playSound(player, TURN_SOUND, TURN_SOUND_CATEGORY, TURN_SOUND_VOLUME, pitch);
	}

	protected static void playTurnSounds(ServerPlayer player) {
		if (player == null) {
			return;
		}

		TurnSounds.playTurnSound(player, TURN_SOUND_BASE_PITCH);
		TurnSounds.playTurnSound(player, TURN_SOUND_BASE_PITCH + TURN_SOUND_PITCH_STEP);
		TurnSounds.playTurnSound(player, TURN_SOUND_BASE_PITCH + TURN_SOUND_PITCH_STEP * 2);
	}
}
