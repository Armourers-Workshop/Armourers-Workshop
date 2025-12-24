package moe.plushie.armourers_workshop.compat.core.menu;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Available("[1.16, )")
public class AbstractContainerSlot extends AbstractContainerSlotImpl {

    public AbstractContainerSlot(Container container, int i, int j, int k) {
        super(container, i, j, k);
    }

    protected void abi$setItem(ItemStack itemStack) {
        super.set(itemStack);
    }

    protected ItemStack abi$getItem() {
        return super.getItem();
    }

    protected void abi$setChanged() {
        super.setChanged();
    }

    protected boolean abi$mayPlace(ItemStack itemStack) {
        return super.mayPlace(itemStack);
    }

    protected boolean abi$mayPickup(Player player) {
        return super.mayPickup(player);
    }

    protected OpenResourceLocation abi$noItemIcon() {
        return null;
    }

    protected boolean abi$isActive() {
        return super.isActive();
    }

    ///  API Implements

    @Override
    public final void set(ItemStack itemStack) {
        abi$setItem(itemStack);
    }

    @Override
    public final ItemStack getItem() {
        return abi$getItem();
    }

    @Override
    public final void setChanged() {
        abi$setChanged();
    }

    @Override
    public final boolean mayPlace(ItemStack itemStack) {
        return abi$mayPlace(itemStack);
    }

    @Override
    public final boolean mayPickup(Player player) {
        return abi$mayPickup(player);
    }

    @Override
    public final boolean isActive() {
        return abi$isActive();
    }

    @Override
    public final ResourceLocation noItemIcon() {
        var location = abi$noItemIcon();
        if (location != null) {
            return location.get();
        }
        return null;
    }
}
