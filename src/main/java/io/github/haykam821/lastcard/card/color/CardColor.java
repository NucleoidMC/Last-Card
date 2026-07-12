package io.github.haykam821.lastcard.card.color;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import io.github.haykam821.lastcard.turn.TurnManager;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public enum CardColor implements ColorRepresentation {
	RED("red", Items.WOOL.red(), ChatFormatting.RED, BossEvent.BossBarColor.RED, CardTemplates.RED_FRONT, CanvasColor.RED_NORMAL),
	GREEN("green", Items.WOOL.green(), ChatFormatting.GREEN, BossEvent.BossBarColor.GREEN, CardTemplates.GREEN_FRONT, CanvasColor.GREEN_NORMAL),
	YELLOW("yellow", Items.WOOL.yellow(), ChatFormatting.YELLOW, BossEvent.BossBarColor.YELLOW, CardTemplates.YELLOW_FRONT, CanvasColor.YELLOW_NORMAL),
	BLUE("blue", Items.WOOL.blue(), ChatFormatting.DARK_AQUA, BossEvent.BossBarColor.BLUE, CardTemplates.BLUE_FRONT, CanvasColor.BLUE_NORMAL);

	public static final CardColor[] VALUES = CardColor.values();

	private final Component name;
	private final Item item;

	private final ChatFormatting formatting;
	private final BossEvent.BossBarColor bossBarColor;

	private final DrawableCanvas template;
	private final CanvasColor canvasTextColor;

	private final ParticleOptions particle;

	private CardColor(String key, Item item, ChatFormatting formatting, BossEvent.BossBarColor bossBarColor, DrawableCanvas template, CanvasColor canvasTextColor, ParticleOptions particle) {
		this.name = Component.translatable("text.lastcard.card.color." + key);
		this.item = item;

		this.formatting = formatting;
		this.bossBarColor = bossBarColor;

		this.template = template;
		this.canvasTextColor = canvasTextColor;

		this.particle = particle;
	}

	private CardColor(String key, Item item, ChatFormatting formatting, BossEvent.BossBarColor bossBarColor, DrawableCanvas template, CanvasColor canvasTextColor) {
		this(key, item, formatting, bossBarColor, template, canvasTextColor, createParticleEffect(canvasTextColor));
	}

	@Override
	public Component getName() {
		return this.name;
	}

	@Override
	public Item getItem() {
		return this.item;
	}

	@Override
	public ChatFormatting getFormatting() {
		return this.formatting;
	}

	@Override
	public BossEvent.BossBarColor getBossBarColor() {
		return this.bossBarColor;
	}

	@Override
	public DrawableCanvas getTemplate() {
		return this.template;
	}

	@Override
	public CanvasColor getCanvasTextColor() {
		return this.canvasTextColor;
	}

	public ParticleOptions getParticle() {
		return this.particle;
	}

	private static ParticleOptions createParticleEffect(CanvasColor canvasColor) {
		return createParticleEffect(canvasColor.getRgbColor());
	}

	public static ParticleOptions createParticleEffect(int rgb) {
		return new DustColorTransitionOptions(rgb, TurnManager.BLACK_PARTICLE_COLOR, TurnManager.PARTICLE_SIZE);
	}
}
