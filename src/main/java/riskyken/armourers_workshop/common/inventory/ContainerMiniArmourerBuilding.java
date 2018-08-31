package riskyken.armourers_workshop.common.inventory;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.api.common.skin.type.ISkinPartType;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.common.network.PacketHandler;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerMiniArmourerCubeEdit;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerMiniArmourerSkinData;
import riskyken.armourers_workshop.common.skin.data.SkinPart;
import riskyken.armourers_workshop.common.tileentities.TileEntityMiniArmourer;

public class ContainerMiniArmourerBuilding extends Container {

    private TileEntityMiniArmourer tileEntity;
    private ArrayList<SkinPart> skinParts;
    
    public ContainerMiniArmourerBuilding(TileEntityMiniArmourer tileEntity) {
        this.tileEntity = tileEntity;
        skinType = tileEntity.getSkinType();
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotID) {
        return null;
    }
    
    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        MessageServerMiniArmourerSkinData message;
        message = new MessageServerMiniArmourerSkinData(tileEntity.getSkinParts());
        if (listener instanceof EntityPlayerMP) {
            PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) listener);
        }
    }
    
    ISkinType skinType;
    
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (skinType == tileEntity.getSkinType() & !tileEntity.getWorld().isRemote) {
            return;
        }
        MessageServerMiniArmourerSkinData message;
        message = new MessageServerMiniArmourerSkinData(tileEntity.getSkinParts());
        for (int i = 0; i < listeners.size(); i++) {
            IContainerListener crafter = (IContainerListener) listeners.get(i);
            PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) crafter);
        }
        skinType = tileEntity.getSkinType();
    }


    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return tileEntity.isUsableByPlayer(entityPlayer);
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
    
    public void updateFromClientCubeEdit(ISkinPartType skinPartType, riskyken.armourers_workshop.common.data.MiniCube cube, boolean remove) {
        ArrayList<SkinPart> skinParts = tileEntity.getSkinParts();
        /*
        for (int i = 0; i < skinParts.size(); i++) {
            if (skinParts.get(i).getPartType() == skinPartType) {
                ArrayList<ICube> cubeData = skinParts.get(i).getArmourData();
                for (int j = 0; j < cubeData.size(); j++) {
                    ICube curCube = cubeData.get(j);
                    if (curCube.getX() == cube.getX() & curCube.getY() == cube.getY() & curCube.getZ() == cube.getZ()) {
                        cubeData.remove(j);
                        break;
                    }
                }
                if (!remove) {
                    cubeData.add(cube);
                }
                break;
            }
        }
        */
        //Send the cube update to all the players that have the GUI open.
        for (int i = 0; i < listeners.size(); i++) {
            IContainerListener crafter = (IContainerListener) listeners.get(i);
            MessageServerMiniArmourerCubeEdit message;
            message = new MessageServerMiniArmourerCubeEdit(skinPartType, cube, remove);
            PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) crafter);
        }
        tileEntity.markDirty();
    }
}
