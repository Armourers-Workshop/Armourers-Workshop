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
   
   private static String[] specialPeople = {
           "eba64cb1-0d29-4434-8d5e-31004b00488c", //RiskyKen
           "b027a4f4-d480-426c-84a3-a9cb029f4b72", //Vic
           "4fda0709-ada7-48a6-b4bf-0bbce8c40dfa", //Nanoha
           "5b6ab850-1b1a-45d0-9669-f84972f94d47", //EXTZ
           "b9e99f95-09fe-497a-8a77-1ccc839ab0f4"  //VermillionX
           };
 
   private static float[][] specialColours = {
           {249F / 255, 223F / 255, 140F / 255},
           {208F / 255, 212F / 255, 248F / 255},
           {1F, 173F / 255, 1F},
           {41F / 255, 25F / 255, 0F},
           {45F / 255, 45F / 255, 45F / 255}
           };
   
   public boolean hasSpecialRender() {
       if (gameProfile == null) {
           return false;
       }
       
       if (gameProfile.getId() == null) {
           return false;
       }
       
       for (int i = 0; i < specialPeople.length; i++) {
           if (gameProfile.getId().toString().equals(specialPeople[i])) {
               return true;
           }
       }
       
       return false;
   }
   
   public float[] getSpecialRenderColour() {
       float[] colour = new float[3];
       if (gameProfile == null) {
           return colour;
       }
       
       for (int i = 0; i < specialColours.length; i++) {
           if (gameProfile.getId().toString().equals(specialPeople[i])) {
               return specialColours[i];
           }
       }
       
       return colour;
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
