package io.github.haykam821.lastcard.game.map;

import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.api.game.level.generator.TemplateChunkGenerator;

public class LastCardMap {
	private static final RandomSource RANDOM = RandomSource.createThreadLocalInstance();

	private final MapTemplate template;
	private final AABB box;

	private final List<Spawn> waitingSpawns;
	private final TemplateRegion pileCardDisplay;

	public LastCardMap(MapTemplate template) {
		this.template = template;
		this.box = this.template.getBounds().asBox();

		this.waitingSpawns = this.template.getMetadata().getRegions(LastCardRegions.WAITING_SPAWN_MARKER)
			.map(Spawn::new)
			.toList();

		if (this.waitingSpawns.isEmpty()) {
			throw new IllegalStateException("There are no waiting spawns");
		}

		this.pileCardDisplay = this.template.getMetadata().getFirstRegion(LastCardRegions.PILE_CARD_DISPLAY_MARKER);

		if (this.pileCardDisplay == null) {
			throw new IllegalStateException("The pile card display region is missing");
		}
	}

	public boolean contains(ServerPlayer player) {
		return this.box.contains(player.position());
	}

	public Spawn getWaitingSpawn() {
		return Util.getRandom(this.waitingSpawns, RANDOM);
	}

	public TemplateRegion getChair(int index) {
		return LastCardRegions.getRegion(this.template, LastCardRegions.CHAIR_MARKER, index);
	}

	public TemplateRegion getPileCardDisplay() {
		return this.pileCardDisplay;
	}

	public TemplateRegion getPrivateCardDisplay(int index) {
		return LastCardRegions.getRegion(this.template, LastCardRegions.PRIVATE_CARD_DISPLAY_MARKER, index);
	}

	public TemplateRegion getPublicCardDisplay(int index) {
		return LastCardRegions.getRegion(this.template, LastCardRegions.PUBLIC_CARD_DISPLAY_MARKER, index);
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}
}