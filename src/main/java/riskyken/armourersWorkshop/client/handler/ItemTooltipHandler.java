package riskyken.armourersWorkshop.client.handler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemTooltipHandler {

    public ItemTooltipHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onItemTooltipEvent (ItemTooltipEvent event) {
        if (event.itemStack == null) {
            return;
        }
        ItemEquipmentSkin.addTooltipToSkinItem(event.itemStack, event.entityPlayer, event.toolTip, event.showAdvancedItemTooltips);
    }
}
