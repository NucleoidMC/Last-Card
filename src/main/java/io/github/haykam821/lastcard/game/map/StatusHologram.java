package io.github.haykam821.lastcard.game.map;

import org.joml.Vector3f;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

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

	public void attach(ServerWorld world, Vec3d pos) {
		Vec3d attachmentPos = pos.subtract(0, 0.12, 0);

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
		element.setLeftRotation(RotationAxis.POSITIVE_Y.rotation(MathHelper.PI));

		return element;
	}

	public static TextDisplayElement createTextElement() {
		TextDisplayElement element = new TextDisplayElement();

		applyElementAttributes(element);
		element.setLineWidth(Integer.MAX_VALUE);

		return element;
	}

	private static <T extends DisplayElement> T applyElementAttributes(T element) {
		element.setBillboardMode(BillboardMode.CENTER);

		element.setViewRange(0.3f);

		element.setDisplayWidth(1);
		element.setDisplayHeight(1);

		return element;
	}
}
