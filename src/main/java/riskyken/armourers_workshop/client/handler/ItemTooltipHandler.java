package riskyken.armourers_workshop.client.handler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemTooltipHandler {

    public ItemTooltipHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    /*
    @SubscribeEvent
    public void onItemTooltipEvent (ItemTooltipEvent event) {
        if (event.itemStack == null) {
            return;
        }
        ItemSkin.addTooltipToSkinItem(event.itemStack, event.entityPlayer, event.toolTip, event.showAdvancedItemTooltips);
    }
    */
}
