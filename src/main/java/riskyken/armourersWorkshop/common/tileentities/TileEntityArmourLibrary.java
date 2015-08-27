package riskyken.armourersWorkshop.common.tileentities;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkinTemplate;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerLibrarySendSkin;
import riskyken.armourersWorkshop.common.skin.ISkinHolder;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArmourLibrary extends AbstractTileEntityInventory implements ISidedInventory {
    
    public static ArrayList<LibraryFile> clientFileNames = null;
    public static ArrayList<LibraryFile> publicServerFileNames = null;
    public static ArrayList<LibraryFile> privateServerFileNames = null;
    
    public TileEntityArmourLibrary() {
        this.items = new ItemStack[2];
    }
    
    @Override
    public String getInventoryName() {
        return LibBlockNames.ARMOUR_LIBRARY;
    }
    
    public boolean isCreativeLibrary() {
        int meta = getBlockMetadata();
        if (meta == 1) {
            return true;
        }
        return false;
    }
    
    public void sendArmourToClient(String filename, EntityPlayerMP player) {
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
        
        if (!(stackInput.getItem() instanceof ItemEquipmentSkin)) {
            return;
        }
        
        if (!EquipmentNBTHelper.stackHasSkinData(stackInput)) {
            return;
        }
        
        SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(stackInput);
        
        Skin skin = SkinDataCache.INSTANCE.getEquipmentData(skinPointer.skinId);
        if (skin == null) {
            return;
        }
        
        MessageServerLibrarySendSkin message = new MessageServerLibrarySendSkin(filename, skin);
        PacketHandler.networkWrapper.sendTo(message, player);
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackInput);
    }

    /**
     * Save armour data from an items NBT data into a file on the disk.
     * @param filename The name of the file to save to.
     * @param player The player that pressed the save button.
     */
    public void saveArmour(String fileName, EntityPlayerMP player) {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (stackInput == null) {
            return;
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!(stackInput.getItem() instanceof ItemEquipmentSkin)) {
            return;
        }
        
        if (!EquipmentNBTHelper.stackHasSkinData(stackInput)) {
            return;
        }
        
        int equipmentId = EquipmentNBTHelper.getSkinIdFromStack(stackInput);
        
        Skin skin = SkinDataCache.INSTANCE.getEquipmentData(equipmentId);
        if (skin == null) {
            return;
        }
        
        if (!SkinIOUtils.saveSkinFromFileName(fileName + ".armour", skin)) {
            return;
        }
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackInput);
    }
    
    /**
     * Loads an armour file from the disk and adds it to an items NBT data.
     * @param filename The name of the file to load.
     * @param player The player that pressed the load button.
     */
    public void loadArmour(String fileName, EntityPlayerMP player) {
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
        
        
        Skin armourItemData = SkinIOUtils.loadSkinFromFileName(fileName + ".armour");
        if (armourItemData == null) {
            return;
        }
        
        SkinDataCache.INSTANCE.addEquipmentDataToCache(armourItemData);
        
        ItemStack stackArmour = EquipmentNBTHelper.makeEquipmentSkinStack(armourItemData);
        
        if (stackArmour == null) {
            return;
        }
        
        if (!isCreativeLibrary()) {
            this.decrStackSize(0, 1);
        }
        
        this.setInventorySlotContents(1, stackArmour);
    }
    
    public void loadArmour(Skin itemData, EntityPlayerMP player) {
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
        
        ItemStack inputItem = EquipmentNBTHelper.makeEquipmentSkinStack(itemData);
        if (inputItem == null) {
            return;
        }
        
        SkinDataCache.INSTANCE.addEquipmentDataToCache(itemData);
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, inputItem);
    }
    
    public static ArrayList<LibraryFile> getFileNames(EntityPlayer player) {
        File directory = SkinIOUtils.getSkinLibraryDirectory();
        if (!directory.exists()) {
            return null;
        }
        
        File[] templateFiles;
        try {
            templateFiles = directory.listFiles();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, "Armour file list load failed.");
            e.printStackTrace();
            return null;
        }
        
        ArrayList<LibraryFile> fileList = new ArrayList<LibraryFile>();
        for (int i = 0; i < templateFiles.length; i++) {
            if (templateFiles[i].getName().endsWith(".armour")) {
                String cleanName = FilenameUtils.removeExtension(templateFiles[i].getName());
                int skinId = 0;
                ISkinType skinType = SkinIOUtils.getSkinTypeNameFromFile(templateFiles[i]);
                boolean readOnly = true;
                if (!ArmourersWorkshop.isDedicated()) {
                    readOnly = false;
                }
                if (skinType != null) {
                    fileList.add(new LibraryFile(cleanName, skinId, skinType, readOnly));
                } else {
                    ModLogger.log(Level.WARN, "Skin type for file " + cleanName + " was null.");
                }
            }
        }
        
        Collections.sort(fileList);
        
        return fileList;
    }
    
    public void setSkinFileList(ArrayList<LibraryFile> fileNames) {
        this.publicServerFileNames = fileNames;
        setLocalFileList();
    }
    
    @SideOnly(Side.CLIENT)
    private static void setLocalFileList() {
        EntityClientPlayerMP localPlayer = Minecraft.getMinecraft().thePlayer;
        clientFileNames = getFileNames(localPlayer);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (isCreativeLibrary()) {
            int[] slots = new int[1];
            slots[0] = 1;
            return slots;
        } else {
            int[] slots = new int[2];
            slots[0] = 0;
            slots[1] = 1;
            return slots;
        }
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        if (isCreativeLibrary()) {
            return false;
        }
        if (slot != 0) {
            return false;
        }
        if (stack.getItem() instanceof ItemEquipmentSkinTemplate && stack.getItemDamage() == 0) {
            return true;
        }
        if (stack.getItem() instanceof ItemEquipmentSkin) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }
    
    public static class LibraryFile implements Comparable<LibraryFile> {
        public final String fileName;
        public final int skinId;
        public final ISkinType skinType;
        public final boolean readOnly;
        
        public LibraryFile(String fileName, int skinId, ISkinType skinType, boolean readOnly) {
            this.fileName = fileName;
            this.skinId = skinId;
            this.skinType = skinType;
            this.readOnly = readOnly;
        }
        
        public void writeToByteBuf(ByteBuf buf) {
            ByteBufUtils.writeUTF8String(buf, fileName);
            buf.writeInt(skinId);
            ByteBufUtils.writeUTF8String(buf, skinType.getRegistryName());
            buf.writeBoolean(readOnly);
        }
        
        public static LibraryFile readFromByteBuf(ByteBuf buf) {
            String fileName = ByteBufUtils.readUTF8String(buf);
            int skinId = buf.readInt();
            String regName = ByteBufUtils.readUTF8String(buf);
            ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
            boolean readOnly = buf.readBoolean();
            return new LibraryFile(fileName, skinId, skinType, readOnly);
        }

        @Override
        public int compareTo(LibraryFile o) {
            return fileName.compareTo(o.fileName);
        }
    }
    
    public enum LibraryFileType {
        LOCAL,
        REMOTE_PUBLIC,
        REMOTE_PRIVATE;
    }
}
