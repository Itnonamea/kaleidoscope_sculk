// java/org/kaleidoscope_sculk/component/SoulSailData.java
package org.kaleidoscope_sculk.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SoulSailData(
        String type,        // "soul", "thousand", "myriad"
        int storedXp,       // 存储的经验点数
        int rotation        // 旗帜旋转方向
) {
    public static final SoulSailData DEFAULT = new SoulSailData("soul", 0, 0);

    public static final Codec<SoulSailData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("type").forGetter(SoulSailData::type),
                    Codec.INT.fieldOf("storedXp").forGetter(SoulSailData::storedXp),
                    Codec.INT.fieldOf("rotation").forGetter(SoulSailData::rotation)
            ).apply(instance, SoulSailData::new)
    );

    public static final StreamCodec<ByteBuf, SoulSailData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SoulSailData::type,
            ByteBufCodecs.INT,
            SoulSailData::storedXp,
            ByteBufCodecs.INT,
            SoulSailData::rotation,
            SoulSailData::new
    );

    public SoulSailData withType(String newType) {
        return new SoulSailData(newType, this.storedXp, this.rotation);
    }

    public SoulSailData withStoredXp(int newXp) {
        return new SoulSailData(this.type, Math.max(0, newXp), this.rotation);
    }

    public SoulSailData withRotation(int newRotation) {
        return new SoulSailData(this.type, this.storedXp, newRotation);
    }
}