package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
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
    private static final Map<String, Optional<Predicate<Biome>>> NAMED_BIOME_TAGS = new ConcurrentHashMap<>();

    private static final Map<String, Optional<Function<LivingEntity, Double>>> NAMED_ATTRIBUTES = new ConcurrentHashMap<>();
    private static final Map<String, Optional<Function<LivingEntity, MobEffectInstance>>> NAMED_EFFECTS = new ConcurrentHashMap<>();

    private static final Map<String, Optional<Function<ItemStack, Integer>>> NAMED_ENCHANTMENTS = new ConcurrentHashMap<>();

    public static String getItemKey(Item item) {
        return ITEM_NAMES.computeIfAbsent(item, it -> EnvironmentManager.getRegistryManager().getItemKey0(it).toString());
    }

    public static String getBlockKey(Block block) {
        return BLOCK_NAMES.computeIfAbsent(block, it -> EnvironmentManager.getRegistryManager().getBlockKey0(it).toString());
    }

    public static String getEntityTypeKey(EntityType<?> entityType) {
        return ENTITY_TYPE_NAMES.computeIfAbsent(entityType, it -> EnvironmentManager.getRegistryManager().getEntityTypeKey0(it).toString());
    }

    public static String getBlockEntityTypeKey(BlockEntityType<?> entityType) {
        return BLOCK_ENTITY_TYPE_NAMES.computeIfAbsent(entityType, it -> EnvironmentManager.getRegistryManager().getBlockEntityTypeKey0(it).toString());
    }


    public static boolean hasItemTag(ItemStack itemStack, String tagName) {
        var tag = NAMED_ITEM_TAGS.computeIfAbsent(tagName, it -> parse(it, AbstractRegistryManager::getItemTag0));
        return tag.map(it -> it.test(itemStack)).orElse(false);
    }

    public static boolean hasBlockTag(BlockState blockState, String tagName) {
        var tag = NAMED_BLOCK_TAGS.computeIfAbsent(tagName, it -> parse(it, AbstractRegistryManager::getBlockTag0));
        return tag.map(it -> it.test(blockState)).orElse(false);
    }

    public static boolean hasBiomeTag(Biome biome, String tagName) {
        var tag = NAMED_BIOME_TAGS.computeIfAbsent(tagName, it -> parse(it, AbstractRegistryManager::getBiomeTag0));
        return tag.map(it -> it.test(biome)).orElse(false);
    }

    @Nullable
    public static Item getItem(String registryName) {
        return NAMED_ITEMS.computeIfAbsent(registryName, it -> parse(it, AbstractRegistryManager::getItem0)).orElse(null);
    }

    @Nullable
    public static Block getBlock(String registryName) {
        return NAMED_BLOCKS.computeIfAbsent(registryName, it -> parse(it, AbstractRegistryManager::getBlock0)).orElse(null);
    }

    public static Biome getBiome(Level level, BlockPos blockPos) {
        return new Biome() {
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
        var value = NAMED_EFFECTS.computeIfAbsent(effectName, it -> parse(it, AbstractRegistryManager::getEffect0));
        return value.map(it -> it.apply(entity)).orElse(null);
    }

    public static double getAttribute(LivingEntity entity, String attributeName) {
        var value = NAMED_ATTRIBUTES.computeIfAbsent(attributeName, it -> parse(it, AbstractRegistryManager::getAttribute0));
        return value.map(it -> it.apply(entity)).orElse(0.0);
    }

    public static Object getEnchantment(ItemStack itemStack, String enchantmentName) {
        var value = NAMED_ENCHANTMENTS.computeIfAbsent(enchantmentName, it -> parse(it, AbstractRegistryManager::getEnchantment0));
        return value.map(it -> it.apply(itemStack)).orElse(null);
    }

    private static <T> Optional<T> parse(String name, BiFunction<AbstractRegistryManager, OpenResourceKey, T> factory) {
        try {
            var key = OpenResourceKey.parse(name);
            return Optional.ofNullable(factory.apply(EnvironmentManager.getRegistryManager(), key));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    protected abstract OpenResourceKey getItemKey0(Item item);

    protected abstract OpenResourceKey getBlockKey0(Block block);

    protected abstract OpenResourceKey getEntityTypeKey0(EntityType<?> entityType);

    protected abstract OpenResourceKey getBlockEntityTypeKey0(BlockEntityType<?> entityType);


    protected abstract Predicate<ItemStack> getItemTag0(OpenResourceKey key);

    protected abstract Predicate<BlockState> getBlockTag0(OpenResourceKey key);

    protected abstract Predicate<Biome> getBiomeTag0(OpenResourceKey key);

    protected abstract Item getItem0(OpenResourceKey key);

    protected abstract Block getBlock0(OpenResourceKey key);

    protected abstract Function<ItemStack, Integer> getEnchantment0(OpenResourceKey key);

    protected abstract Function<LivingEntity, MobEffectInstance> getEffect0(OpenResourceKey key);

    protected abstract Function<LivingEntity, Double> getAttribute0(OpenResourceKey key);


    public interface Biome {

        Level getLevel();

        BlockPos getBlockPos();
    }
}
