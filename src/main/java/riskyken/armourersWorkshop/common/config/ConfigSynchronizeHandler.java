package riskyken.armourersWorkshop.common.config;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSyncConfig;

public final class ConfigSynchronizeHandler {
    
    public ConfigSynchronizeHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            MessageServerSyncConfig message = new MessageServerSyncConfig((EntityPlayer) event.entity);
            PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) event.entity);
        }
    }
    
    public static void resyncConfigs() {
        MessageServerSyncConfig message = new MessageServerSyncConfig();
        PacketHandler.networkWrapper.sendToAll(message);
    }
}
