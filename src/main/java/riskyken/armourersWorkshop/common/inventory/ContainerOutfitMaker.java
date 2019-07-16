package riskyken.armourersWorkshop.common.inventory;

import java.awt.Point;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperty;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.inventory.slot.SlotOutput;
import riskyken.armourersWorkshop.common.inventory.slot.SlotSkin;
import riskyken.armourersWorkshop.common.items.ItemSkin;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.data.SkinProperty;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityOutfitMaker;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ContainerOutfitMaker extends ModTileContainer<TileEntityOutfitMaker> implements IButtonPress {

    private int indexSkinsStart = 0;
    private int indexSkinsEnd = 0;

    public ContainerOutfitMaker(EntityPlayer player, TileEntityOutfitMaker tileEntity) {
        super(player, tileEntity);

        ISkinType[] skinTypes = new ISkinType[] { SkinTypeRegistry.skinHead, SkinTypeRegistry.skinChest, SkinTypeRegistry.skinLegs, SkinTypeRegistry.skinFeet, SkinTypeRegistry.skinWings };

        addPlayerSlots(8, 158);

        // addSlotToContainer(new SlotSkin(SkinTypeRegistry.skinOutfit, tileEntity, 0, 8, 88));
        addSlotToContainer(new SlotOutput(tileEntity, 1, 148, 88));
        indexSkinsStart = getPlayerInvEndIndex() + 1;
        indexSkinsEnd = indexSkinsStart;
        for (int skinIndex = 0; skinIndex < skinTypes.length; skinIndex++) {
            for (int i = 0; i < TileEntityOutfitMaker.OUTFIT_ROWS; i++) {
                addSlotToContainer(new SlotSkin(skinTypes[skinIndex], tileEntity, skinIndex + (i * TileEntityOutfitMaker.OUTFIT_SKINS) + 2, 36 + skinIndex * 20, 58 + i * 20));
                indexSkinsEnd++;
            }
        }
    }

    private void loadOutfit() {

    }

    private void saveOutfit(EntityPlayer player) {
        ArrayList<SkinPart> skinParts = new ArrayList<SkinPart>();
        SkinProperties skinProperties = new SkinProperties();
        String partIndexs = "";
        int[] paintData = null;
        int skinIndex = 0;
        for (int i = 2; i < tileEntity.getSizeInventory(); i++) {
            ItemStack stack = tileEntity.getStackInSlot(i);
            ISkinPointer descriptor = SkinNBTHelper.getSkinPointerFromStack(stack);
            if (descriptor != null) {
                Skin skin = CommonSkinCache.INSTANCE.getSkin(descriptor);
                if (skin != null) {
                    for (int partIndex = 0; partIndex < skin.getPartCount(); partIndex++) {
                        SkinPart part = skin.getParts().get(partIndex);
                        skinParts.add(part);
                    }

                    if (skin.hasPaintData()) {
                        if (paintData == null) {
                            paintData = new int[64 * 32];
                        }
                        for (int partIndex = 0; partIndex < skin.getSkinType().getSkinParts().size(); partIndex++) {
                            ISkinPartType part = skin.getSkinType().getSkinParts().get(partIndex);
                            if (part instanceof ISkinPartTypeTextured) {
                                ISkinPartTypeTextured texType = ((ISkinPartTypeTextured) part);
                                paintData = paintPart(texType, paintData, skin.getPaintData());
                            }
                        }
                    }

                    if (partIndexs.isEmpty()) {
                        partIndexs = String.valueOf(skinParts.size());
                    } else {
                        partIndexs += ":" + String.valueOf(skinParts.size());
                    }

                    for (ISkinProperty prop : skin.getSkinType().getProperties()) {
                        SkinProperty p = (SkinProperty) prop;
                        if (p.getKey().startsWith("wings")) {
                            p.setValue(skinProperties, p.getValue(skin.getProperties()), skinIndex);
                        } else {
                            p.setValue(skinProperties, p.getValue(skin.getProperties()));
                        }
                    }
                    skinIndex++;
                }
            }
        }
        if (!skinParts.isEmpty()) {
            SkinProperties.PROP_OUTFIT_PART_INDEXS.setValue(skinProperties, partIndexs);
            SkinProperties.PROP_ALL_AUTHOR_NAME.setValue(skinProperties, player.getDisplayName());
            if (player.getGameProfile() != null && player.getGameProfile().getId() != null) {
                SkinProperties.PROP_ALL_AUTHOR_UUID.setValue(skinProperties, player.getGameProfile().getId().toString());
            }
            SkinProperties.PROP_ALL_CUSTOM_NAME.setValue(skinProperties, tileEntity.getOutfitName());
            SkinProperties.PROP_ALL_FLAVOUR_TEXT.setValue(skinProperties, tileEntity.getOutfitFlavour());
            Skin skin = new Skin(skinProperties, SkinTypeRegistry.skinOutfit, paintData, skinParts);
            CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, (LibraryFile) null);
            ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinPointer(skin));
            tileEntity.setInventorySlotContents(1, skinStack);
        }
    }

    private int[] paintPart(ISkinPartTypeTextured texType, int[] desPaint, int[] srcPaint) {
        int textureWidth = 64;
        int textureHeight = 32;

        Point pos = texType.getTextureLocation();
        int width = (texType.getTextureModelSize().getX() * 2) + (texType.getTextureModelSize().getZ() * 2);
        int height = texType.getTextureModelSize().getY() + texType.getTextureModelSize().getZ();

        for (int ix = 0; ix < width; ix++) {
            for (int iy = 0; iy < height; iy++) {
                int x = pos.x + ix;
                int y = pos.y + iy;
                byte[] rgbt = PaintingHelper.intToBytes(srcPaint[x + (y * textureWidth)]);
                if ((rgbt[3] & 0xFF) != PaintType.NONE.getKey()) {
                    desPaint[x + (y * textureWidth)] = srcPaint[x + (y * textureWidth)];
                }
            }
        }

        return desPaint;
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            boolean slotted = false;

            // Putting skin in inv.
            if (stack.getItem() instanceof ItemSkin & SkinNBTHelper.stackHasSkinData(stack)) {
                for (int i = indexSkinsStart; i < indexSkinsEnd; i++) {
                    Slot targetSlot = getSlot(i);
                    if (targetSlot.isItemValid(stack)) {
                        if (this.mergeItemStack(stack, i, i + 1, false)) {
                            slotted = true;
                            break;
                        }
                    }
                }
            }

            // Putting outfit in input slot.
            if (stack.getItem() instanceof ItemSkin & SkinNBTHelper.stackHasSkinData(stack)) {
                for (int i = getPlayerInvEndIndex(); i < getPlayerInvEndIndex() + 1; i++) {
                    Slot targetSlot = getSlot(i);
                    if (targetSlot.isItemValid(stack)) {
                        if (this.mergeItemStack(stack, i, i + 1, false)) {
                            slotted = true;
                            break;
                        }
                    }
                }
            }

            if (!slotted) {
                return null;
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onPickupFromSlot(playerIn, stack);

            return result;

        }
        return null;
    }

    @Override
    public void buttonPressed(byte buttonId) {
        if (buttonId == 0) {
            loadOutfit();
        }
        if (buttonId == 1) {
            saveOutfit(getPlayer());
        }
    }
}
