package io.github.haykam821.lastcard.game.map;

import java.util.Comparator;

import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import io.github.haykam821.lastcard.mixin.InteractionAccessor;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
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

		this.blockPos = BlockPos.containing(this.pos);
		this.turnOrder = LastCardRegions.getTurnOrder(region);

		this.chairBlock = chairBlock;
	}

	public boolean isAt(BlockPos pos) {
		return this.blockPos.equals(pos);
	}

	@Override
	public void teleport(ServerPlayer player) {
		ServerLevel level = player.level();
		if (this.mount == null) {
			this.mount = this.createMount(level);
		}

		if (level.isEmptyBlock(this.blockPos)) {
			Direction facing = Direction.fromYRot(this.rotation).getOpposite();
			BlockState state = this.chairBlock.getState(level, level.getRandom(), this.blockPos).setValue(StairBlock.FACING, facing);

			level.setBlockAndUpdate(this.blockPos, state);
		}

		super.teleport(player);
		player.startRiding(this.mount, true, true);
	}

	private Entity createMount(ServerLevel level) {
		Interaction mount = EntityTypes.INTERACTION.create(level, EntitySpawnReason.STRUCTURE);
		InteractionAccessor accessor = (InteractionAccessor) mount;

		accessor.lastcard$setInteractionWidth(0);
		accessor.lastcard$setInteractionHeight(0);

		mount.setPos(this.pos.x(), this.pos.y() + MOUNT_Y_OFFSET, this.pos.z());
		mount.setYRot(this.rotation);

		mount.setInvisible(true);
		mount.setNoGravity(true);
		mount.setSilent(true);

		level.addFreshEntity(mount);
		return mount;
	}

	public Vec3 getStatusHologramPos() {
		return this.pos.add(0, MOUNT_Y_OFFSET + 1.8, 0);
	}
}
