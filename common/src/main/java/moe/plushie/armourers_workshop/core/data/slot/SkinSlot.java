package moe.plushie.armourers_workshop.core.data.slot;

import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class SkinSlot extends Slot {

    protected final Collection<SkinSlotType> slotTypes;
    private ArrayList<Pair<ResourceLocation, ResourceLocation>> backgroundPairs;

    public SkinSlot(Container inventory, int index, int x, int y, SkinSlotType... slotTypes) {
        super(inventory, index, x, y);
        this.slotTypes = ObjectUtils.map(slotTypes);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        // when slot type is not provide, we consider it is an unrestricted slot.
        if (!slotTypes.isEmpty() && !slotTypes.contains(SkinSlotType.byItem(itemStack))) {
            return false;
        }
        return container.canPlaceItem(index, itemStack);
    }

    public Collection<SkinSlotType> getSlotTypes() {
        return slotTypes;
    }

    @Nullable
    @Environment(EnvType.CLIENT)
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        if (backgroundPairs == null) {
            backgroundPairs = new ArrayList<>();
            for (SkinSlotType slotType : slotTypes) {
                backgroundPairs.add(Pair.of(InventoryMenu.BLOCK_ATLAS, slotType.getIconSprite()));
            }
        }
        int size = backgroundPairs.size();
        if (size > 0) {
            return backgroundPairs.get((int) ((System.currentTimeMillis() / 1000L) % size));
        }
        return null;
    }
}
