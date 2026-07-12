package io.github.haykam821.lastcard.card.color;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public interface ColorRepresentation {
	public Component getName();

	public Item getItem();

	public ChatFormatting getFormatting();

	public BossEvent.BossBarColor getBossBarColor();

	public DrawableCanvas getTemplate();

	public CanvasColor getCanvasTextColor();
}
