package riskyken.armourersWorkshop.common.equipment;

import net.minecraft.client.Minecraft;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataManager;

public class DemoDataManager implements IEquipmentDataManager {
    
    private Minecraft mc;
    private IEquipmentDataHandler dataHandler;
    
    public DemoDataManager() {
        //FMLCommonHandler.instance().bus().register(this);
        this.mc = Minecraft.getMinecraft();
    }
    
    @Override
    public void onLoad(IEquipmentDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
    /*
    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        EntityClientPlayerMP player = mc.thePlayer;
        if (Keybindings.undo.isPressed()) {
            IEntityEquipment data = dataHandler.getCustomEquipmentForEntity(player);
            ModLogger.log(data.haveEquipment(EnumEquipmentType.SKIRT));
            ModLogger.log(data.getEquipmentId(EnumEquipmentType.SKIRT));
        }
    }
    */
}
