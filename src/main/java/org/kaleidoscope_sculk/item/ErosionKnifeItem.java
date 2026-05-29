package org.kaleidoscope_sculk.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public class ErosionKnifeItem extends SwordItem {

    public ErosionKnifeItem() {
        super(Tiers.DIAMOND, new Properties()
                .attributes(SwordItem.createAttributes(Tiers.STONE, 3, -2.0f))
        );
    }

    public ErosionKnifeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }
}