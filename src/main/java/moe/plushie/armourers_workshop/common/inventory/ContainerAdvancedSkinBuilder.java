package moe.plushie.armourers_workshop.common.inventory;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkin;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerAdvancedSkinBuilder extends ModTileContainer<TileEntityAdvancedSkinBuilder> implements IButtonPress {

    public ContainerAdvancedSkinBuilder(InventoryPlayer invPlayer, TileEntityAdvancedSkinBuilder tileEntity) {
        super(invPlayer, tileEntity);
        addPlayerSlots(8, 40);
        for (int i = 0; i < tileEntity.getSizeInventory(); i++) {
            addSlotToContainer(new SlotSkin(tileEntity, i, 8 + 20 * i, 20, SkinTypeRegistry.skinPart));
        }
    }

    @Override
    public void buttonPressed(EntityPlayerMP player, byte buttonId) {
        ArmourersWorkshop.getLogger().info("Making advanced part.");

        SkinProperties properties = new SkinProperties();
        ISkinType skinType = SkinTypeRegistry.skinLegs;
        ArrayList<SkinPart> skinParts = new ArrayList<SkinPart>();

        SkinProperties.PROP_ALL_AUTHOR_NAME.setValue(properties, player.getName());
        if (player.getGameProfile() != null && player.getGameProfile().getId() != null) {
            SkinProperties.PROP_ALL_AUTHOR_UUID.setValue(properties, player.getGameProfile().getId().toString());
        }
        // SkinProperties.PROP_ALL_CUSTOM_NAME.setValue(properties, "");
        // SkinProperties.PROP_ALL_FLAVOUR_TEXT.setValue(properties, "");

        for (int i = 0; i < getTileEntity().getSizeInventory(); i++) {
            ItemStack itemStack = getTileEntity().getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
                Skin skin = CommonSkinCache.INSTANCE.getSkin(descriptor);
                if (skin != null) {
                    SkinPart skinPart = skin.getParts().get(0);
                    if (!skinParts.contains(skinPart)) {
                        skinParts.add(skinPart);
                    }
                }
            }
        }

        ArmourersWorkshop.getLogger().info("Parts found: " + skinParts.size());

        Skin skin = new Skin(properties, skinType, null, skinParts);
        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, (LibraryFile) null);
        ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinDescriptor(skin));

        UtilItems.spawnItemAtEntity(player, skinStack, false);
    }
}
