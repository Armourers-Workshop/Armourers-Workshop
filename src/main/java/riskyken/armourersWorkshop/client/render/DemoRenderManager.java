package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderManager;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentType;
import riskyken.armourersWorkshop.utils.ModLogger;

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
    public void onRenderEquipment(Entity entity, EnumEquipmentType armourType) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRenderEquipmentPart(Entity entity, EnumEquipmentPart armourPart) {
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
