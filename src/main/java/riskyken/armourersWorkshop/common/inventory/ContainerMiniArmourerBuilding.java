package riskyken.armourersWorkshop.common.inventory;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerMiniArmourerSkinData;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.utils.ModLogger;

public class ContainerMiniArmourerBuilding extends Container {

    private TileEntityMiniArmourer tileEntity;
    private ArrayList<SkinPart> skinParts;
    
    public ContainerMiniArmourerBuilding(TileEntityMiniArmourer tileEntity) {
        this.tileEntity = tileEntity;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotID) {
        return null;
    }
    
    @Override
    public void addCraftingToCrafters(ICrafting player) {
        super.addCraftingToCrafters(player);
        MessageServerMiniArmourerSkinData message;
        message = new MessageServerMiniArmourerSkinData(tileEntity.getSkinParts());
        if (player instanceof EntityPlayerMP) {
            PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) player);
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return tileEntity.isUseableByPlayer(entityPlayer);
    }
    
    public TileEntityMiniArmourer getTileEntity() {
        return tileEntity;
    }
    
    public void setSkinParts(ArrayList<SkinPart> skinParts) {
        tileEntity.setSkinParts(skinParts);
    }
    
    public ArrayList<SkinPart> getSkinParts() {
        return tileEntity.getSkinParts();
    }
    
    public void updateFromClientAddOrEdit(ISkinPartType skinPartType, ICube cube) {
        ModLogger.log("got cube: " + cube);
        ArrayList<SkinPart> skinParts = tileEntity.getSkinParts();
        for (int i = 0; i < skinParts.size(); i++) {
            if (skinParts.get(i).getPartType() == skinPartType) {
                ModLogger.log("found part: " + skinPartType.getRegistryName());
                ArrayList<ICube> cubeData = skinParts.get(i).getArmourData();
                for (int j = 0; j < cubeData.size(); j++) {
                    ICube curCube = cubeData.get(j);
                    if (curCube.getX() == cube.getX() & curCube.getY() == cube.getY() & curCube.getZ() == cube.getZ()) {
                        ModLogger.log("removing old cube");
                        cubeData.remove(j);
                        break;
                    }
                }
                cubeData.add(cube);
                return;
            }
        }
    }
    
    public void updateFromClientRemove(ISkinPartType skinPartType, byte x, byte y, byte z) {
        ArrayList<SkinPart> skinParts = tileEntity.getSkinParts();
        for (int i = 0; i < skinParts.size(); i++) {
            if (skinParts.get(i).getPartType() == skinPartType) {
                ArrayList<ICube> cubeData = skinParts.get(i).getArmourData();
                for (int j = 0; j < cubeData.size(); j++) {
                    ICube curCube = cubeData.get(j);
                    if (curCube.getX() == x & curCube.getY() == y & curCube.getZ() == z) {
                        cubeData.remove(j);
                        return;
                    }
                }
                return;
            }
        }
    }
}
