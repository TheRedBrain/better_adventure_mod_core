package com.github.theredbrain.betteradventuremode.mixin.entity.damage;

import com.github.theredbrain.betteradventuremode.BetterAdventureMode;
import com.github.theredbrain.betteradventuremode.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.betteradventuremode.item.BasicWeaponItem;
import com.github.theredbrain.betteradventuremode.entity.damage.DuckDamageSourcesMixin;
import com.github.theredbrain.betteradventuremode.registry.DamageTypesRegistry;
import com.github.theredbrain.betteradventuremode.registry.StatusEffectsRegistry;
import com.github.theredbrain.betteradventuremode.registry.Tags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.EntityTypeTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSources.class)
public abstract class DamageSourcesMixin implements DuckDamageSourcesMixin {

    @Shadow public abstract DamageSource create(RegistryKey<DamageType> key);

    @Shadow public abstract DamageSource create(RegistryKey<DamageType> key, @Nullable Entity attacker);

    /**
     * @author TheRedBrain
     */
    @Inject(method = "mobAttack", at = @At("HEAD"), cancellable = true)
    public void betteradventuremode$mobAttack(LivingEntity attacker, CallbackInfoReturnable<DamageSource> cir) {
        if (!BetterAdventureMode.serverConfig.useVanillaDamageCalculation) {
            ItemStack mainHandStack = attacker.getMainHandStack();
            if (mainHandStack.getItem() instanceof BasicWeaponItem) {
                cir.setReturnValue(this.create(((BasicWeaponItem) mainHandStack.getItem()).getDamageTypeRegistryKey(false), attacker));
            } else {
                if (attacker.getType().isIn(Tags.ATTACKS_WITH_BASHING)) {
                    cir.setReturnValue(this.create(DamageTypesRegistry.MOB_BASHING_DAMAGE_TYPE, attacker));
                    cir.cancel();
                } else if (attacker.getType().isIn(Tags.ATTACKS_WITH_PIERCING)) {
                    cir.setReturnValue(this.create(DamageTypesRegistry.MOB_PIERCING_DAMAGE_TYPE, attacker));
                    cir.cancel();
                } else if (attacker.getType().isIn(Tags.ATTACKS_WITH_SLASHING)) {
                    cir.setReturnValue(this.create(DamageTypesRegistry.MOB_SLASHING_DAMAGE_TYPE, attacker));
                    cir.cancel();
                }
            }
        }
    }

    /**
     * @author TheRedBrain
     */
    @Inject(method = "playerAttack", at = @At("HEAD"), cancellable = true)
    public void betteradventuremode$playerAttack(PlayerEntity attacker, CallbackInfoReturnable<DamageSource> cir) {
        if (!BetterAdventureMode.serverConfig.useVanillaDamageCalculation) {
            ItemStack mainHandStack = attacker.getMainHandStack();
            if (mainHandStack.getItem() instanceof BasicWeaponItem) {
                cir.setReturnValue(this.create(DamageTypesRegistry.getPlayerVariant(((BasicWeaponItem) mainHandStack.getItem()).getDamageTypeRegistryKey(!((DuckPlayerEntityMixin) attacker).betteradventuremode$isMainHandStackSheathed() && ((DuckPlayerEntityMixin) attacker).betteradventuremode$isOffHandStackSheathed())), attacker));
                cir.cancel();
            }
        }
    }

    @Override
    public DamageSource betteradventuremode$bleeding() {
        return this.create(DamageTypesRegistry.BLEEDING_DAMAGE_TYPE);
    }

    @Override
    public DamageSource betteradventuremode$burning() {
        return this.create(DamageTypesRegistry.BURNING_DAMAGE_TYPE);
    }

    @Override
    public DamageSource betteradventuremode$poison() {
        return this.create(DamageTypesRegistry.POISON_DAMAGE_TYPE);
    }

    @Override
    public DamageSource betteradventuremode$shocked() {
        return this.create(DamageTypesRegistry.SHOCKED_DAMAGE_TYPE);
    }

    @Override
    public DamageSource betteradventuremode$lava() {
        return this.create(DamageTypesRegistry.LAVA_DAMAGE_TYPE);
    }

    @Override
    public DamageSource betteradventuremode$bloodMagicCasting() {
        return this.create(DamageTypesRegistry.BLOOD_MAGIC_CASTING_DAMAGE_TYPE);
    }
}
