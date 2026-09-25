package Tequilacat.KaleidoscopeSculk.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import Tequilacat.KaleidoscopeSculk.Kaleidoscope_sculk;
import Tequilacat.KaleidoscopeSculk.network.ModPackets;
import Tequilacat.KaleidoscopeSculk.register.ModEffects;
import Tequilacat.KaleidoscopeSculk.register.ModItems;
import Tequilacat.KaleidoscopeSculk.register.ModRegistries;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class ModHandlers {

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addRecipe(
                Ingredient.of(createPotionStack(Potions.STRONG_STRENGTH)),
                Ingredient.of(ModItems.SCULK_FUNGUS.get()),
                createPotionStack(ModRegistries.ABYSS_POTION)
        );
    }

    private static ItemStack createPotionStack(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }

    private static final int TOOLTIP_INDEX = 1;

    private static Map<Item, Component> simpleTooltips;

    private static Map<Item, Component> simpleTooltips() {
        Map<Item, Component> cached = simpleTooltips;
        if (cached == null) {
            cached = Map.ofEntries(
                    simpleTooltip(ModItems.WARDEN_TENDRIL, "warden_tendril", ChatFormatting.GRAY),
                    simpleTooltip(ModItems.ANCIENT_BONE_FRAGMENT, "ancient_bone_fragment", ChatFormatting.DARK_GRAY),
                    simpleTooltip(ModItems.SCULK_FUNGUS, "sculk_fungus", ChatFormatting.GRAY),
                    simpleTooltip(ModItems.ECHO_SEED, "echo_seed", ChatFormatting.GRAY),
                    simpleTooltip(ModItems.SCULK_BRANCH, "sculk_branch", ChatFormatting.GRAY),
                    simpleTooltip(ModItems.SCULK_PINAPPLE, "sculk_pinapple", ChatFormatting.DARK_GRAY),
                    simpleTooltip(ModItems.SOUL, "soul", ChatFormatting.GRAY)
            );
            simpleTooltips = cached;
        }
        return cached;
    }

    private static Map.Entry<Item, Component> simpleTooltip(Supplier<? extends Item> item,
                                                            String key, ChatFormatting color) {
        return Map.entry(item.get(),
                Component.translatable("item.kaleidoscope_sculk." + key + ".tooltip").withStyle(color));
    }

    private static Map<Item, List<Component>> multiLineTooltips;

    private static Map<Item, List<Component>> multiLineTooltips() {
        Map<Item, List<Component>> cached = multiLineTooltips;
        if (cached == null) {
            cached = Map.ofEntries(
                    Map.entry(ModItems.SCULK_CATERPILLAR.get(), List.of(
                            Component.empty(),
                            Component.translatable("item.kaleidoscope_sculk.echo")
                                    .withStyle(ChatFormatting.BLUE))),
                    Map.entry(ModItems.ANCIENT_BRITTLE_BONE_FRAGMENTS.get(), List.of(
                            Component.empty(),
                            Component.translatable("item.kaleidoscope_sculk.strengthII1800")
                                    .withStyle(ChatFormatting.BLUE))),
                    Map.entry(ModItems.SOUL_PANCAKE.get(), List.of(
                            Component.translatable("item.kaleidoscope_sculk.soul_pancake.tooltip")
                                    .withStyle(ChatFormatting.DARK_GRAY),
                            Component.empty(),
                            Component.translatable("item.kaleidoscope_sculk.resistance")
                                    .withStyle(ChatFormatting.BLUE),
                            Component.translatable("item.kaleidoscope_sculk.regeneration")
                                    .withStyle(ChatFormatting.BLUE)))
            );
            multiLineTooltips = cached;
        }
        return cached;
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        List<Component> tooltip = event.getToolTip();

        Component simpleLine = simpleTooltips().get(item);
        if (simpleLine != null) {
            tooltip.add(TOOLTIP_INDEX, simpleLine);
        }

        List<Component> extraLines = multiLineTooltips().get(item);
        if (extraLines != null) {
            tooltip.addAll(TOOLTIP_INDEX, extraLines);
        }
    }

    private static final int START_DEPTH = 0;
    private static final int MAX_DEPTH = -60;
    private static final float BASE_MAX_BONUS_DAMAGE = 5.0f;
    private static final float BONUS_PER_LEVEL = 3.0f;
    private static final double DEPTH_SCALE = 15.0;
    private static final double MAX_DEPTH_RANGE = START_DEPTH - MAX_DEPTH;

    private static Holder<MobEffect> abyssCache;
    private static Holder<MobEffect> echoCache;

    private static Holder<MobEffect> abyss() {
        Holder<MobEffect> holder = abyssCache;
        if (holder == null) {
            holder = ModEffects.ABYSS.getDelegate();
            abyssCache = holder;
        }
        return holder;
    }

    private static Holder<MobEffect> echo() {
        Holder<MobEffect> holder = echoCache;
        if (holder == null) {
            holder = ModEffects.ECHO.getDelegate();
            echoCache = holder;
        }
        return holder;
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity attacker = resolveAttacker(event.getSource());
        if (attacker == null) return;

        // Abyss: the deeper the player, the higher the damage; consumes Echo on hit.
        if (attacker instanceof Player player) {
            MobEffectInstance abyssEffect = player.getEffect(abyss());
            if (abyssEffect != null) {
                float bonusDamage = calculateAbyssBonus(player.getY(), abyssEffect.getAmplifier());
                if (bonusDamage > 0.0f) {
                    event.setAmount(event.getAmount() + bonusDamage);
                }
            }
        }

        if (attacker.hasEffect(echo())) {
            attacker.removeEffect(echo());
        }
    }

    private static LivingEntity resolveAttacker(DamageSource source) {
        if (source.getEntity() instanceof LivingEntity living) {
            return living;
        }
        return source.getDirectEntity() instanceof LivingEntity living ? living : null;
    }

    private static float calculateAbyssBonus(double y, int amplifier) {
        if (y >= START_DEPTH) {
            return 0.0f;
        }

        double depth = START_DEPTH - y;
        double clampedDepth = Math.min(depth, MAX_DEPTH_RANGE);
        double ratio = clampedDepth / (clampedDepth + DEPTH_SCALE);

        float maxBonus = BASE_MAX_BONUS_DAMAGE + (amplifier * BONUS_PER_LEVEL);
        float bonusDamage = (float) (ratio * maxBonus);

        return Math.min(maxBonus, Math.max(0.0f, bonusDamage));
    }

    private static final Map<UUID, Float> WARDEN_ACCUMULATED_DAMAGE = new ConcurrentHashMap<>();

    private static final float WARDEN_DROP_INTERVAL_RATIO = 0.10f;
    private static final int WARDEN_MIN_DROPS_PER_INTERVAL = 1;
    private static final int WARDEN_MAX_DROPS_PER_INTERVAL = 2;

    @SubscribeEvent
    public static void onWardenDamage(LivingDamageEvent.Post event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Warden warden)) return;
        if (warden.level().isClientSide) return;

        float damage = event.getNewDamage();
        if (damage <= 0) return;

        UUID uuid = warden.getUUID();
        float maxHealth = warden.getMaxHealth();
        float intervalDamage = maxHealth * WARDEN_DROP_INTERVAL_RATIO;

        float currentDamage = WARDEN_ACCUMULATED_DAMAGE.getOrDefault(uuid, 0f);
        float newDamage = Math.min(currentDamage + damage, maxHealth);

        int totalIntervals = (int) (newDamage / intervalDamage);
        int previousIntervals = (int) (currentDamage / intervalDamage);
        int intervalsToProcess = totalIntervals - previousIntervals;

        for (int i = 0; i < intervalsToProcess; i++) {
            int count = WARDEN_MIN_DROPS_PER_INTERVAL
                    + warden.getRandom().nextInt(WARDEN_MAX_DROPS_PER_INTERVAL - WARDEN_MIN_DROPS_PER_INTERVAL + 1);
            for (int j = 0; j < count; j++) {
                spawnAncientBoneFragment(warden);
            }
        }

        WARDEN_ACCUMULATED_DAMAGE.put(uuid, newDamage);
    }

    private static void spawnAncientBoneFragment(Warden warden) {
        ItemEntity drop = new ItemEntity(warden.level(), warden.getX(), warden.getY(), warden.getZ(),
                new ItemStack(ModItems.ANCIENT_BONE_FRAGMENT.get()));
        drop.setDefaultPickUpDelay();
        warden.level().addFreshEntity(drop);
    }

    @SubscribeEvent
    public static void onWardenDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Warden warden) {
            WARDEN_ACCUMULATED_DAMAGE.remove(warden.getUUID());
        }
    }

    @SubscribeEvent
    public static void onWardenLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Warden warden) {
            WARDEN_ACCUMULATED_DAMAGE.remove(warden.getUUID());
        }
    }

    private static final float SOUL_FIRE_DROP_CHANCE = 0.03f;

    @SubscribeEvent
    public static void onSoulFireBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!event.getState().is(Blocks.SOUL_FIRE)) {
            return;
        }

        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();

        if (level.getRandom().nextFloat() < SOUL_FIRE_DROP_CHANCE) {
            level.addFreshEntity(createDrop(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    new ItemStack(ModItems.SOUL.get())));
        }
    }

    @SubscribeEvent
    public static void onWardenDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Warden warden)) {
            return;
        }

        int dropCount = 1 + warden.getRandom().nextInt(2);
        for (int i = 0; i < dropCount; i++) {
            event.getDrops().add(createDrop(warden.level(),
                    warden.getX(), warden.getY(), warden.getZ(),
                    new ItemStack(ModItems.WARDEN_TENDRIL.get())));
        }
    }

    @SubscribeEvent
    public static void onKnifeKillDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (!(event.getSource().getEntity() instanceof Player killer)) {
            return;
        }

        ItemStack mainHand = killer.getMainHandItem();
        if (!mainHand.is(ModItems.EROSION_KITCHEN_KNIFE.get())
                && !mainHand.is(ModItems.SILENT_KITCHEN_KNIFE.get())) {
            return;
        }

        if (!isConvertibleAnimal(entity)) {
            return;
        }

        playConvertParticles(entity);

        int totalMeatCount = 0;
        for (Iterator<ItemEntity> iterator = event.getDrops().iterator(); iterator.hasNext(); ) {
            ItemEntity drop = iterator.next();
            ItemStack stack = drop.getItem();
            if (isMeatItem(stack)) {
                totalMeatCount += stack.getCount();
                iterator.remove();
                drop.discard();
            }
        }

        if (totalMeatCount > 0) {
            event.getDrops().add(createDrop(entity.level(),
                    entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(ModItems.EERIE_MEAT.get(), totalMeatCount)));
        }
    }

    private static void playConvertParticles(Entity entity) {
        if (entity.level().isClientSide) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) entity.level();
        Vec3 deathPos = entity.position();

        serverLevel.sendParticles(new SculkChargeParticleOptions(serverLevel.random.nextFloat()),
                deathPos.x, deathPos.y + 0.5, deathPos.z, 25, 1.5, 1.2, 1.5, 0.05);
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                deathPos.x, deathPos.y + 0.2, deathPos.z, 20, 1.2, 0.15, 1.2, 0.02);
        serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                deathPos.x, deathPos.y + 0.3, deathPos.z, 15, 1.0, 0.1, 1.0, 0.01);
        serverLevel.sendParticles(ParticleTypes.SOUL,
                deathPos.x, deathPos.y + 0.6, deathPos.z, 12, 1.2, 0.8, 1.2, 0.01);
        serverLevel.sendParticles(new SculkChargeParticleOptions(serverLevel.random.nextFloat()),
                deathPos.x, deathPos.y + 0.1, deathPos.z, 15, 1.6, 0.1, 1.6, 0.03);

        serverLevel.playSound(null, deathPos.x, deathPos.y, deathPos.z,
                SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.PLAYERS,
                0.6f, 0.8f + serverLevel.random.nextFloat() * 0.4f);
    }

    private static boolean isConvertibleAnimal(Entity entity) {
        EntityType<?> type = entity.getType();
        return type == EntityType.PIG
                || type == EntityType.SHEEP
                || type == EntityType.COW
                || type == EntityType.CHICKEN;
    }

    private static boolean isMeatItem(ItemStack stack) {
        return stack.is(Items.PORKCHOP)
                || stack.is(Items.COOKED_PORKCHOP)
                || stack.is(Items.MUTTON)
                || stack.is(Items.COOKED_MUTTON)
                || stack.is(Items.BEEF)
                || stack.is(Items.COOKED_BEEF)
                || stack.is(Items.CHICKEN)
                || stack.is(Items.COOKED_CHICKEN)
                || stack.is(Items.RABBIT)
                || stack.is(Items.COOKED_RABBIT)
                || stack.is(ModItems.EERIE_MEAT.get());
    }

    private static ItemEntity createDrop(Level level, double x, double y, double z, ItemStack stack) {
        ItemEntity drop = new ItemEntity(level, x, y, z, stack);
        drop.setDefaultPickUpDelay();
        return drop;
    }

    private static Holder<MobEffect> sonicWaveCache;

    private static Holder<MobEffect> sonicWave() {
        Holder<MobEffect> holder = sonicWaveCache;
        if (holder == null) {
            holder = ModEffects.SONIC_WAVE.getDelegate();
            sonicWaveCache = holder;
        }
        return holder;
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect().is(ModEffects.SCULK_DASH.getKey())) {
            ModEffects.SculkDash.clearModifiers(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance() != null
                && event.getEffectInstance().getEffect().is(ModEffects.SCULK_DASH.getKey())) {
            ModEffects.SculkDash.clearModifiers(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide) {
            return;
        }

        MobEffectInstance newEffect = event.getEffectInstance();
        if (newEffect == null || !newEffect.getEffect().is(ModEffects.SONIC_WAVE.getKey())) {
            return;
        }

        MobEffectInstance existingEffect = player.getEffect(sonicWave());
        if (existingEffect == null) {
            return;
        }

        int totalAmplifier = Math.min(
                Math.max(existingEffect.getAmplifier(), newEffect.getAmplifier()), 20);
        int totalDuration = Math.max(existingEffect.getDuration(), newEffect.getDuration());

        event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        player.removeEffect(sonicWave());
        player.addEffect(new MobEffectInstance(sonicWave(), totalDuration, totalAmplifier));
    }

    private static final String LAST_MAIN_HAND_KEY = "kaleidoscope_sculk:sonic_last_main_hand";

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        String held = BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString();
        String last = player.getPersistentData().getString(LAST_MAIN_HAND_KEY);
        if (!held.equals(last)) {
            player.getPersistentData().putString(LAST_MAIN_HAND_KEY, held);
            ModPackets.SonicBoom.resetStreak(player);
        }
    }

    private static final float SCULK_BRANCH_DROP_CHANCE = 0.1f;
    private static final float SCULK_CATERPILLAR_DROP_CHANCE = 0.05f;

    @SubscribeEvent
    public static void onSculkVeinInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack handItem = event.getItemStack();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        InteractionHand hand = event.getHand();

        if (!handItem.is(ModItems.KITCHEN_KNIVES)) return;
        if (player.isShiftKeyDown()) return;

        boolean isSculk = state.is(Blocks.SCULK);
        boolean isSculkVein = state.is(Blocks.SCULK_VEIN);
        if (!isSculk && !isSculkVein) return;

        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;

            int fungusCount = isSculkVein ? 1 : (1 + serverLevel.random.nextInt(3));
            for (int i = 0; i < fungusCount; i++) {
                serverLevel.addFreshEntity(createDrop(serverLevel,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        ModItems.SCULK_FUNGUS.get().getDefaultInstance()));
            }

            if (serverLevel.random.nextFloat() < SCULK_BRANCH_DROP_CHANCE) {
                serverLevel.addFreshEntity(createDrop(serverLevel,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        ModItems.SCULK_BRANCH.get().getDefaultInstance()));
            }

            if (isSculkVein && serverLevel.random.nextFloat() < SCULK_CATERPILLAR_DROP_CHANCE) {
                serverLevel.addFreshEntity(createDrop(serverLevel,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        ModItems.SCULK_CATERPILLAR.get().getDefaultInstance()));
            }

            serverLevel.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                EquipmentSlot slot = hand == InteractionHand.MAIN_HAND
                        ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                handItem.hurtAndBreak(2, player, slot);
            }

            serverLevel.destroyBlock(pos, false);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    /**
     * Right-click a black banner with a skull pattern while holding Soul to convert it into a Soul Sail.
     */
    @SubscribeEvent
    public static void onBannerInteract(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.SOUL.get())) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BannerBlock)) return;
        if (!isValidBanner(level, pos)) return;

        if (!level.isClientSide) {
            Player player = event.getEntity();
            level.destroyBlock(pos, false);

            level.addFreshEntity(createDrop(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    new ItemStack(ModItems.SOUL_SAIL.get())));

            level.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static boolean isValidBanner(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof BannerBlockEntity banner)) {
            return false;
        }

        ItemStack bannerItem = banner.getItem();
        if (bannerItem.isEmpty() || !bannerItem.is(Items.BLACK_BANNER)) {
            return false;
        }

        return hasSkullPattern(banner.getPatterns())
                || hasSkullPattern(bannerItem.get(DataComponents.BANNER_PATTERNS));
    }

    private static boolean hasSkullPattern(BannerPatternLayers layers) {
        if (layers == null) {
            return false;
        }

        for (BannerPatternLayers.Layer layer : layers.layers()) {
            ResourceLocation patternLocation = layer.pattern().unwrapKey()
                    .map(key -> key.location()).orElse(null);
            if (patternLocation != null && patternLocation.getPath().contains("skull")) {
                return true;
            }
        }

        return false;
    }
}
