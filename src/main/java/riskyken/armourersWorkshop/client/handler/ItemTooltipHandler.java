package riskyken.armourersWorkshop.client.handler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.items.ItemSkin;

@SideOnly(Side.CLIENT)
public class ItemTooltipHandler {

    public ItemTooltipHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onItemTooltipEvent (ItemTooltipEvent event) {
        if (event.getItemStack() == null) {
            return;
        }
        ItemSkin.addTooltipToSkinItem(event.getItemStack(), event.getEntityPlayer(), event.getToolTip(), event.isShowAdvancedItemTooltips());
    }
}
