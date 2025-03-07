package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.common.IItemStackProvider;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ItemStackProvider implements IItemStackProvider {

    private static final List<ItemStack> DEFAULT_EMPTY_LIST = Collections.emptyList();
    private static final ItemStackProvider DEFAULT_ITEM_PROVIDER = new ItemStackProvider();

    private final List<IItemStackProvider> itemProviders = Collections.newList(new VanillaItemStackProvider());

    public static ItemStackProvider getInstance() {
        return DEFAULT_ITEM_PROVIDER;
    }

    public void register(IItemStackProvider itemProvider) {
        itemProviders.add(itemProvider);
    }


    @Override
    public Iterable<ItemStack> getArmorSlots(Entity entity) {
        Iterable<ItemStack> allArmourSlots = DEFAULT_EMPTY_LIST;
        for (var itemProvider : itemProviders) {
            Iterable<ItemStack> armorSlots = itemProvider.getArmorSlots(entity);
            if (armorSlots != null) {
                allArmourSlots = Collections.concat(allArmourSlots, armorSlots);
            }
        }
        return allArmourSlots;
    }

    @Override
    public Iterable<ItemStack> getHandSlots(Entity entity) {
        Iterable<ItemStack> allHandSlots = DEFAULT_EMPTY_LIST;
        for (var itemProvider : itemProviders) {
            Iterable<ItemStack> handSlots = itemProvider.getHandSlots(entity);
            if (handSlots != null) {
                allHandSlots = Collections.concat(allHandSlots, handSlots);
            }
        }
        return allHandSlots;
    }
}

