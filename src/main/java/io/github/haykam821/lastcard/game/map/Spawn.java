package io.github.haykam821.lastcard.game.map;

import java.util.Set;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;

public class Spawn {
	public final Vec3d pos;
	public final float rotation;

	public Spawn(TemplateRegion region) {
		this.pos = region.getBounds().centerBottom();
		this.rotation = LastCardRegions.getRotation(region);
	}

	public void teleport(ServerPlayerEntity player) {
		player.teleport(player.getWorld(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), Set.of(), this.rotation, 0, true);
	}

	public JoinAcceptorResult.Teleport acceptPlayers(JoinAcceptor acceptor, ServerWorld world, GameMode gameMode) {
		return acceptor.teleport(world, this.pos).thenRunForEach(player -> {
			player.changeGameMode(gameMode);
			player.setYaw(this.rotation);
		});
	}
}
