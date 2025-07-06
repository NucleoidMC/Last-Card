package io.github.haykam821.lastcard.game.map;

import java.util.Comparator;

import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import io.github.haykam821.lastcard.mixin.InteractionEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import xyz.nucleoid.map_templates.TemplateRegion;

public class Chair extends Spawn {
	public static final Comparator<AbstractPlayerEntry> TURN_ORDER_COMPARATOR = Comparator.comparingInt(player -> {
		return player.getChair().turnOrder;
	});

	private static final double MOUNT_Y_OFFSET = 0.5;

	private final BlockPos blockPos;
	private final int turnOrder;

	private final BlockStateProvider chairBlock;

	private Entity mount; 

	public Chair(TemplateRegion region, BlockStateProvider chairBlock) {
		super(region);

		this.blockPos = BlockPos.ofFloored(this.pos);
		this.turnOrder = LastCardRegions.getTurnOrder(region);

		this.chairBlock = chairBlock;
	}

	public boolean isAt(BlockPos pos) {
		return this.blockPos.equals(pos);
	}

	@Override
	public void teleport(ServerPlayerEntity player) {
		ServerWorld world = player.getWorld();
		if (this.mount == null) {
			this.mount = this.createMount(world);
		}

		if (world.isAir(this.blockPos)) {
			Direction facing = Direction.fromHorizontalDegrees(this.rotation).getOpposite();
			BlockState state = this.chairBlock.get(world.getRandom(), this.blockPos).with(StairsBlock.FACING, facing);

			world.setBlockState(this.blockPos, state);
		}

		super.teleport(player);
		player.startRiding(this.mount, true);
	}

	private Entity createMount(ServerWorld world) {
		InteractionEntity mount = EntityType.INTERACTION.create(world, SpawnReason.STRUCTURE);
		InteractionEntityAccessor accessor = (InteractionEntityAccessor) mount;

		accessor.lastcard$setInteractionWidth(0);
		accessor.lastcard$setInteractionHeight(0);

		mount.setPosition(this.pos.getX(), this.pos.getY() + MOUNT_Y_OFFSET, this.pos.getZ());
		mount.setYaw(this.rotation);

		mount.setInvisible(true);
		mount.setNoGravity(true);
		mount.setSilent(true);

		world.spawnEntity(mount);
		return mount;
	}

	public Vec3d getStatusHologramPos() {
		return this.pos.add(0, MOUNT_Y_OFFSET + 1.8, 0);
	}
}
