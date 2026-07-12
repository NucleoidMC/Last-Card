package io.github.haykam821.lastcard.game.player;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProviders;

public record VirtualPlayerConfig(
        IntProvider singleplayerCount,
        IntProvider multiplayerCount
) {
	public static final VirtualPlayerConfig DEFAULT = new VirtualPlayerConfig(ConstantInt.of(3), ConstantInt.of(0));

	private static final Codec<VirtualPlayerConfig> RECORD_CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			IntProviders.NON_NEGATIVE_CODEC.optionalFieldOf("singleplayer_count", DEFAULT.singleplayerCount()).forGetter(VirtualPlayerConfig::singleplayerCount),
			IntProviders.NON_NEGATIVE_CODEC.optionalFieldOf("multiplayer_count", DEFAULT.multiplayerCount()).forGetter(VirtualPlayerConfig::multiplayerCount)
		).apply(instance, VirtualPlayerConfig::new);
	});

	public static final Codec<VirtualPlayerConfig> CODEC = Codec.either(IntProviders.NON_NEGATIVE_CODEC, VirtualPlayerConfig.RECORD_CODEC).xmap(
		either -> either.map(VirtualPlayerConfig::new, Function.identity()),
		config -> config.singleplayerCount().equals(config.multiplayerCount()) ? Either.left(config.singleplayerCount()) : Either.right(config)
	);

	public VirtualPlayerConfig(IntProvider count) {
		this(count, count);
	}

	public int getCount(RandomSource random, boolean singleplayer) {
		IntProvider provider = singleplayer ? this.singleplayerCount() : this.multiplayerCount();
		return provider.sample(random);
	}
}
