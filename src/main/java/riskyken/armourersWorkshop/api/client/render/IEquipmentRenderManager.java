package riskyken.armourersWorkshop.api.client.render;

import net.minecraft.tileentity.TileEntity;

import com.mojang.authlib.GameProfile;

/**
 * Used to handle rendering in the armourers workshop API.
 * To use create a class that implements IEquipmentRenderManager
 * then add this line to your mod's FMLInitializationEvent event.</BR>
 * </BR>
 * {@code FMLInterModComms.sendMessage("armourersWorkshop", "register", "full path to your class");}
 * 
 * @author RiskyKen
 *
 */
public interface IEquipmentRenderManager {
    
    public void onLoad(IEquipmentRenderHandler handler);
    
    public void onRenderMannequin(TileEntity TileEntity, GameProfile gameProfile);
}
