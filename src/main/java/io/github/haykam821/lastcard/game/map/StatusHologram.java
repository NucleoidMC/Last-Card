package io.github.haykam821.lastcard.game.map;

import org.joml.Vector3f;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.world.entity.Display.BillboardConstraints;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;

public final class StatusHologram {
	private final AbstractPlayerEntry player;

	private final ItemDisplayElement headStackElement;
	private final ElementHolder headStackHolder;

	private final TextDisplayElement textElement;
	private final ElementHolder textHolder;

	public StatusHologram(AbstractPlayerEntry player) {
		this.player = player;

		this.headStackElement = createHeadStackElement();
		this.headStackHolder = createHolder(this.headStackElement);

		this.textElement = createTextElement();
		this.textHolder = createHolder(this.textElement);
	}

	public void update() {
		this.headStackElement.setItem(this.player.createHeadStack());
		this.textElement.setText(this.player.getHologramText());
	}

	public void tick() {
		this.headStackHolder.tick();
		this.textElement.tick();
	}

	public void attach(ServerLevel world, Vec3 pos) {
		Vec3 attachmentPos = pos.subtract(0, 0.12, 0);

		ChunkAttachment.of(this.headStackHolder, world, attachmentPos);
		ChunkAttachment.of(this.textHolder, world, attachmentPos);
	}

	public void destroy() {
		this.headStackHolder.destroy();
		this.textHolder.destroy();
	}

	public static ElementHolder createHolder(DisplayElement element) {
		ElementHolder holder = new ElementHolder();
		holder.addElement(element);

		return holder;
	}

	public static ItemDisplayElement createHeadStackElement() {
		ItemDisplayElement element = createItemElement();

		element.setScale(new Vector3f(1, 1, 0.01f));
		element.setTranslation(new Vector3f(0, 0.68f, 0));

		return element;
	}

	public static ItemDisplayElement createItemElement() {
		ItemDisplayElement element = new ItemDisplayElement();

		applyElementAttributes(element);
		element.setItemDisplayContext(ItemDisplayContext.GROUND);
		element.setLeftRotation(Axis.YP.rotation(Mth.PI));

		return element;
	}

	public static TextDisplayElement createTextElement() {
		TextDisplayElement element = new TextDisplayElement();

		applyElementAttributes(element);
		element.setLineWidth(Integer.MAX_VALUE);

		return element;
	}

	private static <T extends DisplayElement> T applyElementAttributes(T element) {
		element.setBillboardMode(BillboardConstraints.CENTER);

		element.setViewRange(0.3f);

		element.setDisplayWidth(1);
		element.setDisplayHeight(1);

		return element;
	}
}
