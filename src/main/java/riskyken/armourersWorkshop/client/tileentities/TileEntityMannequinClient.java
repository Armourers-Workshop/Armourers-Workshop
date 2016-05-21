package riskyken.armourersWorkshop.client.tileentities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.render.EntityTextureInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

@SideOnly(Side.CLIENT)
public class TileEntityMannequinClient extends TileEntityMannequin {
    
    /** Keeps track if the inventory skins have been updated so the render can update. */
   private boolean skinsUpdated;
   
   /** Texture used when rendering this mannequin. */
   public EntityTextureInfo skinTexture;
   
   public ISkinPointer[] sp;
   
   public TileEntityMannequinClient() {
       super();
   }
   
   public TileEntityMannequinClient(boolean isDoll) {
       super(isDoll);
   }
   
   public boolean haveSkinsUpdated() {
       if (skinsUpdated) {
           skinsUpdated = false;
           return true;
       }
       return false;
   }
   
   private void setSkinsUpdated(boolean skinsUpdated) {
       this.skinsUpdated = skinsUpdated;
   }
   
   @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        super.setInventorySlotContents(i, itemstack);
        if (worldObj.isRemote) {
            setSkinsUpdated(true);
        }
    }
   
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        if (worldObj.isRemote) {
            setSkinsUpdated(true);
        }
    }
}
