package org.kaleidoscope_sculk.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.kaleidoscope_sculk.register.ModBlocks;
import org.kaleidoscope_sculk.register.ModEffects;
import org.kaleidoscope_sculk.register.ModItems;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

public class ModLanguageProvider extends LanguageProvider {

    private final String locale;

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, Kaleidoscope_sculk.MODID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        if (locale.equals("en_us")) {
            // 效果名称
            add(ModEffects.SONIC_WAVE.get(), "Sonic Wave");
            add(ModEffects.ECHO.get(), "Echo");
            add(ModEffects.ABYSS.get(), "Abyss");
            add(ModEffects.SCULK_DASH.get(), "Sculk Dash");

            // 创造标签
            add("itemGroup." + Kaleidoscope_sculk.MODID, "Kaleidoscope Sculk");

            // 物品名称
            add(ModItems.WARDEN_TENDRIL.get(), "Warden Tendril");
            add(ModItems.COOKED_WARDEN_TENDRIL_BOWL.get(), "Cooked Warden Tendril Bowl");
            add(ModItems.ANCIENT_BONE_FRAGMENT.get(), "Ancient Bone Fragment");
            add(ModItems.SCULK_FUNGUS.get(), "Sculk Fungus");
            add(ModItems.SCULK_FUNGUS_SOUP.get(), "Sculk Fungus Soup");
            add(ModItems.ECHO_SEED.get(), "Echo Seed");
            add(ModItems.BOIL_SCULK_PINAPPLE.get(), "Boil Sheer Purity");
            add(ModItems.SCULK_CATERPILLAR.get(), "Sculk Caterpillar");
            add(ModItems.SCULK_PINAPPLE.get(), "Sheer Purity");
            add(ModItems.ANCIENT_BRITTLE_BONE_FRAGMENTS.get(), "Ancient Brittle Bone Fragments");
            add(ModItems.PORK_ANCIENT_BONE_SOUP.get(), "Pork Ancient Bone Soup");
            add(ModItems.SCULK_BRANCH.get(), "Sculk Branch");
            add(ModItems.EROSION_KITCHEN_KNIFE.get(), "Erosion Kitchen Knife");
            add(ModItems.EERIE_MEAT.get(), "Eerie Meat");
            add(ModItems.SCULK_FLESH.get(), "Sculk Flesh");
            add(ModItems.SCULK_DUST.get(), "Sculk Dust");
            add(ModItems.SCULK_CHICKEN_STEW_BLOCK_ITEM.get(), "Sculk Chicken Stew");
            add(ModItems.SOUL_PANCAKE.get(), "Soul Pancake");
            add(ModItems.SCULK_DOUGH.get(), "Sculk Dough");
            add(ModItems.COOKED_EERIE_MEAT.get(), "Cooked Eerie Meat");
            add(ModItems.SILENT_UPGRADE_SMITHING_TEMPLATE.get(), "Silent Upgrade Smithing Template");
            add(ModItems.SILENT_KITCHEN_KNIFE.get(), "Silent Kitchen Knife");
            add(ModItems.SOUL.get(), "Soul");

            // 方块名称
            add(ModBlocks.SCULK_STEW_BLOCK.get(), "Sculk Stew");
            add(ModBlocks.ECHO_CROP.get(), "Echo Crop");
            add(ModBlocks.SCULK_PORK_RIBS_BLOCK.get(), "Sculk Pork Ribs");
            add(ModBlocks.DEEPSLATE_STOVE.get(), "Deepslate Stove");
            add(ModBlocks.ANCIENT_CITY_STYLE_SASHIMI_BLOCK.get(), "Ancient City Style Sashimi");
            add(ModBlocks.SCULK_LAMB_CHOP_BLOCK.get(), "Sculk Lamb Chop");

            // 深邃药水
            add("item.minecraft.potion.effect.abyss_potion", "Abyss Potion");
            add("item.minecraft.splash_potion.effect.abyss_potion", "Splash Potion of Abyss");
            add("item.minecraft.lingering_potion.effect.abyss_potion", "Lingering Potion of Abyss");

            // 物品描述
            add("item." + Kaleidoscope_sculk.MODID + ".warden_tendril.tooltip", "A strange tendril dropped by the Warden");
            add("item." + Kaleidoscope_sculk.MODID + ".cooked_warden_tendril_bowl.tooltip", "Delicious and mysterious");
            add("item." + Kaleidoscope_sculk.MODID + ".ancient_bone_fragment.tooltip", "A fragment from the Warden's ancient bones");
            add("item." + Kaleidoscope_sculk.MODID + ".sculk_fungus.tooltip", "A strange fungus grown from sculk");
            add("item." + Kaleidoscope_sculk.MODID + ".sculk_fungus_soup.tooltip", "The smell of fear");
            add("item." + Kaleidoscope_sculk.MODID + ".sculk_stew.tooltip", "A stew that whispers secrets of the deep");
            add("item." + Kaleidoscope_sculk.MODID + ".echo_seed.tooltip", "Perhaps don't disturb it until it becomes mature");
            add("item." + Kaleidoscope_sculk.MODID + ".boil_sheer_purity.tooltip", "It has an inhibitory effect on the sonic detection of the warden.");
            add("item.kaleidoscope_sculk.soul_pancake.tooltip", "Warmth from the soul");
            add("item.kaleidoscope_sculk.silent_knife.charge", "Charge: %d/%d");
            add("item.kaleidoscope_sculk.silent_knife.next_buff", "§eNext full charge attack will trigger a sonic wave!");
            add("item.kaleidoscope_sculk.boil_sculk_pinapple.tooltip", "Is this really tasty?");
            add("item.kaleidoscope_sculk.sculk_branch.tooltip", "A branch cut from sculk, can be ground into dust");
            add("item.kaleidoscope_sculk.soul.tooltip", "Souls scattered in the flames");
            add("item.kaleidoscope_sculk.sculk_soul_sand.tooltip", "Soul sand infused with sculk energy");
            add("item.kaleidoscope_sculk.sculk_plant_fiber.tooltip", "Fibrous material from sculk plants");
            add("item.kaleidoscope_sculk.sculk_meatballs.tooltip", "Bouncy and mysterious meatballs");
            add("item.kaleidoscope_sculk.sculk_mixed_soup.tooltip", "A chaotic but delicious soup");
            add("item.kaleidoscope_sculk.sculk_meat_ingot.tooltip", "An ingot compressed from sculk flesh");
            add("block." + Kaleidoscope_sculk.MODID + ".ancient_city_style_sashimi.tooltip", "Ancient city secret recipe sashimi, a symbol of bravery");
            add("block." + Kaleidoscope_sculk.MODID + ".mustard_effect", "Wasabi (05:00)");

            // 效果描述
            add("item." + Kaleidoscope_sculk.MODID + ".sonic_wave", "Sonic Wave III (05:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".sonic_waveI", "Sonic Wave I (05:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".warmth", "Warmth (05:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".strength", "Strength (03:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".strengthII1800", "Strength II (01:30)");
            add("item." + Kaleidoscope_sculk.MODID + ".echo", "Echo (03:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".vigor", "Vigor (05:00)");
            add("item.kaleidoscope_sculk.warmth_2min", "Warmth (02:00)");
            add("item.kaleidoscope_sculk.resistance", "Resistance (01:00)");
            add("item.kaleidoscope_sculk.regeneration", "Regeneration (00:05)");

            // 按键绑定
            add("key.category." + Kaleidoscope_sculk.MODID, "Kaleidoscope Sculk");
            add("key." + Kaleidoscope_sculk.MODID + ".sonic_boom", "Sonic Boom");

        } else if (locale.equals("zh_cn")) {
            // 效果名称
            add(ModEffects.SONIC_WAVE.get(), "声波");
//            add(ModEffects.SCULK.get(), "幽匿黑暗");
            add(ModEffects.ECHO.get(), "回响");
            add(ModEffects.ABYSS.get(), "深邃");
            add(ModEffects.SCULK_DASH.get(), "幽匿疾行");

            add("itemGroup." + Kaleidoscope_sculk.MODID, "森罗物语：幽匿");

            // 物品名称
            add(ModItems.WARDEN_TENDRIL.get(), "监守者触须");
            add(ModItems.COOKED_WARDEN_TENDRIL_BOWL.get(), "炒监守者触须");
            add(ModItems.ANCIENT_BONE_FRAGMENT.get(), "远古骨碎片");
            add(ModItems.SCULK_FUNGUS.get(), "幽匿真菌");
            add(ModItems.SCULK_FUNGUS_SOUP.get(), "幽匿真菌汤");
            add(ModItems.ECHO_SEED.get(), "回响种子");
            add(ModItems.BOIL_SCULK_PINAPPLE.get(), "炖幽匿菠萝糊糊");
            add(ModItems.SCULK_CATERPILLAR.get(), "幽匿猪儿虫");
            add(ModItems.ANCIENT_BRITTLE_BONE_FRAGMENTS.get(), "远古骨脆片");
            add(ModItems.PORK_ANCIENT_BONE_SOUP.get(), "幽匿炖大骨");
            add(ModItems.SCULK_BRANCH.get(), "幽匿枝");
            add(ModItems.EROSION_KITCHEN_KNIFE.get(), "侵蚀菜刀");
            add(ModItems.EERIE_MEAT.get(), "幽寂肉");
            add(ModItems.SCULK_FLESH.get(), "幽匿果肉");
            add(ModItems.SCULK_DUST.get(), "幽匿粉尘");
            add(ModItems.SCULK_CHICKEN_STEW_BLOCK_ITEM.get(), "幽匿炖鸡煲");
            add(ModItems.SOUL_PANCAKE.get(), "灵魂薄饼");
            add(ModItems.SCULK_DOUGH.get(), "幽匿面团");
            add(ModItems.COOKED_EERIE_MEAT.get(), "熟幽寂肉");
            add(ModItems.SILENT_UPGRADE_SMITHING_TEMPLATE.get(), "静匿升级模板");
            add(ModItems.SILENT_KITCHEN_KNIFE.get(), "静匿菜刀");
            add(ModBlocks.SCULK_LAMB_CHOP_BLOCK.get(), "幽匿羊排");
            add(ModItems.SCULK_JUICE_BUCKET.get(), "幽匿果汁桶");
            add(ModItems.SCULK_BREW.get(), "幽匿菠萝啤");
            add(ModItems.HONGLAN_JIU.get(), "红兰酒");
            add(ModItems.HUADIAO_JIU.get(), "花雕酒");

            // 深邃药水F
            add("item.minecraft.potion.effect.abyss_potion", "深邃药水");
            add("item.minecraft.splash_potion.effect.abyss_potion", "喷溅型深邃药水");
            add("item.minecraft.lingering_potion.effect.abyss_potion", "滞留型深邃药水");


            add(ModItems.DEEPSLATE_CAKE_SLICE.get(), "深板岩蛋糕片");
            add(ModItems.DEEPSLATE_CAKE.get(), "深板岩蛋糕");
            add("item.kaleidoscope_sculk.deepslate_cake_slice.tooltip", "嚼劲十足");
            add("item.kaleidoscope_sculk.deepslate_cake_slice.effect", "生命恢复 I (30秒)");
            add("item.kaleidoscope_sculk.deepslate_cake_slice.damage", "§8§0有点硌牙");
            add("item.kaleidoscope_sculk.deepslate_cake_slice.too_hungry", "§c再吃牙要碎完了");
            add("death.attack.deepslate_cake_slice", "%1$s被硌深板岩蛋糕硌死了");
            add("death.attack.deepslate_cake_slice.player", "%1$s被硌深板岩蛋糕硌死了");

            // 魂幡物品
            add(ModItems.SOUL_SAIL.get(), "魂幡");
            add(ModItems.SOUL_SAIL.get().getDescriptionId() + ".soul", "魂幡");
            add(ModItems.SOUL_SAIL.get().getDescriptionId() + ".thousand", "千魂幡");
            add(ModItems.SOUL_SAIL.get().getDescriptionId() + ".myriad", "万魂魂幡");

            add("item.kaleidoscope_sculk.soul_sail.soul.tooltip", "三十级满级");
            add("item.kaleidoscope_sculk.soul_sail.thousand.tooltip", "六十级满级");
            add("item.kaleidoscope_sculk.soul_sail.myriad.tooltip", "一百级满级");

            // 方块名称
            add(ModBlocks.SCULK_STEW_BLOCK.get(), "幽匿烩菜");
            add(ModBlocks.ECHO_CROP.get(), "回响作物");
            add(ModBlocks.SCULK_PORK_RIBS_BLOCK.get(), "幽匿排骨");
            add(ModBlocks.DEEPSLATE_STOVE.get(), "深板岩炉灶");

            // 物品描述
            add("item." + Kaleidoscope_sculk.MODID + ".warden_tendril.tooltip", "监守者掉落的奇特触须");
            add("item." + Kaleidoscope_sculk.MODID + ".cooked_warden_tendril_bowl.tooltip", "美味而神秘");
            add("item." + Kaleidoscope_sculk.MODID + ".ancient_bone_fragment.tooltip", "监守者远古骨骼的碎片");
            add("item." + Kaleidoscope_sculk.MODID + ".sculk_fungus.tooltip", "从幽匿中生长出的奇异真菌");
            add("item." + Kaleidoscope_sculk.MODID + ".sculk_fungus_soup.tooltip", "恐惧的味道");
            add("item." + Kaleidoscope_sculk.MODID + ".sculk_stew.tooltip", "低语着深渊秘密的炖菜");
            add("item." + Kaleidoscope_sculk.MODID + ".echo_seed.tooltip", "也许应在他成熟前不要打扰它");
            add("item." + Kaleidoscope_sculk.MODID + ".boil_sheer_purity.tooltip", "对监守者的声波检测有抑制作用");
            add("item.kaleidoscope_sculk.soul_pancake.tooltip", "来自灵魂的温暖");
            add("item.kaleidoscope_sculk.silent_knife.charge", "蓄力充能: %d/%d");
            add("item.kaleidoscope_sculk.silent_knife.next_buff", "§e下次满蓄力攻击将触发声波效果！");
            add("item.kaleidoscope_sculk.boil_sculk_pinapple.tooltip", "这玩意真的好吃吗");
            add("item.kaleidoscope_sculk.sculk_branch.tooltip", "用刀从幽匿砍下来的枝条，可以磨成粉");

            // 效果描述
            add("item." + Kaleidoscope_sculk.MODID + ".sonic_wave", "声波 III (05:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".sonic_waveI", "声波 I (05:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".warmth", "温暖 (05:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".strength", "力量 II (03:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".strengthII1800", "力量 II (01:30)");
            add("item." + Kaleidoscope_sculk.MODID + ".echo", "回响 (03:00)");
            add("item." + Kaleidoscope_sculk.MODID + ".vigor", "活力 (05:00)");
            add("item.kaleidoscope_sculk.warmth_2min", "温暖 (02:00)");
            add("item.kaleidoscope_sculk.resistance", "抗性提升 (01:00)");
            add("item.kaleidoscope_sculk.regeneration", "生命恢复 (00:05)");

            // 按键绑定
            add("key.category." + Kaleidoscope_sculk.MODID, "森罗物语-幽匿");
            add("key." + Kaleidoscope_sculk.MODID + ".sonic_boom", "声波攻击");

            add(ModItems.SCULK_PINAPPLE.get(), "幽匿苞果");
            add("item." + Kaleidoscope_sculk.MODID + ".sculk_pinapple.tooltip", "拍拿破？！");

            add(ModItems.SOUL.get(), "灵魂");
            add("item.kaleidoscope_sculk.soul.tooltip", "火焰中散落的灵魂");

            add(ModBlocks.ANCIENT_CITY_STYLE_SASHIMI_BLOCK.get(), "古城风味刺身");

            add("block." + Kaleidoscope_sculk.MODID + ".ancient_city_style_sashimi.tooltip", "古城秘制刺身，勇敢者的象征");
            add("block." + Kaleidoscope_sculk.MODID + ".mustard_effect", "芥末 (05:00)");
        }
    }
}