package io.github.haykam821.lastcard.util;
import io.github.haykam821.lastcard.Main;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class PlaySound {
    public static final Identifier PLAY_CARD_ID = Main.identifier("play_card");
    public static final Identifier DRAW_CARD_ID = Main.identifier("draw_card");
    public static final Identifier TURN_SKIPPED_ID = Main.identifier("turn_skipped");

    public static void playSound(ServerPlayer player, net.minecraft.core.Holder<net.minecraft.sounds.SoundEvent> soundEvent, SoundSource category, float volume, float pitch) {
        if (player != null) {
            Vec3 pos = player.position();
            player.connection.send(new ClientboundSoundPacket(soundEvent, category, pos.x(), pos.y(), pos.z(), volume, pitch, player.level().getRandom().nextLong()));
        }
    }

    public static void playSound(ServerPlayer player, net.minecraft.sounds.SoundEvent soundEvent, SoundSource category, float volume, float pitch) {
        if (player != null) {
            Vec3 pos = player.position();
            player.connection.send(new ClientboundSoundPacket(Holder.direct(soundEvent), category, pos.x(), pos.y(), pos.z(), volume, pitch, player.level().getRandom().nextLong()));
        }
    }
}


