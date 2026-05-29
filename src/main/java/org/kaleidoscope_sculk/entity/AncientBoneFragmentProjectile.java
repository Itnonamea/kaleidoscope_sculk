package org.kaleidoscope_sculk.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.kaleidoscope_sculk.register.ModEffects;
import org.kaleidoscope_sculk.register.ModEntities;
import org.kaleidoscope_sculk.register.ModItems;

public class AncientBoneFragmentProjectile extends ThrowableItemProjectile {

    private static final float DAMAGE = 4.0f;
    private boolean hasDropped = false;

    public AncientBoneFragmentProjectile(EntityType<? extends AncientBoneFragmentProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public AncientBoneFragmentProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.ANCIENT_BONE_FRAGMENT_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ANCIENT_BONE_FRAGMENT.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();

        if (!this.level().isClientSide) {
            DamageSource damageSource = this.damageSources().thrown(this, owner);
            target.hurt(damageSource, DAMAGE);

            if (target instanceof LivingEntity livingTarget && owner instanceof LivingEntity) {
                double dx = target.getX() - owner.getX();
                double dz = target.getZ() - owner.getZ();
                livingTarget.knockback(0.5f, dx, dz);
            }
        }

        // 击中实体后生成掉落物
        spawnDropItem();

        this.discard();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (!hasDropped) {
                spawnDropItem();
            }
            this.discard();
        }
    }

    // 生成掉落物
    private void spawnDropItem() {
        if (hasDropped) return;
        hasDropped = true;

        ItemStack dropStack = new ItemStack(ModItems.ANCIENT_BONE_FRAGMENT.get(), 1);
        ItemEntity itemEntity = new ItemEntity(
                this.level(),
                this.getX(),
                this.getY(),
                this.getZ(),
                dropStack
        );
        itemEntity.setDefaultPickUpDelay();
        this.level().addFreshEntity(itemEntity);
    }
}