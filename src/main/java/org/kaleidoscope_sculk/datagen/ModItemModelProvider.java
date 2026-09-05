package org.kaleidoscope_sculk.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModItems;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Kaleidoscope_sculk.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.WARDEN_TENDRIL.get());
        basicItem(ModItems.COOKED_WARDEN_TENDRIL_BOWL.get());
        basicItem(ModItems.ANCIENT_BONE_FRAGMENT.get());
        basicItem(ModItems.ANCIENT_BRITTLE_BONE_FRAGMENTS.get());
        basicItem(ModItems.SCULK_FUNGUS_SOUP.get());
        basicItem(ModItems.BOIL_SCULK_PINAPPLE.get());
        basicItem(ModItems.PORK_ANCIENT_BONE_SOUP.get());
        basicItem(ModItems.SCULK_CATERPILLAR.get());
        basicItem(ModItems.SOUL.get());
        basicItem(ModItems.SCULK_BRANCH.get());
        basicItem(ModItems.EERIE_MEAT.get());
        basicItem(ModItems.SCULK_FLESH.get());
        basicItem(ModItems.SCULK_DUST.get());
        basicItem(ModItems.SCULK_CHICKEN_STEW_BLOCK_ITEM.get());
        basicItem(ModItems.SCULK_PORK_RIBS_BLOCK_ITEM.get());
        basicItem(ModItems.SOUL_PANCAKE.get());
        basicItem(ModItems.SCULK_DOUGH.get());
        basicItem(ModItems.COOKED_EERIE_MEAT.get());
        basicItem(ModItems.SILENT_UPGRADE_SMITHING_TEMPLATE.get());
        basicItem(ModItems.ANCIENT_CITY_STYLE_SASHIMI.get());
        basicItem(ModItems.SCULK_LAMB_CHOP.get());
        basicItem(ModItems.HUADIAO_JIU.get());

//        getBuilder("soul_sail")
//                .parent(new ModelFile.UncheckedModelFile("builtin/entity"));

    }
}