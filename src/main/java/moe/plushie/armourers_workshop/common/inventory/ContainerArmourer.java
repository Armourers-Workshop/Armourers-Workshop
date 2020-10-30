package moe.plushie.armourers_workshop.common.inventory;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.common.exception.SkinSaveException;
import moe.plushie.armourers_workshop.common.init.items.ItemArmourContainerItem;
import moe.plushie.armourers_workshop.common.init.items.ItemSkin;
import moe.plushie.armourers_workshop.common.init.items.ItemSkinTemplate;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotOutput;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkinTemplate;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.armourers_workshop.common.skin.ISkinHolder;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperty;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import moe.plushie.armourers_workshop.common.world.ArmourerWorldHelper;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;

public class ContainerArmourer extends ModTileContainer<TileEntityArmourer> implements IButtonPress {

    public ContainerArmourer(InventoryPlayer invPlayer, TileEntityArmourer tileEntity) {
        super(invPlayer, tileEntity);

        addSlotToContainer(new SlotSkinTemplate(tileEntity, 0, 64, 21));
        addSlotToContainer(new SlotOutput(tileEntity, 1, 147, 21));

        addPlayerSlots(8, 142);
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if ((stack.getItem() instanceof ItemSkinTemplate) | stack.getItem() instanceof ItemSkin | stack.getItem() instanceof ItemArmourContainerItem) {
                if (!this.mergeItemStack(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(playerIn, stack);

            return result;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Get blocks in the world and saved them onto an items NBT data.
     * 
     * @param player The player that pressed the save button.
     * @param name   Custom name for the item.
     */
    public void saveArmourItem(EntityPlayerMP player, String customName, String tags) {
        if (tileEntity.getWorld().isRemote) {
            return;
        }

        ItemStack stackInput = tileEntity.getStackInSlot(0);
        ItemStack stackOutput = tileEntity.getStackInSlot(1);

        if (player.capabilities.isCreativeMode) {
            if (stackInput.isEmpty()) {
                stackInput = new ItemStack(ModItems.SKIN_TEMPLATE);
            }
        }

        if (stackInput.isEmpty()) {
            return;
        }

        if (!stackOutput.isEmpty()) {
            return;
        }

        if (!(stackInput.getItem() instanceof ISkinHolder)) {
            return;
        }

        GameProfile authorProfile = player.getGameProfile();
        // authorProfile = new GameProfile(UUID.fromString("b9e99f95-09fe-497a-8a77-1ccc839ab0f4"), "VermillionX");

        Skin skin = null;
        SkinProperties skinProps = new SkinProperties();
        SkinProperties.PROP_ALL_AUTHOR_NAME.setValue(skinProps, authorProfile.getName());
        if (player.getGameProfile() != null && player.getGameProfile().getId() != null) {
            SkinProperties.PROP_ALL_AUTHOR_UUID.setValue(skinProps, authorProfile.getId().toString());
        }
        SkinProperties.PROP_ALL_CUSTOM_NAME.setValue(skinProps, customName);

        for (int i = 0; i < tileEntity.getSkinType().getProperties().size(); i++) {
            SkinProperty skinProp = (SkinProperty) tileEntity.getSkinType().getProperties().get(i);
            skinProp.setValue(skinProps, skinProp.getValue(tileEntity.getSkinProps()));
        }

        try {
            skin = ArmourerWorldHelper.saveSkinFromWorld(tileEntity.getWorld(), skinProps, tileEntity.getSkinType(), tileEntity.getPaintData(), tileEntity.getPos().offset(EnumFacing.UP, tileEntity.getHeightOffset()), tileEntity.getDirection());
        } catch (SkinSaveException e) {
            switch (e.getType()) {
            case NO_DATA:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            case MARKER_ERROR:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            case MISSING_PARTS:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            case BED_AND_SEAT:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            case INVALID_MULTIBLOCK:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            }
        }

        if (skin == null) {
            return;
        }

        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, (LibraryFile) null);

        ItemStack stackArmour = ((ISkinHolder) stackInput.getItem()).makeSkinStack(skin);

        if (stackArmour.isEmpty()) {
            return;
        }
        if (!player.capabilities.isCreativeMode) {
            tileEntity.decrStackSize(0, 1);
        }
        tileEntity.setInventorySlotContents(1, stackArmour);
    }

    /**
     * Reads the NBT data from an item and places blocks in the world.
     * 
     * @param player The player that pressed the load button.
     */
    public void loadArmourItem(EntityPlayerMP player) {
        if (tileEntity.getWorld().isRemote) {
            return;
        }
        ItemStack stackInput = tileEntity.getStackInSlot(0);
        ItemStack stackOuput = tileEntity.getStackInSlot(1);

        if (stackInput.isEmpty()) {
            return;
        }
        if (!stackOuput.isEmpty()) {
            return;
        }
        if (!(stackInput.getItem() instanceof ItemSkin)) {
            return;
        }
        SkinDescriptor skinPointerInput = SkinNBTHelper.getSkinDescriptorFromStack(stackInput);
        if (skinPointerInput == null) {
            return;
        }
        if (tileEntity.getSkinType() == null) {
            return;
        }
        if (tileEntity.getSkinType() != skinPointerInput.getIdentifier().getSkinType()) {
            if (!(tileEntity.getSkinType() == SkinTypeRegistry.skinLegs && skinPointerInput.getIdentifier().getSkinType() == SkinTypeRegistry.oldSkinSkirt)) {
                return;
            }
        }

        Skin skin = CommonSkinCache.INSTANCE.getSkin(skinPointerInput);
        if (skin == null) {
            return;
        }

        tileEntity.setSkinProps(new SkinProperties(skin.getProperties()));

        ArmourerWorldHelper.loadSkinIntoWorld(tileEntity.getWorld(), tileEntity.getPos().offset(EnumFacing.UP, tileEntity.getHeightOffset()), skin, tileEntity.getDirection());
        if (skin.hasPaintData()) {
            tileEntity.setPaintData(skin.getPaintData().clone());
        } else {
            tileEntity.clearPaintData(true);
        }
        tileEntity.dirtySync();

        tileEntity.setInventorySlotContents(0, ItemStack.EMPTY);
        tileEntity.setInventorySlotContents(1, stackInput);
    }

    @Override
    public void buttonPressed(EntityPlayerMP player, byte buttonId) {
        TileEntityArmourer armourerBrain = getTileEntity();
        // ModLogger.log("load " + message.buttonId);
        if (buttonId == 14) {
            loadArmourItem(player);
        }
        if (buttonId == 7) {
            armourerBrain.toggleGuides();
        }
        if (buttonId == 6) {
            armourerBrain.toggleHelper();
        }
        if (buttonId == 11) {
            // armourerBrain.cloneToSide(ForgeDirection.WEST);
        }
        if (buttonId == 12) {
            // armourerBrain.cloneToSide(ForgeDirection.EAST);
        }
    }
}
