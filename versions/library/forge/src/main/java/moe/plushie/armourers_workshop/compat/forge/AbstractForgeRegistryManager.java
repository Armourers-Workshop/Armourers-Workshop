package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Predicate;

@Available("[1.21, )")
public class AbstractForgeRegistryManager extends AbstractRegistryManager {

    public static final AbstractForgeRegistryManager INSTANCE = new AbstractForgeRegistryManager();

    @Override
    protected ResourceLocation getItemKey0(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    @Override
    protected ResourceLocation getBlockKey0(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    @Override
    protected Predicate<ItemStack> getItemTag0(ResourceLocation key) {
        var tag = TagKey.create(Registries.ITEM, key);
        return itemStack -> itemStack.is(tag);
    }

    @Override
    protected Predicate<BlockState> getBlockTag0(ResourceLocation key) {
        var tag = TagKey.create(Registries.BLOCK, key);
        return blockState -> blockState.is(tag);
    }

    @Override
    protected Predicate<Biome> getBiomeTag0(ResourceLocation key) {
        var tag = TagKey.create(Registries.BIOME, key);
        return info -> info.getLevel().getBiome(info.getBlockPos()).is(tag);
    }

    @Override
    protected Item getItem0(ResourceLocation key) {
        var item = getHolder0(Registries.ITEM, key);
        if (item != null) {
            return item.value();
        }
        return null;
    }

    @Override
    protected Block getBlock0(ResourceLocation key) {
        var block = getHolder0(Registries.BLOCK, key);
        if (block != null) {
            return block.value();
        }
        return null;
    }

    @Override
    protected Function<ItemStack, Integer> getEnchantment0(ResourceLocation key) {
        var enchantment = getHolder0(Registries.ENCHANTMENT, key);
        if (enchantment != null) {
            return itemStack -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
        }
        return null;
    }

    @Override
    protected Function<LivingEntity, MobEffectInstance> getEffect0(ResourceLocation key) {
        var effect = getHolder0(Registries.MOB_EFFECT, key);
        if (effect != null) {
            return entity -> entity.getEffect(effect);
        }
        return null;
    }

    @Override
    protected Function<LivingEntity, Double> getAttribute0(ResourceLocation key) {
        var attribute = getHolder0(Registries.ATTRIBUTE, key);
        if (attribute != null) {
            return entity -> entity.getAttributeValue(attribute);
        }
        return null;
    }

    protected <E> Holder<E> getHolder0(ResourceKey<? extends Registry<? extends E>> registryKey, ResourceLocation rl) {
        var registryProvider = AbstractRegistryProvider.from(registryKey);
        if (registryProvider != null) {
            return registryProvider.get(rl).orElse(null);
        }
        return null;
    }
}
