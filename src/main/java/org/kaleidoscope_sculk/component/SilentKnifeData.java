// kaleidoscope_sculk/component/SilentKnifeData.java
package org.kaleidoscope_sculk.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SilentKnifeData(int chargeCount) {
    public static final SilentKnifeData DEFAULT = new SilentKnifeData(0);

    public static final Codec<SilentKnifeData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("chargeCount").forGetter(SilentKnifeData::chargeCount)
            ).apply(instance, SilentKnifeData::new)
    );

    public static final StreamCodec<ByteBuf, SilentKnifeData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SilentKnifeData::chargeCount,
            SilentKnifeData::new
    );

    public SilentKnifeData increment() {
        return new SilentKnifeData((chargeCount + 1) % 20);
    }

    public boolean canGiveBuff() {
        return chargeCount == 19; // 即将满20次时，下一次触发
    }

    public int getProgress() {
        return chargeCount;
    }
}