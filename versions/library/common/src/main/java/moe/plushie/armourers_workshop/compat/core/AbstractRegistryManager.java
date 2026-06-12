package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.compat.api.level.BiomeAccessor;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.platform.Platform;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonRegistryAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractRegistryManager {

    private static final Map<Item, String> ITEM_NAMES = new ConcurrentHashMap<>();
    private static final Map<Block, String> BLOCK_NAMES = new ConcurrentHashMap<>();

    private static final Map<EntityType<?>, String> ENTITY_TYPE_NAMES = new ConcurrentHashMap<>();
    private static final Map<BlockEntityType<?>, String> BLOCK_ENTITY_TYPE_NAMES = new ConcurrentHashMap<>();

    private static final Map<String, Optional<Item>> NAMED_ITEMS = new ConcurrentHashMap<>();
    private static final Map<String, Optional<Block>> NAMED_BLOCKS = new ConcurrentHashMap<>();

    private static final Map<String, Optional<Predicate<ItemStack>>> NAMED_ITEM_TAGS = new ConcurrentHashMap<>();
    private static final Map<String, Optional<Predicate<BlockState>>> NAMED_BLOCK_TAGS = new ConcurrentHashMap<>();
    private static final Map<String, Optional<Predicate<BiomeAccessor>>> NAMED_BIOME_TAGS = new ConcurrentHashMap<>();

    private static final Map<String, Optional<Function<LivingEntity, Double>>> NAMED_ATTRIBUTES = new ConcurrentHashMap<>();
    private static final Map<String, Optional<Function<LivingEntity, MobEffectInstance>>> NAMED_EFFECTS = new ConcurrentHashMap<>();

    private static final Map<String, Optional<Function<ItemStack, Integer>>> NAMED_ENCHANTMENTS = new ConcurrentHashMap<>();

    public static String getItemKey(Item item) {
        return ITEM_NAMES.computeIfAbsent(item, it -> find(it, CommonRegistryAccessor::getItemKey));
    }

    public static String getBlockKey(Block block) {
        return BLOCK_NAMES.computeIfAbsent(block, it -> find(it, CommonRegistryAccessor::getBlockKey));
    }

    public static String getEntityTypeKey(EntityType<?> entityType) {
        return ENTITY_TYPE_NAMES.computeIfAbsent(entityType, it -> find(it, CommonRegistryAccessor::getEntityTypeKey));
    }

    public static String getBlockEntityTypeKey(BlockEntityType<?> entityType) {
        return BLOCK_ENTITY_TYPE_NAMES.computeIfAbsent(entityType, it -> find(it, CommonRegistryAccessor::getBlockEntityTypeKey));
    }


    public static boolean hasItemTag(ItemStack itemStack, String tagName) {
        var tag = NAMED_ITEM_TAGS.computeIfAbsent(tagName, it -> parse(it, CommonRegistryAccessor::getItemTag));
        return tag.map(it -> it.test(itemStack)).orElse(false);
    }

    public static boolean hasBlockTag(BlockState blockState, String tagName) {
        var tag = NAMED_BLOCK_TAGS.computeIfAbsent(tagName, it -> parse(it, CommonRegistryAccessor::getBlockTag));
        return tag.map(it -> it.test(blockState)).orElse(false);
    }

    public static boolean hasBiomeTag(BiomeAccessor biome, String tagName) {
        var tag = NAMED_BIOME_TAGS.computeIfAbsent(tagName, it -> parse(it, CommonRegistryAccessor::getBiomeTag));
        return tag.map(it -> it.test(biome)).orElse(false);
    }

    @Nullable
    public static Item getItem(String registryName) {
        return NAMED_ITEMS.computeIfAbsent(registryName, it -> parse(it, CommonRegistryAccessor::getItem)).orElse(null);
    }

    @Nullable
    public static Block getBlock(String registryName) {
        return NAMED_BLOCKS.computeIfAbsent(registryName, it -> parse(it, CommonRegistryAccessor::getBlock)).orElse(null);
    }

    public static BiomeAccessor getBiome(Level level, BlockPos blockPos) {
        return new BiomeAccessor() {
            @Override
            public Level getLevel() {
                return level;
            }

            @Override
            public BlockPos getBlockPos() {
                return blockPos;
            }
        };
    }

    @Nullable
    public static MobEffectInstance getEffect(LivingEntity entity, String effectName) {
        var value = NAMED_EFFECTS.computeIfAbsent(effectName, it -> parse(it, CommonRegistryAccessor::getEffect));
        return value.map(it -> it.apply(entity)).orElse(null);
    }

    public static double getAttribute(LivingEntity entity, String attributeName) {
        var value = NAMED_ATTRIBUTES.computeIfAbsent(attributeName, it -> parse(it, CommonRegistryAccessor::getAttribute));
        return value.map(it -> it.apply(entity)).orElse(0.0);
    }

    public static Object getEnchantment(ItemStack itemStack, String enchantmentName) {
        var value = NAMED_ENCHANTMENTS.computeIfAbsent(enchantmentName, it -> parse(it, CommonRegistryAccessor::getEnchantment));
        return value.map(it -> it.apply(itemStack)).orElse(null);
    }

    private static <T> String find(T value, BiFunction<CommonRegistryAccessor, T, OpenResourceKey> transformer) {
        var key = transformer.apply(Platform.get().common().registryAccess(), value);
        return key.toString();
    }

    private static <T> Optional<T> parse(String name, BiFunction<CommonRegistryAccessor, OpenResourceKey, T> factory) {
        try {
            var key = OpenResourceKey.parse(name);
            return Optional.ofNullable(factory.apply(Platform.get().common().registryAccess(), key));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
