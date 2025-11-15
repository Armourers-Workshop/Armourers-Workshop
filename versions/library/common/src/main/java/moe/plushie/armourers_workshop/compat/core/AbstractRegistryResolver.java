package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Available("[1.21, )")
public class AbstractRegistryResolver {

    private static final Map<String, Optional<TagKey<Item>>> NAMED_ITEM_TAGS = new ConcurrentHashMap<>();
    private static final Map<String, Optional<TagKey<Block>>> NAMED_BLOCK_TAGS = new ConcurrentHashMap<>();
    private static final Map<String, Optional<TagKey<Biome>>> NAMED_BIOME_TAGS = new ConcurrentHashMap<>();

    private static final Map<String, Optional<? extends Holder<Attribute>>> NAMED_ATTRIBUTES = new ConcurrentHashMap<>();
    private static final Map<String, Optional<? extends Holder<MobEffect>>> NAMED_EFFECTS = new ConcurrentHashMap<>();
    private static final Map<String, Optional<? extends Holder<Enchantment>>> NAMED_ENCHANTMENTS = new ConcurrentHashMap<>();

    public static String getItemKey(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    public static String getBlockKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }


    public static boolean hasItemTag(ItemStack itemStack, String tagName) {
        var tag = NAMED_ITEM_TAGS.computeIfAbsent(tagName, it -> findTag(it, Registries.ITEM));
        return tag.map(itemStack::is).orElse(false);
    }

    public static boolean hasBlockTag(BlockState blockState, String tagName) {
        var tag = NAMED_BLOCK_TAGS.computeIfAbsent(tagName, it -> findTag(it, Registries.BLOCK));
        return tag.map(blockState::is).orElse(false);
    }

    public static boolean hasBiomeTag(Object biome, String tagName) {
        Holder<Biome> biome1 = Objects.unsafeCast(biome);
        var tag = NAMED_BIOME_TAGS.computeIfAbsent(tagName, it -> findTag(it, Registries.BIOME));
        return tag.map(biome1::is).orElse(false);
    }

    @Nullable
    public static Object getBiome(Level level, BlockPos blockPos) {
        return level.getBiome(blockPos);
    }

    @Nullable
    public static MobEffectInstance getEffect(LivingEntity entity, String effectName) {
        var value = NAMED_EFFECTS.computeIfAbsent(effectName, it -> findValue(it, Registries.MOB_EFFECT));
        return value.map(entity::getEffect).orElse(null);
    }

    public static double getAttribute(LivingEntity entity, String attributeName) {
        var value = NAMED_ATTRIBUTES.computeIfAbsent(attributeName, it -> findValue(it, Registries.ATTRIBUTE));
        return value.map(entity::getAttributeValue).orElse(0.0);
    }

    public static Object getEnchantment(ItemStack itemStack, String enchantmentName) {
        var value = NAMED_ENCHANTMENTS.computeIfAbsent(enchantmentName, it -> findValue(it, Registries.ENCHANTMENT));
        return value.map(it -> EnchantmentHelper.getItemEnchantmentLevel(it, itemStack)).orElse(null);
    }


    private static <T> Optional<TagKey<T>> findTag(String name, ResourceKey<? extends Registry<T>> resourceKey) {
        var key = ResourceLocation.tryParse(name);
        if (key != null) {
            return Optional.of(TagKey.create(resourceKey, key));
        }
        return Optional.empty();
    }

    private static <T> Optional<? extends Holder<T>> findValue(String name, ResourceKey<? extends Registry<T>> resourceKey) {
        var key = ResourceLocation.tryParse(name);
        if (key != null) {
            var registryProvider = AbstractRegistryProvider.from(resourceKey);
            if (registryProvider != null) {
                return registryProvider.get(key);
            }
        }
        return Optional.empty();
    }
}
