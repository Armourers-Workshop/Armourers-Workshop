package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.level.BiomeAccessor;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonRegistryAccessor;
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
public class AbstractForgeRegistryAccessor implements CommonRegistryAccessor {

    @Override
    public OpenResourceKey getItemKey(Item item) {
        return Registry.findKey(BuiltInRegistries.ITEM, item);
    }

    @Override
    public OpenResourceKey getBlockKey(Block block) {
        return Registry.findKey(BuiltInRegistries.BLOCK, block);
    }

    @Override
    public OpenResourceKey getEntityTypeKey(EntityType<?> entityType) {
        return Registry.findKey(BuiltInRegistries.ENTITY_TYPE, entityType);
    }

    @Override
    public OpenResourceKey getBlockEntityTypeKey(BlockEntityType<?> entityType) {
        return Registry.findKey(BuiltInRegistries.BLOCK_ENTITY_TYPE, entityType);
    }

    @Override
    public Predicate<ItemStack> getItemTag(OpenResourceKey key) {
        var tag = Registry.findTag(Registries.ITEM, key);
        return itemStack -> itemStack.is(tag);
    }

    @Override
    public Predicate<BlockState> getBlockTag(OpenResourceKey key) {
        var tag = Registry.findTag(Registries.BLOCK, key);
        return blockState -> blockState.is(tag);
    }

    @Override
    public Predicate<BiomeAccessor> getBiomeTag(OpenResourceKey key) {
        var tag = Registry.findTag(Registries.BIOME, key);
        return info -> info.getLevel().getBiome(info.getBlockPos()).is(tag);
    }

    @Override
    public Item getItem(OpenResourceKey key) {
        var item = Registry.findValue(Registries.ITEM, key);
        if (item != null) {
            return item.value();
        }
        return null;
    }

    @Override
    public Block getBlock(OpenResourceKey key) {
        var block = Registry.findValue(Registries.BLOCK, key);
        if (block != null) {
            return block.value();
        }
        return null;
    }

    @Override
    public Function<ItemStack, Integer> getEnchantment(OpenResourceKey key) {
        var enchantment = Registry.findValue(Registries.ENCHANTMENT, key);
        if (enchantment != null) {
            return itemStack -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
        }
        return null;
    }

    @Override
    public Function<LivingEntity, MobEffectInstance> getEffect(OpenResourceKey key) {
        var effect = Registry.findValue(Registries.MOB_EFFECT, key);
        if (effect != null) {
            return entity -> entity.getEffect(effect);
        }
        return null;
    }

    @Override
    public Function<LivingEntity, Double> getAttribute(OpenResourceKey key) {
        var attribute = Registry.findValue(Registries.ATTRIBUTE, key);
        if (attribute != null) {
            return entity -> entity.getAttributeValue(attribute);
        }
        return null;
    }
}
