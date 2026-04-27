package moe.plushie.armourers_workshop.init.function;

import moe.plushie.armourers_workshop.api.common.IContextKey;
import moe.plushie.armourers_workshop.api.common.ILootContext;
import moe.plushie.armourers_workshop.api.common.ILootItemFunction;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.compat.core.AbstractLootContextParams;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * <pre>
 * {
 *  "pools": [{
 *   "conditions": [{
 *    "condition": "killed_by_player"
 *   }],
 *   "rolls": 1,
 *   "entries": [{
 *    "type": "item",
 *    "name": "armourers_workshop:skin",
 *    "weight": 1,
 *    "functions": [{
 *     "function": "armourers_workshop:skin_randomly",
 *     "skins": [
 *      "ks:10830", // direct access global skin library
 *      "ws:/path/to/file.armour", // direct access server skin-library
 *      {"type": "any"}, // any type, any slot
 *      {"type": "outfit"}, // in outfit, any slot
 *      {"type": "sword", "slot": 1} // in sword, first slot
 *     ]
 *    }]
 *   }]
 *  }]
 * }
 * </pre>
 */
public class SkinRandomlyFunction implements ILootItemFunction {

    public static final IDataMapCodec<SkinRandomlyFunction> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(SkinSource.COMPLEX_CODEC.listOf().fieldOf("skins").forGetter(SkinRandomlyFunction::sources)).apply(instance, SkinRandomlyFunction::new));

    private final List<SkinSource> sources;

    public SkinRandomlyFunction(Collection<SkinSource> sources) {
        this.sources = Collections.newList(sources);
    }

    @Override
    public ItemStack apply(ItemStack itemStack, ILootContext context) {
        var descriptor = SkinDescriptor.EMPTY;

        // random find all provider.
        var pending = new ArrayList<>(sources);
        while (descriptor.isEmpty() && !pending.isEmpty()) {
            int index = context.randomSource().nextInt(pending.size());
            descriptor = pending.remove(index).apply(context);
        }

        // we can't found valid skin, abort the loot function.
        if (descriptor.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // attach the skin to the item stack.
        itemStack.set(ModDataComponents.SKIN.get(), descriptor);
        return itemStack;
    }

    public List<SkinSource> sources() {
        return sources;
    }

    @Override
    public Set<? extends IContextKey<?>> getReferencedContextParams() {
        return new HashSet<>(Collections.compactMap(sources, SkinSource::param));
    }

    public static class SkinSource implements IResultHandler<SkinDescriptor> {

        public static final IDataCodec<SkinSource> PARSE_CODEC = IDataCodec.STRING.xmap(SkinSource::new, it -> null);
        public static final IDataCodec<SkinSource> SIMPLE_CODEC = IDataCodec.create(instance -> instance.group(SkinSlotType.CODEC.fieldOf("type").forGetter(SkinSource::slotType), IDataCodec.INT.optionalFieldOf("slot", 0).forGetter(SkinSource::slot)).apply(instance, SkinSource::new));
        public static final IDataCodec<SkinSource> COMPLEX_CODEC = IDataCodec.either(PARSE_CODEC, SIMPLE_CODEC).xmap(it -> it.map(it1 -> it1, it1 -> it1), it -> null);

        private SkinDescriptor provider;
        private Function<ILootContext, SkinDescriptor> searcher;
        private IContextKey<?> param;

        public SkinSource(String identifier) {
            // "ks:10830"
            // "ws:/path/to/skin.armour"
            if (!identifier.isEmpty()) {
                // direct load will take a long time, so we need to preload.
                // during the loading, the current source is disabled.
                SkinLoader.getInstance().submit(() -> SkinLoader.getInstance().loadSkinFromDB(identifier, SkinPaintScheme.EMPTY, this));
            }
        }

        public SkinSource(SkinSlotType slotType, int slot) {
            // {"type": "any"}              // any type, any slot
            // {"type": "outfit"}           // in outfit, any slot
            // {"type": "sword", "slot": 1} // in sword, first slot
            searcher = context -> search(context, slotType, slot);
            param = AbstractLootContextParams.THIS_ENTITY;
        }

        public SkinDescriptor apply(ILootContext context) {
            if (provider != null) {
                return provider;
            }
            if (searcher != null) {
                return searcher.apply(context);
            }
            return SkinDescriptor.EMPTY;
        }

        @Override
        public void apply(SkinDescriptor value, Throwable exception) {
            provider = value;
        }

        public SkinDescriptor search(ILootContext context, @Nullable SkinSlotType slotType, int index) {
            var value = context.getOptionalParameter(param);
            var wardrobe = SkinWardrobe.of(Objects.safeCast(value, Entity.class));
            if (wardrobe == null) {
                return SkinDescriptor.EMPTY;
            }
            // collect all available skin items.
            var descriptor = SkinDescriptor.EMPTY;
            var pending = collect(wardrobe, slotType, index);
            while (descriptor.isEmpty() && !pending.isEmpty()) {
                index = context.randomSource().nextInt(pending.size());
                descriptor = SkinDescriptor.of(pending.remove(index));
            }
            return descriptor;
        }

        public ArrayList<ItemStack> collect(SkinWardrobe wardrobe, @Nullable SkinSlotType slotType, int index) {
            // when slot type not specified by user, we will search all slot.
            var results = new ArrayList<ItemStack>();
            if (slotType == null) {
                for (var slotType1 : SkinSlotType.values()) {
                    if (slotType1.skinType() != null) {
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
            var count = wardrobe.getUnlockedSize(slotType);
            for (int i = 0; i < count; ++i) {
                results.add(wardrobe.getItem(slotType, i));
            }
            return results;
        }

        public IContextKey<?> param() {
            return param;
        }

        public int slot() {
            return 0;
        }

        public SkinSlotType slotType() {
            return null;
        }
    }
}
