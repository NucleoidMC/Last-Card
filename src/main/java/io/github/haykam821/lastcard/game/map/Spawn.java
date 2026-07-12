package io.github.haykam821.lastcard.game.map;

import java.util.Set;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameType;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;

public class Spawn {
	public final Vec3 pos;
	public final float rotation;

	public Spawn(TemplateRegion region) {
		this.pos = region.getBounds().centerBottom();
		this.rotation = LastCardRegions.getRotation(region);
	}

	public void teleport(ServerPlayer player) {
		player.teleportTo(player.level(), this.pos.x(), this.pos.y(), this.pos.z(), Set.of(), this.rotation, 0, true);
	}

	public JoinAcceptorResult.Teleport acceptPlayers(JoinAcceptor acceptor, ServerLevel level, GameType gameMode) {
		return acceptor.teleport(level, this.pos).thenRunForEach(player -> {
			player.setGameMode(gameMode);
			player.setYRot(this.rotation);
		});
	}
}
