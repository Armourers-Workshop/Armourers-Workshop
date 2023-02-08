package moe.plushie.armourers_workshop.core.data;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.api.common.IItemStackProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemStackProvider implements IItemStackProvider {

    private static final List<ItemStack> DEFAULT_EMPTY_LIST = Collections.emptyList();
    private static final ItemStackProvider DEFAULT_ITEM_PROVIDER = new ItemStackProvider();

    private final ArrayList<IItemStackProvider> itemProviders = Lists.newArrayList(new VanillaItemStackProvider());

    public static ItemStackProvider getInstance() {
        return DEFAULT_ITEM_PROVIDER;
    }

    public void register(IItemStackProvider itemProvider) {
        itemProviders.add(itemProvider);
    }



    @Override
    public Iterable<ItemStack> getArmorSlots(Entity entity) {
        Iterable<ItemStack> allArmourSlots = DEFAULT_EMPTY_LIST;
        for (IItemStackProvider itemProvider : itemProviders) {
            Iterable<ItemStack> armorSlots = itemProvider.getArmorSlots(entity);
            if (armorSlots != null) {
                allArmourSlots = Iterables.concat(allArmourSlots, armorSlots);
            }
        }
        return allArmourSlots;
    }

    @Override
    public Iterable<ItemStack> getHandSlots(Entity entity) {
        Iterable<ItemStack> allHandSlots = DEFAULT_EMPTY_LIST;
        for (IItemStackProvider itemProvider : itemProviders) {
            Iterable<ItemStack> handSlots = itemProvider.getHandSlots(entity);
            if (handSlots != null) {
                allHandSlots = Iterables.concat(allHandSlots, handSlots);
            }
        }
        return allHandSlots;
    }
}

