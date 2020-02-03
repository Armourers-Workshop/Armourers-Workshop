package moe.plushie.armourers_workshop.client.handler;

import moe.plushie.armourers_workshop.common.init.items.ItemSkin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemTooltipHandler {

    public ItemTooltipHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onItemTooltipEvent (ItemTooltipEvent event) {
        if (event.getItemStack() == ItemStack.EMPTY) {
            return;
        }
        ItemSkin.addTooltipToSkinItem(event.getItemStack(), event.getEntityPlayer(), event.getToolTip(), event.getFlags());
    }
}
