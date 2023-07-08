package moe.plushie.armourers_workshop.init.function;

import com.google.common.collect.ImmutableList;
import moe.plushie.armourers_workshop.api.common.ILootConditionalFunction;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
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
public class SkinRandomlyFunction implements ILootConditionalFunction {

    public final List<SkinSource> sources;

    SkinRandomlyFunction(Collection<SkinSource> sources) {
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

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return new HashSet<>(ObjectUtils.compactMap(sources, SkinSource::getParam));
    }

    public static class SkinSource implements IResultHandler<SkinDescriptor> {

        private SkinDescriptor provider;
        private Function<LootContext, SkinDescriptor> searcher;
        private LootContextParam<?> param;

        public SkinSource(IDataPackObject object) {
            switch (object.type()) {
                case STRING: {
                    // "ks:10830"
                    // "ws:/path/to/skin.armour"
                    String identifier = object.stringValue();
                    if (!identifier.isEmpty()) {
                        // direct load will take a long time, so we need to preload.
                        // during the loading, the current source is disabled.
                        SkinLoader.getInstance().submit(() -> SkinLoader.getInstance().loadSkinFromDB(identifier, ColorScheme.EMPTY, this));
                    }
                    break;
                }
                case DICTIONARY: {
                    // {"type": "any"}              // any type, any slot
                    // {"type": "outfit"}           // in outfit, any slot
                    // {"type": "sword", "slot": 1} // in sword, first slot
                    int slot = object.get("slot").intValue();
                    SkinSlotType slotType = SkinSlotType.of(object.get("type").stringValue());
                    searcher = context -> search(context, slotType, slot);
                    param = LootContextParams.THIS_ENTITY;
                    break;
                }
                default: {
                    // ignore
                    break;
                }
            }
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

    public static class Serializer implements ILootConditionalFunction.Serializer<SkinRandomlyFunction> {

        @Override
        public void serialize(IDataPackObject object, SkinRandomlyFunction function) {
            if (function.sources.isEmpty()) {
                return;
            }
            // TODO: why need serialize @SAGESSE
        }

        @Override
        public SkinRandomlyFunction deserialize(IDataPackObject object) {
            ArrayList<SkinSource> list = new ArrayList<>();
            object.get("skins").allValues().forEach(it -> list.add(new SkinSource(it)));
            return new SkinRandomlyFunction(list);
        }
    }
}
