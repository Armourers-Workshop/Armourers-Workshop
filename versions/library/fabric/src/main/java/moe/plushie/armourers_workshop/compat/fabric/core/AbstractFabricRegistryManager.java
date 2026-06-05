package moe.plushie.armourers_workshop.compat.fabric.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Predicate;

@Available("[21, )")
public class AbstractFabricRegistryManager extends AbstractRegistryManager {

    public static final AbstractFabricRegistryManager INSTANCE = new AbstractFabricRegistryManager();

    @Override
    protected OpenResourceKey getItemKey0(Item item) {
        return Registry.findKey(BuiltInRegistries.ITEM, item);
    }

    @Override
    protected OpenResourceKey getBlockKey0(Block block) {
        return Registry.findKey(BuiltInRegistries.BLOCK, block);
    }

    @Override
    protected OpenResourceKey getEntityTypeKey0(EntityType<?> entityType) {
        return Registry.findKey(BuiltInRegistries.ENTITY_TYPE, entityType);
    }

    @Override
    protected OpenResourceKey getBlockEntityTypeKey0(BlockEntityType<?> entityType) {
        return Registry.findKey(BuiltInRegistries.BLOCK_ENTITY_TYPE, entityType);
    }

    @Override
    protected Predicate<ItemStack> getItemTag0(OpenResourceKey key) {
        var tag = Registry.findTag(Registries.ITEM, key);
        return itemStack -> itemStack.is(tag);
    }

    @Override
    protected Predicate<BlockState> getBlockTag0(OpenResourceKey key) {
        var tag = Registry.findTag(Registries.BLOCK, key);
        return blockState -> blockState.is(tag);
    }

    @Override
    protected Predicate<Biome> getBiomeTag0(OpenResourceKey key) {
        var tag = Registry.findTag(Registries.BIOME, key);
        return info -> info.getLevel().getBiome(info.getBlockPos()).is(tag);
    }

    @Override
    protected Item getItem0(OpenResourceKey key) {
        var item = Registry.findValue(Registries.ITEM, key);
        if (item != null) {
            return item.value();
        }
        return null;
    }

    @Override
    protected Block getBlock0(OpenResourceKey key) {
        var block = Registry.findValue(Registries.BLOCK, key);
        if (block != null) {
            return block.value();
        }
        return null;
    }

    @Override
    protected Function<ItemStack, Integer> getEnchantment0(OpenResourceKey key) {
        var enchantment = Registry.findValue(Registries.ENCHANTMENT, key);
        if (enchantment != null) {
            return itemStack -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
        }
        return null;
    }

    @Override
    protected Function<LivingEntity, MobEffectInstance> getEffect0(OpenResourceKey key) {
        var effect = Registry.findValue(Registries.MOB_EFFECT, key);
        if (effect != null) {
            return entity -> entity.getEffect(effect);
        }
        return null;
    }

    @Override
    protected Function<LivingEntity, Double> getAttribute0(OpenResourceKey key) {
        var attribute = Registry.findValue(Registries.ATTRIBUTE, key);
        if (attribute != null) {
            return entity -> entity.getAttributeValue(attribute);
        }
        return null;
    }
}
