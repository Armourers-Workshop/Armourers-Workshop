package moe.plushie.armourers_workshop.compat.core.menu;

import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

@Available("[16, 26)")
public abstract class AbstractContainerSlotImpl extends Slot {

    public AbstractContainerSlotImpl(Container container, int i, int j, int k) {
        super(container, i, j, k);
    }

    public abstract OpenResourceKey noItemIcon();

    @Nullable
    @Override
    public final Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        var location = noItemIcon();
        if (location != null) {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, location.get());
        }
        return null;
    }
}
