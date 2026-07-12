package io.github.haykam821.lastcard.game.map;

import java.io.IOException;

import io.github.haykam821.lastcard.game.LastCardConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.MapTemplateSerializer;
import xyz.nucleoid.plasmid.api.game.GameOpenException;

public class LastCardMapBuilder {
	private final LastCardConfig config;

	public LastCardMapBuilder(LastCardConfig config) {
		this.config = config;
	}

	public LastCardMap create(MinecraftServer server) {
		try {
			MapTemplate template = MapTemplateSerializer.loadFromResource(server, this.config.getMap());
			return new LastCardMap(template);
		} catch (IOException exception) {
			throw new GameOpenException(Component.translatable("text.infiniteparkour.template_load_failed"), exception);
		}
	}
}