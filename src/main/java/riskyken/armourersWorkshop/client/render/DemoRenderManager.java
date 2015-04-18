package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderManager;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.utils.ModLogger;

import com.mojang.authlib.GameProfile;

public class DemoRenderManager implements IEquipmentRenderManager {

    private Minecraft mc;
    private IEquipmentRenderHandler handler;
    
    public DemoRenderManager() {
        //FMLCommonHandler.instance().bus().register(this);
        mc = Minecraft.getMinecraft();
    }
    
    @Override
    public void onLoad(IEquipmentRenderHandler handler) {
        this.handler = handler;
        ModLogger.log("Loaded DemoRenderManager");
    }

    @Override
    public void onRenderEquipmentPart(Entity entity, EnumEquipmentPart armourPart) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRenderMannequin(TileEntity TileEntity, GameProfile gameProfile) {
        // TODO Auto-generated method stub
        
    }
    
    /*
    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (Keybindings.undo.isPressed()) {
            ModLogger.log(player);
        }
    }
    */
}
