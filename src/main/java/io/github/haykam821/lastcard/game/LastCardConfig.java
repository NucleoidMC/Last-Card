package io.github.haykam821.lastcard.game;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.lastcard.game.player.VirtualPlayerConfig;
import net.minecraft.SharedConstants;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

public class LastCardConfig {
	public static final MapCodec<LastCardConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> {
		return instance.group(
			Identifier.CODEC.fieldOf("map").forGetter(LastCardConfig::getMap),
			WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(LastCardConfig::getPlayerConfig),
			VirtualPlayerConfig.CODEC.optionalFieldOf("virtual_players", VirtualPlayerConfig.DEFAULT).forGetter(LastCardConfig::getVirtualPlayers),
			IntProviders.NON_NEGATIVE_CODEC.optionalFieldOf("ticks_until_close", ConstantInt.of(SharedConstants.TICKS_PER_SECOND * 5)).forGetter(LastCardConfig::getTicksUntilClose),
			IntProviders.POSITIVE_CODEC.optionalFieldOf("initial_hand_count", ConstantInt.of(7)).forGetter(LastCardConfig::getInitialHandCount),
			IntProviders.POSITIVE_CODEC.optionalFieldOf("time_of_day", ConstantInt.of(6000)).forGetter(LastCardConfig::getTimeOfDay),
			BlockStateProvider.CODEC.optionalFieldOf("chair_block", BlockStateProvider.simple(Blocks.DARK_OAK_STAIRS)).forGetter(LastCardConfig::getChairBlock)
		).apply(instance, LastCardConfig::new);
	});

	private final Identifier map;
	private final WaitingLobbyConfig playerConfig;
	private final VirtualPlayerConfig virtualPlayers;
	private final IntProvider ticksUntilClose;
	private final IntProvider initialHandCount;
	private final IntProvider timeOfDay;
	private final BlockStateProvider chairBlock;

	public LastCardConfig(Identifier map, WaitingLobbyConfig playerConfig, VirtualPlayerConfig virtualPlayers, IntProvider ticksUntilClose, IntProvider initialHandCount, IntProvider timeOfDay, BlockStateProvider chairBlock) {
		this.map = map;
		this.playerConfig = playerConfig;
		this.virtualPlayers = virtualPlayers;
		this.ticksUntilClose = ticksUntilClose;
		this.initialHandCount = initialHandCount;
		this.timeOfDay = timeOfDay;
		this.chairBlock = chairBlock;
	}

	public Identifier getMap() {
		return this.map;
	}

	public WaitingLobbyConfig getPlayerConfig() {
		return this.playerConfig;
	}

	public VirtualPlayerConfig getVirtualPlayers() {
		return this.virtualPlayers;
	}

	public IntProvider getTicksUntilClose() {
		return this.ticksUntilClose;
	}

	public IntProvider getInitialHandCount() {
		return this.initialHandCount;
	}

	public IntProvider getTimeOfDay() {
		return this.timeOfDay;
	}

	public BlockStateProvider getChairBlock() {
		return this.chairBlock;
	}
}