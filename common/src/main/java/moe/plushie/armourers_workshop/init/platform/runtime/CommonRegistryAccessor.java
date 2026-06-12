package moe.plushie.armourers_workshop.init.platform.runtime;

import moe.plushie.armourers_workshop.compat.api.level.BiomeAccessor;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Predicate;

public interface CommonRegistryAccessor {

    OpenResourceKey getItemKey(Item item);

    OpenResourceKey getBlockKey(Block block);

    OpenResourceKey getEntityTypeKey(EntityType<?> entityType);

    OpenResourceKey getBlockEntityTypeKey(BlockEntityType<?> entityType);


    Predicate<ItemStack> getItemTag(OpenResourceKey key);

    Predicate<BlockState> getBlockTag(OpenResourceKey key);

    Predicate<BiomeAccessor> getBiomeTag(OpenResourceKey key);

    Item getItem(OpenResourceKey key);

    Block getBlock(OpenResourceKey key);

    Function<ItemStack, Integer> getEnchantment(OpenResourceKey key);

    Function<LivingEntity, MobEffectInstance> getEffect(OpenResourceKey key);

    Function<LivingEntity, Double> getAttribute(OpenResourceKey key);
}
