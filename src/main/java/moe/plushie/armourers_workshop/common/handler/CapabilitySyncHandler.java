package moe.plushie.armourers_workshop.common.handler;

import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class CapabilitySyncHandler {
    
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityLivingBase) {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get((EntityLivingBase) event.getTarget());
            if (skinCapability != null) {
                skinCapability.syncToPlayerDelayed((EntityPlayerMP) event.getEntityPlayer(), 2);
            }
        }
        if (event.getTarget() instanceof EntityPlayer) {
            IWardrobeCap wardrobeCapability = WardrobeCap.get((EntityPlayer) event.getTarget());
            if (wardrobeCapability != null) {
                wardrobeCapability.syncToPlayerDelayed((EntityPlayerMP) event.getEntityPlayer(), 2);
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get((EntityLivingBase) event.player);
        if (skinCapability != null) {
            skinCapability.syncToPlayer((EntityPlayerMP) event.player);
        }
        
        IWardrobeCap wardrobeCapability = WardrobeCap.get(event.player);
        if (wardrobeCapability != null) {
            wardrobeCapability.syncToPlayer((EntityPlayerMP) event.player);
        }
    }
    
    /* @SubscribeEvent
    public static void onLivingDeathEvent (LivingDeathEvent  event) {
        if (!event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
            boolean dropSkins = true;
            MinecraftServer server = event.getEntity().getEntityWorld().getMinecraftServer();
            GameRules gr = getGameRules(server);
            boolean keepInventory = false;
            if (gr.hasRule("keepInventory")) {
                keepInventory = gr.getBoolean("keepInventory");
            }
            
            switch (ConfigHandler.dropSkinsOnDeath) {
            case 0:
                dropSkins = !keepInventory;
                break;
            case 1:
                dropSkins = false;
                break;
            case 2:
                dropSkins = true;
                break;
            default:
                dropSkins = !keepInventory;
                break;
            }

            ExPropsPlayerSkinData playerData = ExPropsPlayerSkinData.get((EntityPlayer) event.getEntity());
            if (dropSkins) {
                playerData.getWardrobeInventoryContainer().dropItems((EntityPlayer) event.getEntity());
            }
        }
    }
    
    private static GameRules getGameRules(MinecraftServer server) {
        return server.getWorld(0).getGameRules();
    }
    
    @SubscribeEvent
    public static void onLivingDeathEvent (PlayerEvent.Clone  event) {
        NBTTagCompound compound = new NBTTagCompound();
        ExPropsPlayerSkinData oldProps = ExPropsPlayerSkinData.get(event.getOriginal());
        ExPropsPlayerSkinData newProps = ExPropsPlayerSkinData.get(event.getEntityPlayer());
        oldProps.saveNBTData(compound);
        newProps.loadNBTData(compound);
    }*/
}
