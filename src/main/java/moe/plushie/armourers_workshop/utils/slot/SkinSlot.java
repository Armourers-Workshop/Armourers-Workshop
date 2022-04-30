package moe.plushie.armourers_workshop.utils.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("NullableProblems")
public class SkinSlot extends Slot {

    protected final Collection<SkinSlotType> slotTypes;
    private ArrayList<Pair<ResourceLocation, ResourceLocation>> backgroundPairs;

    public SkinSlot(IInventory inventory, int index, int x, int y, SkinSlotType... slotTypes) {
        super(inventory, index, x, y);
        this.slotTypes = Arrays.asList(slotTypes);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        // when slot type is not provide, we consider it is an unrestricted slot.
        if (!slotTypes.isEmpty() && !slotTypes.contains(SkinSlotType.of(itemStack))) {
            return false;
        }
        return container.canPlaceItem(index, itemStack);
    }

    public Collection<SkinSlotType> getSlotTypes() {
        return slotTypes;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        if (backgroundPairs == null) {
            backgroundPairs = new ArrayList<>();
            for (SkinSlotType slotType : slotTypes) {
                backgroundPairs.add(Pair.of(PlayerContainer.BLOCK_ATLAS, slotType.getIconSprite()));
            }
        }
        int size = backgroundPairs.size();
        if (size > 0) {
            return backgroundPairs.get((int) ((System.currentTimeMillis() / 1000L) % size));
        }
        return null;
    }
}
