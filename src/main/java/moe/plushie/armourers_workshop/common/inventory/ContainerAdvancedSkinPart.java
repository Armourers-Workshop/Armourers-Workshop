package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileAdvancedSkinPart;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAdvancedSkinPart extends ModTileContainer<TileAdvancedSkinPart> {

    public ContainerAdvancedSkinPart(InventoryPlayer invPlayer, TileAdvancedSkinPart tileEntity) {
        super(invPlayer, tileEntity);
        addPlayerSlots(8, 40);
        
        addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinAdvanced, tileEntity, 0, 8, 16));
    }
}
