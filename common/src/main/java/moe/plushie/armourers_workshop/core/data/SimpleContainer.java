package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.compat.core.menu.AbstractContainerListener;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.NonNullItemList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SimpleContainer implements Container, IDataSerializable.Mutable {

    private final NonNullItemList items;
    private List<AbstractContainerListener> listeners;

    public SimpleContainer(int size) {
        this.items = new NonNullItemList(size);
    }

    public void addListener(AbstractContainerListener containerListener) {
        if (listeners == null) {
            listeners = Collections.newList();
        }
        listeners.add(containerListener);
    }

    public void removeListener(AbstractContainerListener containerListener) {
        if (listeners != null) {
            listeners.remove(containerListener);
        }
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        items.serialize(serializer);
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        items.deserialize(serializer);
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int i) {
        return items.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        var itemStack = ContainerHelper.removeItem(items, i, j);
        if (!itemStack.isEmpty()) {
            setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(items, i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        items.set(i, itemStack);
        if (itemStack.getCount() > getMaxStackSize()) {
            itemStack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public void setChanged() {
        if (listeners != null) {
            for (var listener : this.listeners) {
                listener.containerChanged(this);
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    public NonNullItemList getItems() {
        return items;
    }
}
