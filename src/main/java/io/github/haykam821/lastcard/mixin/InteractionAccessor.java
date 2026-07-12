package io.github.haykam821.lastcard.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.Interaction;

@Mixin(Interaction.class)
public interface InteractionAccessor {
	@Invoker("setWidth")
	public void lastcard$setInteractionWidth(float width);

	@Invoker("setHeight")
	public void lastcard$setInteractionHeight(float height);
}
