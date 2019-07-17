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
import riskyken.armourersWorkshop.common.addons.ModAddon.ItemOverrideType;
import riskyken.armourersWorkshop.common.addons.ModAddonManager;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerPlayerLeftTrackingRange;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
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
    
    public boolean isRenderItem(ItemOverrideType  type, Item item) {
        String itemName = Item.itemRegistry.getNameForObject(item);
        if (itemName != null && !itemName.isEmpty()) {
            String overrideToFind = type.toString().toLowerCase() + ":" + itemName;
            return ModAddonManager.itemOverrides.contains(overrideToFind);
        }
        return false;
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER & event.type == Type.PLAYER & event.phase == Phase.END) {
            EntityPlayer player = event.player;
            ExPropsPlayerSkinData props = ExPropsPlayerSkinData.get(player);
            if (props == null) {
                return;
            }
            updateWeaponNBT(player, props);
        }
    }
    
    private void updateWeaponNBT(EntityPlayer player, ExPropsPlayerSkinData props) {
        InventoryPlayer inventory = player.inventory;
        EntityEquipmentData equipmentData = props.getEquipmentData();
        ItemStack stack = inventory.getCurrentItem();
        if (stack != null) {
            if (isRenderItem(ItemOverrideType.SWORD, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 0)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 0));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.BOW, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinBow, 0)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinBow, 0));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.PICKAXE, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinPickaxe, 0)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinPickaxe, 0));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.AXE, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinAxe, 0)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinAxe, 0));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.SHOVEL, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinShovel, 0)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinShovel, 0));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.HOE, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinHoe, 0)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinHoe, 0));
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
            ExPropsPlayerSkinData.get((EntityPlayer) event.entity).sendCustomArmourDataToPlayer(targetPlayer);
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
        if (event.entity instanceof EntityPlayer && ExPropsPlayerSkinData.get((EntityPlayer) event.entity) == null) {
            ExPropsPlayerSkinData.register((EntityPlayer) event.entity);
        }
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            ExPropsPlayerSkinData playerData = ExPropsPlayerSkinData.get((EntityPlayer) event.entity);
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

            ExPropsPlayerSkinData playerData = ExPropsPlayerSkinData.get((EntityPlayer) event.entity);
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
        ExPropsPlayerSkinData oldProps = ExPropsPlayerSkinData.get(event.original);
        ExPropsPlayerSkinData newProps = ExPropsPlayerSkinData.get(event.entityPlayer);
        oldProps.saveNBTData(compound);
        newProps.loadNBTData(compound);
    }
}
