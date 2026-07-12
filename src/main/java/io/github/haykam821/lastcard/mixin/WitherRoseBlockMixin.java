package io.github.haykam821.lastcard.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.haykam821.lastcard.Main;
import net.minecraft.world.level.block.WitherRoseBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.stimuli.event.EventResult;

@Mixin(WitherRoseBlock.class)
public class WitherRoseBlockMixin {
	@Redirect(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)Z"))
	private boolean modifyWitherRoseInvulnerability(LivingEntity entity, ServerLevel level, DamageSource source) {
		if (entity instanceof ServerPlayer player) {
			GameSpace gameSpace = GameSpaceManager.get().byPlayer(player);
			if (gameSpace != null && gameSpace.getBehavior().testRule(Main.WITHER_ROSE_WITHER_EFFECT) == EventResult.DENY) {
				return true;
			}
		}

		return entity.isInvulnerableTo(level, source);
	}
}
