package riskyken.armourersWorkshop.common.skin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import riskyken.armourersWorkshop.common.capability.IWardrobeCapability;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerPlayerLeftTrackingRange;
import riskyken.armourersWorkshop.utils.HolidayHelper;

public final class EntityEquipmentDataManager {
    
    public static EntityEquipmentDataManager INSTANCE;
    
    @CapabilityInject(IWardrobeCapability.class)
    private static final Capability<IWardrobeCapability> WARDROBE_CAP = null;
    public static ResourceLocation WARDROBE_KEY = new ResourceLocation(LibModInfo.ID, "playerWardrobe");
    
    public static void init() {
        INSTANCE = new EntityEquipmentDataManager();
    }
    
    public EntityEquipmentDataManager() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent.Entity event) {
        if (event.getEntity() instanceof EntityPlayer & getEffectiveSide() == Side.SERVER) {
            //event.addCapability(WARDROBE_KEY, new WardrobeProvider((EntityPlayer) event.getEntity()));
        }
    }
    
    public boolean isSwordRenderItem(Item item) {
        /*
        UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(item);
        if (ui != null) {
            for (int i = 0; i < Addons.overrideSwordsActive.length; i++) {
                if (Addons.overrideSwordsActive[i].equals(ui.toString())) {
                    return true;
                }
            }
        }
        */
        return false;
    }
    
    public boolean isBowRenderItem(Item item) {
        /*
        UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(item);
        if (ui != null) {
            for (int i = 0; i < Addons.overrideBowsActive.length; i++) {
                if (Addons.overrideBowsActive[i].equals(ui.toString())) {
                    return true;
                }
            }
        }
        */
        return false;
    }
    /*
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER & event.type == Type.PLAYER & event.phase == Phase.END) {
            EntityPlayer player = event.player;
            ExPropsPlayerEquipmentData props = ExPropsPlayerEquipmentData.get(player);
            if (props == null) {
                return;
            }
            updateWeaponNBT(player, props);
        }
    }
    
    private void updateWeaponNBT(EntityPlayer player, ExPropsPlayerEquipmentData props) {
        InventoryPlayer inventory = player.inventory;
        EntityEquipmentData equipmentData = props.getEquipmentData();
        ItemStack stack = inventory.getCurrentItem();
        if (stack != null) {
            if (isSwordRenderItem(stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 0)) {
                    SkinNBTHelper.addRenderIdToStack(
                            stack, SkinTypeRegistry.skinSword,
                            equipmentData.getEquipmentId(SkinTypeRegistry.skinSword, 0),
                            equipmentData.getSkinDye(SkinTypeRegistry.skinSword, 0));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isBowRenderItem(stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinBow, 0)) {
                    SkinNBTHelper.addRenderIdToStack(
                            stack, SkinTypeRegistry.skinBow,
                            equipmentData.getEquipmentId(SkinTypeRegistry.skinBow, 0),
                            equipmentData.getSkinDye(SkinTypeRegistry.skinBow, 0));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
        }
    }
    */
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityPlayerMP && event.getEntity()instanceof EntityPlayer) {
            EntityPlayer sourcePlayer = (EntityPlayer) event.getEntity();
            EntityPlayerMP targetPlayer = (EntityPlayerMP) event.getTarget();
            IWardrobeCapability wardrobe = sourcePlayer.getCapability(WARDROBE_CAP, null);
            if (wardrobe != null) {
                wardrobe.sendWardrobeDataToPlayer(targetPlayer);
            }
        }
    }
    
    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking event) {
        if (event.getTarget() instanceof EntityPlayerMP) {
            EntityPlayerMP target = (EntityPlayerMP) event.getTarget();
            MessageServerPlayerLeftTrackingRange message = new MessageServerPlayerLeftTrackingRange(new PlayerPointer(target));
            PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) event.getEntityPlayer());
        }
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getEntity().worldObj.isRemote && event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
            IWardrobeCapability wardrobe = player.getCapability(WARDROBE_CAP, null);
            if (wardrobe != null) {
                wardrobe.sendWardrobeDataToPlayer(player);
            }
            HolidayHelper.giftPlayer(player);
        }
    }
    
    /*
    @SubscribeEvent
    public void onLivingDeathEvent (LivingDeathEvent  event) {
        if (!event.getEntity().worldObj.isRemote && event.getEntity() instanceof EntityPlayerMP) {
            boolean dropSkins = true;
            
            GameRules gr = getGameRules();
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

            ExPropsPlayerEquipmentData playerData = ExPropsPlayerEquipmentData.get((EntityPlayer) event.getEntity());
            if (dropSkins) {
                playerData.getWardrobeInventoryContainer().dropItems((EntityPlayer) event.getEntity());
            }
        }
    }
    
    private GameRules getGameRules() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0).getGameRules();
    }
    
    @SubscribeEvent
    public void onLivingDeathEvent (PlayerEvent.Clone  event) {
        NBTTagCompound compound = new NBTTagCompound();
        ExPropsPlayerEquipmentData oldProps = ExPropsPlayerEquipmentData.get(event.getOriginal());
        ExPropsPlayerEquipmentData newProps = ExPropsPlayerEquipmentData.get(event.getEntityPlayer());
        oldProps.saveNBTData(compound);
        newProps.loadNBTData(compound);
    }
    */
    
    private Side getEffectiveSide() {
        return FMLCommonHandler.instance().getEffectiveSide();
    }
}
