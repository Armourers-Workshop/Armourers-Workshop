package moe.plushie.armourers_workshop.init.function;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moe.plushie.armourers_workshop.api.common.ILootFunction;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * <code>
 * {
 *   "pools": [{
 *     "conditions": [{
 *       "condition": "killed_by_player"
 *     }],
 *     "rolls": 1,
 *     "entries": [{
 *       "type": "item",
 *       "name": "armourers_workshop:skin",
 *       "weight": 1,
 *       "functions": [{
 *         "function": "armourers_workshop:skin_randomly",
 *         "skins": [
 *           "ks:10830", // direct access global skin library
 *           "ws:/path/to/file.armour", // direct access server skin-library
 *           {"type": "any"}, // any type, any slot
 *           {"type": "outfit"}, // in outfit, any slot
 *           {"type": "sword", "slot": 1} // in sword, first slot
 *         ]
 *       }]
 *     }]
 *   }]
 * }
 * </code>
 */
public class SkinRandomlyFunction implements ILootFunction {

    public static final Codec<SkinRandomlyFunction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkinSource.CODEC.listOf().fieldOf("skins").forGetter(SkinRandomlyFunction::getSources)
    ).apply(instance, SkinRandomlyFunction::new));

    public final List<SkinSource> sources;

    public SkinRandomlyFunction(Collection<SkinSource> sources) {
        this.sources = ImmutableList.copyOf(sources);
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        SkinDescriptor descriptor = SkinDescriptor.EMPTY;

        // random find all provider.
        ArrayList<SkinSource> pending = new ArrayList<>(sources);
        while (descriptor.isEmpty() && !pending.isEmpty()) {
            int index = lootContext.getRandom().nextInt(pending.size());
            descriptor = pending.remove(index).apply(lootContext);
        }

        // we can't found valid skin, abort the loot function.
        if (descriptor.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // attach the skin to the item stack.
        SkinDescriptor.setDescriptor(itemStack, descriptor);
        return itemStack;
    }

    public List<SkinSource> getSources() {
        return sources;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return new HashSet<>(ObjectUtils.compactMap(sources, SkinSource::getParam));
    }

    public static class SkinSource implements IResultHandler<SkinDescriptor> {

        public static final Codec<SkinSource> CODEC = new Codec<SkinSource>() {

            final Codec<SkinSource> simple = Codec.STRING.xmap(SkinSource::new, it -> null);
            final Codec<SkinSource> complex = RecordCodecBuilder.create(instance -> instance.group(
                    SkinSlotType.CODEC.fieldOf("type").forGetter(it -> null),
                    Codec.INT.optionalFieldOf("slot", 0).forGetter(it -> 0)
            ).apply(instance, SkinSource::new));

            @Override
            public <T> DataResult<Pair<SkinSource, T>> decode(DynamicOps<T> ops, T input) {
                DataResult<Pair<SkinSource, T>> result = simple.decode(ops, input);
                if (result.result().isPresent()) {
                    return result;
                }
                return complex.decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(SkinSource input, DynamicOps<T> ops, T prefix) {
                throw new RuntimeException("why you needs serializer?");
            }
        };

        private SkinDescriptor provider;
        private Function<LootContext, SkinDescriptor> searcher;
        private LootContextParam<?> param;

        public SkinSource(String identifier) {
            // "ks:10830"
            // "ws:/path/to/skin.armour"
            if (!identifier.isEmpty()) {
                // direct load will take a long time, so we need to preload.
                // during the loading, the current source is disabled.
                SkinLoader.getInstance().submit(() -> SkinLoader.getInstance().loadSkinFromDB(identifier, ColorScheme.EMPTY, this));
            }
        }

        public SkinSource(SkinSlotType slotType, int slot) {
            // {"type": "any"}              // any type, any slot
            // {"type": "outfit"}           // in outfit, any slot
            // {"type": "sword", "slot": 1} // in sword, first slot
            searcher = context -> search(context, slotType, slot);
            param = LootContextParams.THIS_ENTITY;
        }

        public SkinDescriptor apply(LootContext lootContext) {
            if (provider != null) {
                return provider;
            }
            if (searcher != null) {
                return searcher.apply(lootContext);
            }
            return SkinDescriptor.EMPTY;
        }

        @Override
        public void apply(SkinDescriptor value, Exception exception) {
            provider = value;
        }

        public SkinDescriptor search(LootContext lootContext, @Nullable SkinSlotType slotType, int index) {
            Object value = lootContext.getParamOrNull(param);
            SkinWardrobe wardrobe = SkinWardrobe.of(ObjectUtils.safeCast(value, Entity.class));
            if (wardrobe == null) {
                return SkinDescriptor.EMPTY;
            }
            // collect all available skin items.
            SkinDescriptor descriptor = SkinDescriptor.EMPTY;
            ArrayList<ItemStack> pending = collect(wardrobe, slotType, index);
            while (descriptor.isEmpty() && !pending.isEmpty()) {
                index = lootContext.getRandom().nextInt(pending.size());
                descriptor = SkinDescriptor.of(pending.remove(index));
            }
            return descriptor;
        }

        public ArrayList<ItemStack> collect(SkinWardrobe wardrobe, @Nullable SkinSlotType slotType, int index) {
            // when slot type not specified by user, we will search all slot.
            ArrayList<ItemStack> results = new ArrayList<>();
            if (slotType == null) {
                for (SkinSlotType slotType1 : SkinSlotType.values()) {
                    if (slotType1.getSkinType() != null) {
                        results.addAll(collect(wardrobe, slotType1, index));
                    }
                }
                return results;
            }
            // a specified slot by user.
            if (index != 0) {
                results.add(wardrobe.getItem(slotType, index));
                return results;
            }
            // add all skins to the pending list.
            int count = wardrobe.getUnlockedSize(slotType);
            for (int i = 0; i < count; ++i) {
                results.add(wardrobe.getItem(slotType, i));
            }
            return results;
        }

        public LootContextParam<?> getParam() {
            return param;
        }
    }
}
