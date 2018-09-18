package moe.plushie.armourers_workshop.common.tileentities;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.items.ItemSkin;
import moe.plushie.armourers_workshop.common.items.ItemSkinTemplate;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.library.LibraryFileType;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin.SendType;
import moe.plushie.armourers_workshop.common.skin.ISkinHolder;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TileEntitySkinLibrary extends AbstractTileEntityInventory implements ISidedInventory {
    
    private static final int INVENTORY_SIZE = 2;
    
    public TileEntitySkinLibrary() {
        super(INVENTORY_SIZE);
    }
    
    @Override
    public String getName() {
        return LibBlockNames.ARMOUR_LIBRARY;
    }
    
    public boolean isCreativeLibrary() {
        int meta = getBlockMetadata();
        if (meta == 1) {
            return true;
        }
        return false;
    }
    
    public void sendArmourToClient(String filename, String filePath, EntityPlayerMP player) {
        if (!ConfigHandler.allowClientsToDownloadSkins) {
            return;
        }
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (stackInput == null) {
            return;
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!(stackInput.getItem() instanceof ItemSkin)) {
            return;
        }
        
        if (!SkinNBTHelper.stackHasSkinData(stackInput)) {
            return;
        }
        
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stackInput);
        
        Skin skin = CommonSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            return;
        }
        
        LibraryFile file = new LibraryFile(filename, filePath, skin.getSkinType());
        
        //if the file was overwritten remove it's old id link
        CommonSkinCache.INSTANCE.clearFileNameIdLink(file);
        
        //ModLogger.log(file.getFullName());
        
        MessageServerLibrarySendSkin message = new MessageServerLibrarySendSkin(filename, filePath, skin, SendType.LIBRARY_SAVE);
        PacketHandler.networkWrapper.sendTo(message, player);
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackInput);
    }

    /**
     * Save armour data from an items NBT data into a file on the disk.
     * @param filePath 
     * @param filename The name of the file to save to.
     * @param player The player that pressed the save button.
     * @param publicFiles If true save to the public file list or false for the players private files.
     */
    public void saveArmour(String fileName, String filePath, EntityPlayerMP player, boolean publicFiles) {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (stackInput == null) {
            return;
        }
        if (stackOutput != null) {
            return;
        }
        if (!(stackInput.getItem() instanceof ItemSkin)) {
            return;
        }
        if (!SkinNBTHelper.stackHasSkinData(stackInput)) {
            return;
        }
        
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stackInput);
        if (skinPointer == null) {
            return;
        }
        
        if (!publicFiles) {
            //filePath = "/private/" + player.getUniqueID().toString() + filePath;
        }
        ModLogger.log("save");
        Skin skin = CommonSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            ModLogger.log("no input");
            return;
        }
        ModLogger.log("save");
        filePath = SkinIOUtils.makeFilePathValid(filePath);
        fileName = SkinIOUtils.makeFileNameValid(fileName);
        
        LibraryFile file = new LibraryFile(fileName, filePath, skin.getSkinType());
        
        //if the file was overwritten remove it's old id link
        CommonSkinCache.INSTANCE.clearFileNameIdLink(file);

        if (!SkinIOUtils.saveSkinFromFileName(filePath, fileName + SkinIOUtils.SKIN_FILE_EXTENSION, skin)) {
            return;
        }
        
        if (ArmourersWorkshop.isDedicated()) {
            if (publicFiles) {
                ArmourersWorkshop.proxy.libraryManager.addFileToListType(new LibraryFile(fileName, filePath, skin.getSkinType()), LibraryFileType.SERVER_PUBLIC, player);
            } else {
                ArmourersWorkshop.proxy.libraryManager.addFileToListType(new LibraryFile(fileName, filePath, skin.getSkinType()), LibraryFileType.SERVER_PRIVATE, player);
            }
        } else {
            ArmourersWorkshop.proxy.libraryManager.addFileToListType(new LibraryFile(fileName, filePath, skin.getSkinType()), LibraryFileType.LOCAL, player);
        }
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackInput);
    }
    
    /**
     * Loads an armour file from the disk and adds it to an items NBT data.
     * @param filePath 
     * @param filename The name of the file to load.
     * @param player The player that pressed the load button.
     */
    public void loadArmour(String fileName, String filePath, EntityPlayerMP player, boolean trackFile) {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (!isCreativeLibrary()) {
            if (stackInput == null) {
                return;
            }
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!isCreativeLibrary()) {
            if (!(stackInput.getItem() instanceof ISkinHolder)) {
                return;
            }
        }
        
        
        Skin skin = null;
        String fullFileName = fileName;
        
        
        skin = SkinIOUtils.loadSkinFromFileName(filePath + fileName + SkinIOUtils.SKIN_FILE_EXTENSION);
        
        if (skin == null) {
            return;
        }
        
        LibraryFile libraryFile = new LibraryFile(fileName, filePath, skin.getSkinType());
        SkinIdentifier identifier = null;
        if (trackFile) {
            identifier = new SkinIdentifier(0, libraryFile, 0, skin.getSkinType());
        } else {
            identifier = new SkinIdentifier(skin.lightHash(), null, 0, skin.getSkinType());
        }
        
        
        // TODO Set master using trackFile
        
        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, libraryFile);
        ModLogger.log("Loaded file form lib: " + libraryFile.toString());
        
        ItemStack stackArmour = SkinNBTHelper.makeEquipmentSkinStack(skin, identifier);
        
        if (stackArmour == null) {
            return;
        }
        
        if (!isCreativeLibrary()) {
            this.decrStackSize(0, 1);
        }
        
        this.setInventorySlotContents(1, stackArmour);
    }
    
    public void loadArmour(Skin skin, EntityPlayerMP player) {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (!isCreativeLibrary()) {
            if (stackInput == null) {
                return;
            }
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!isCreativeLibrary()) {
            if (!(stackInput.getItem() instanceof ISkinHolder)) {
                return;
            }
        }
        
        ItemStack inputItem = SkinNBTHelper.makeEquipmentSkinStack(skin);
        if (inputItem == null) {
            return;
        }
        
        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, (LibraryFile)null);
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, inputItem);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        int[] slots = new int[2];
        slots[0] = 0;
        slots[1] = 1;
        return slots;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        if (index != 0) {
            return false;
        }
        if (itemStackIn.getItem() instanceof ItemSkinTemplate && itemStackIn.getItemDamage() == 0) {
            return true;
        }
        if (itemStackIn.getItem() instanceof ItemSkin) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }
}
