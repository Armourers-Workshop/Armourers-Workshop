package moe.plushie.armourers_workshop.common.handler;

import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class CapabilitySyncHandler {
    
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityLivingBase) {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get((EntityLivingBase) event.getTarget());
            if (skinCapability != null) {
                skinCapability.syncToPlayer((EntityPlayerMP) event.getEntityPlayer());
            }
        }
        if (event.getTarget() instanceof EntityPlayer) {
            IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get((EntityPlayer) event.getTarget());
            if (wardrobeCapability != null) {
                wardrobeCapability.syncToPlayer((EntityPlayerMP) event.getEntityPlayer());
            }
        } else {
            IWardrobeCap wardrobeCapability = WardrobeCap.get(event.getTarget());
            if (wardrobeCapability != null) {
                wardrobeCapability.syncToPlayer((EntityPlayerMP) event.getEntityPlayer());
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get((EntityLivingBase) event.player);
        if (skinCapability != null) {
            skinCapability.syncToPlayer((EntityPlayerMP) event.player);
        }
        
        IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get(event.player);
        if (wardrobeCapability != null) {
            wardrobeCapability.syncToPlayer((EntityPlayerMP) event.player);
        }
    }
    
    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent  event) {
        if (!event.getEntity().getEntityWorld().isRemote & event.getEntityLiving() instanceof EntityPlayer) {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get(event.getEntityLiving());
            if (skinCapability == null) {
                return;
            }
            boolean dropSkins = true;
            MinecraftServer server = event.getEntity().getEntityWorld().getMinecraftServer();
            GameRules gr = getGameRules(server);
            boolean keepInventory = false;
            if (gr.hasRule("keepInventory")) {
                keepInventory = gr.getBoolean("keepInventory");
            }
            
            switch (ConfigHandler.wardrobeDropSkinsOnDeath) {
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
            if (dropSkins) {
                skinCapability.getSkinInventoryContainer().dropItems((EntityPlayer) event.getEntityLiving());
            }
        }
    }
    
    private static GameRules getGameRules(MinecraftServer server) {
        return server.getWorld(0).getGameRules();
    }
    
    @SubscribeEvent
    public static void onRespawn(PlayerRespawnEvent event) {
        if (!event.isEndConquered()) {
            IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(event.player);
            wardrobeCap.syncToAllTracking();
            wardrobeCap.syncToPlayer((EntityPlayerMP) event.player);
            
            IEntitySkinCapability skinCap = EntitySkinCapability.get(event.player);
            skinCap.syncToAllTracking();
            skinCap.syncToPlayer((EntityPlayerMP) event.player);
        }
    }
    
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        World world = event.getEntityPlayer().getEntityWorld();
        if (event.isWasDeath()) {
            NBTBase nbt = null;
            IPlayerWardrobeCap wardrobeCapOld = PlayerWardrobeCap.get(event.getOriginal());
            IPlayerWardrobeCap wardrobeCapNew = PlayerWardrobeCap.get(event.getEntityPlayer());
            IStorage<IPlayerWardrobeCap> storageWardrobe = PlayerWardrobeCap.PLAYER_WARDROBE_CAP.getStorage();
            nbt = storageWardrobe.writeNBT(PlayerWardrobeCap.PLAYER_WARDROBE_CAP, wardrobeCapOld, null);
            storageWardrobe.readNBT(PlayerWardrobeCap.PLAYER_WARDROBE_CAP, wardrobeCapNew, null, nbt);
            
            IEntitySkinCapability skinCapOld = EntitySkinCapability.get(event.getOriginal());
            IEntitySkinCapability skinCapNew = EntitySkinCapability.get(event.getEntityPlayer());
            IStorage<IEntitySkinCapability> storageEntitySkin = EntitySkinCapability.ENTITY_SKIN_CAP.getStorage();
            nbt = storageEntitySkin.writeNBT(EntitySkinCapability.ENTITY_SKIN_CAP, skinCapOld, null);
            storageEntitySkin.readNBT(EntitySkinCapability.ENTITY_SKIN_CAP, skinCapNew, null, nbt);
        }
    }
}
