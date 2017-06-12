package riskyken.armourersWorkshop.common.skin;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import riskyken.armourersWorkshop.common.addons.ModAddonManager;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerPlayerLeftTrackingRange;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.HolidayHelper;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public final class EntityEquipmentDataManager {
    
    public static EntityEquipmentDataManager INSTANCE;
    
    public static void init() {
        INSTANCE = new EntityEquipmentDataManager();
    }
    
    public EntityEquipmentDataManager() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public boolean isSwordRenderItem(Item item) {
        String itemName = item.itemRegistry.getNameForObject(item);
        if (itemName != null && !itemName.equals("")) {
            for (int i = 0; i < ModAddonManager.itemOverrides.size(); i++) {
                if (ModAddonManager.itemOverrides.get(i).equals("sword:" + itemName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isBowRenderItem(Item item) {
        String itemName = item.itemRegistry.getNameForObject(item);
        if (itemName != null && !itemName.equals("")) {
            for (int i = 0; i < ModAddonManager.itemOverrides.size(); i++) {
                if (ModAddonManager.itemOverrides.get(i).equals("bow:" + itemName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
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
    
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.target instanceof EntityPlayerMP) {
            EntityPlayerMP targetPlayer = (EntityPlayerMP) event.target;
            ExPropsPlayerEquipmentData.get((EntityPlayer) event.entity).sendCustomArmourDataToPlayer(targetPlayer);
        }
    }
    
    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking event) {
        if (event.target instanceof EntityPlayerMP) {
            EntityPlayerMP target = (EntityPlayerMP) event.target;
            MessageServerPlayerLeftTrackingRange message = new MessageServerPlayerLeftTrackingRange(new PlayerPointer(target));
            PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) event.entityPlayer);
        }
    }
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && ExPropsPlayerEquipmentData.get((EntityPlayer) event.entity) == null) {
            ExPropsPlayerEquipmentData.register((EntityPlayer) event.entity);
        }
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            ExPropsPlayerEquipmentData playerData = ExPropsPlayerEquipmentData.get((EntityPlayer) event.entity);
            playerData.sendCustomArmourDataToPlayer((EntityPlayerMP) event.entity);
            HolidayHelper.giftPlayer((EntityPlayerMP) event.entity);
        }
    }
    
    @SubscribeEvent
    public void onLivingDeathEvent (LivingDeathEvent  event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            boolean dropSkins = true;
            
            GameRules gr = getGameRules();
            boolean keepInventory = false;
            if (gr.hasRule("keepInventory")) {
                keepInventory = gr.getGameRuleBooleanValue("keepInventory");
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

            ExPropsPlayerEquipmentData playerData = ExPropsPlayerEquipmentData.get((EntityPlayer) event.entity);
            if (dropSkins) {
                playerData.getWardrobeInventoryContainer().dropItems((EntityPlayer) event.entity);
            }
        }
    }
    
    private GameRules getGameRules() {
        return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
    }
    
    @SubscribeEvent
    public void onLivingDeathEvent (PlayerEvent.Clone  event) {
        NBTTagCompound compound = new NBTTagCompound();
        ExPropsPlayerEquipmentData oldProps = ExPropsPlayerEquipmentData.get(event.original);
        ExPropsPlayerEquipmentData newProps = ExPropsPlayerEquipmentData.get(event.entityPlayer);
        oldProps.saveNBTData(compound);
        newProps.loadNBTData(compound);
    }
}
