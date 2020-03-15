package moe.plushie.armourers_workshop.common.capability;

import java.util.concurrent.Callable;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinProvider;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinStorage;
import moe.plushie.armourers_workshop.common.capability.holiday.HolidayTrackCap;
import moe.plushie.armourers_workshop.common.capability.holiday.IHolidayTrackCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeProvider;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeStorage;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeProvider;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeStorage;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.entity.SkinnableEntityRegisty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@Mod.EventBusSubscriber(modid = LibModInfo.ID)
public final class ModCapabilityManager {

    private static final ResourceLocation KEY_ENTITY_SKIN_PROVIDER = new ResourceLocation(LibModInfo.ID, "entity-skin-provider");
    private static final ResourceLocation KEY_WARDROBE_PROVIDER = new ResourceLocation(LibModInfo.ID, "wardrobe-provider");
    private static final ResourceLocation KEY_PLAYER_WARDROBE_PROVIDER = new ResourceLocation(LibModInfo.ID, "player-wardrobe-provider");
    private static final ResourceLocation KEY_HOLIDAY_TRACKER = new ResourceLocation(LibModInfo.ID, "holiday-tracker");

    private ModCapabilityManager() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IEntitySkinCapability.class, new EntitySkinStorage(), new Callable<IEntitySkinCapability>() {

            @Override
            public IEntitySkinCapability call() throws Exception {
                return null;
            }
        });

        CapabilityManager.INSTANCE.register(IWardrobeCap.class, new WardrobeStorage(), new Callable<IWardrobeCap>() {

            @Override
            public IWardrobeCap call() throws Exception {
                return null;
            }
        });

        CapabilityManager.INSTANCE.register(IPlayerWardrobeCap.class, new PlayerWardrobeStorage(), new Callable<IPlayerWardrobeCap>() {

            @Override
            public IPlayerWardrobeCap call() throws Exception {
                return null;
            }
        });
        CapabilityManager.INSTANCE.register(IHolidayTrackCap.class, new HolidayTrackCap.Storage(), new HolidayTrackCap.Factory());
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Entity)) {
            return;
        }
        Entity entity = event.getObject();
        ISkinnableEntity skinnableEntity = SkinnableEntityRegisty.INSTANCE.getSkinnableEntity(entity);
        if (skinnableEntity == null) {
            return;
        }
        event.addCapability(KEY_ENTITY_SKIN_PROVIDER, new EntitySkinProvider(entity, skinnableEntity));
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            event.addCapability(KEY_PLAYER_WARDROBE_PROVIDER, new PlayerWardrobeProvider(player, skinnableEntity));
            event.addCapability(KEY_HOLIDAY_TRACKER, new HolidayTrackCap.Provider());
        } else {
            event.addCapability(KEY_WARDROBE_PROVIDER, new WardrobeProvider(entity, skinnableEntity));
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(event.getTarget());
        if (skinCapability != null) {
            skinCapability.syncToPlayer((EntityPlayerMP) event.getEntityPlayer());
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
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(event.player);
        if (skinCapability != null) {
            skinCapability.syncToPlayer((EntityPlayerMP) event.player);
        }

        IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get(event.player);
        if (wardrobeCapability != null) {
            wardrobeCapability.syncToPlayer((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
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
                skinCapability.getSkinInventoryContainer().dropItems(event.getEntityLiving().getEntityWorld(), event.getEntityLiving().getPositionVector());
            }
        }
    }

    private static GameRules getGameRules(MinecraftServer server) {
        return server.getWorld(0).getGameRules();
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        World world = event.getEntityPlayer().getEntityWorld();
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

    @SubscribeEvent
    public static void onRespawn(PlayerRespawnEvent event) {
        // Called after onPlayerClone. Used to sync after death.
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(event.player);
        wardrobeCap.syncToAllTracking();
        wardrobeCap.syncToPlayer((EntityPlayerMP) event.player);

        IEntitySkinCapability skinCap = EntitySkinCapability.get(event.player);
        skinCap.syncToAllTracking();
        skinCap.syncToPlayer((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerChangedDimensionEvent event) {
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(event.player);
        wardrobeCap.syncToPlayer((EntityPlayerMP) event.player);

        IEntitySkinCapability skinCap = EntitySkinCapability.get(event.player);
        skinCap.syncToPlayer((EntityPlayerMP) event.player);
    }
}
