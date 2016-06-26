package riskyken.armourersWorkshop.common.config;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSyncConfig;

public final class ConfigSynchronizeHandler {
    
    public ConfigSynchronizeHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getEntity().worldObj.isRemote && event.getEntity() instanceof EntityPlayerMP) {
            MessageServerSyncConfig message = new MessageServerSyncConfig();
            PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) event.getEntity());
        }
    }
    
    public static void resyncConfigs() {
        MessageServerSyncConfig message = new MessageServerSyncConfig();
        PacketHandler.networkWrapper.sendToAll(message);
    }
}
