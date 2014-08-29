package riskyken.armourersWorkshop.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ModForgeEventHandler {
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        if (ArmourersWorkshop.proxy.playerHasSkirt(player.getDisplayName())) {
            if (player.limbSwingAmount > 0.25F) {
                player.limbSwingAmount = 0.25F;
            }
        }
    }
}
