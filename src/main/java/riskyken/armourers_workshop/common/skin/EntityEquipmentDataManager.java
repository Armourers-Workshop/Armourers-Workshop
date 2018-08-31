package riskyken.armourers_workshop.common.skin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;
import riskyken.armourers_workshop.common.addons.ModAddon.ItemOverrideType;
import riskyken.armourers_workshop.common.addons.ModAddonManager;
import riskyken.armourers_workshop.common.config.ConfigHandler;
import riskyken.armourers_workshop.common.skin.data.SkinPointer;
import riskyken.armourers_workshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourers_workshop.utils.SkinNBTHelper;

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
        ResourceLocation itemName = item.REGISTRY.getNameForObject(item);
        if (itemName != null) {
            for (int i = 0; i < ModAddonManager.itemOverrides.size(); i++) {
                if (ModAddonManager.itemOverrides.get(i).equals(type.toString().toLowerCase() + ":" + itemName)) {
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
            ExPropsPlayerSkinData props = ExPropsPlayerSkinData.get(player);
            if (props == null) {
                return;
            }
            //updateWeaponNBT(player, props);
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
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 1)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 1));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.AXE, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 2)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 2));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.SHOVEL, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 3)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 3));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.HOE, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 4)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinPointer) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 4));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof EntityPlayerMP) {
            //EntityPlayerMP targetPlayer = (EntityPlayerMP) event.getTarget();
            //ExPropsPlayerSkinData.get((EntityPlayer) event.getEntity()).sendCustomArmourDataToPlayer(targetPlayer);
        }
    }
    
    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking event) {
        if (event.getTarget() instanceof EntityPlayerMP) {
            EntityPlayerMP target = (EntityPlayerMP) event.getTarget();
            //MessageServerPlayerLeftTrackingRange message = new MessageServerPlayerLeftTrackingRange(new PlayerPointer(target));
            //PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) event.getEntityPlayer());
        }
    }
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.getEntity() instanceof EntityPlayer && ExPropsPlayerSkinData.get((EntityPlayer) event.getEntity()) == null) {
            //ExPropsPlayerSkinData.register((EntityPlayer) event.getEntity());
        }
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
            //ExPropsPlayerSkinData playerData = ExPropsPlayerSkinData.get((EntityPlayer) event.getEntity());
            //playerData.sendCustomArmourDataToPlayer((EntityPlayerMP) event.getEntity());
            //HolidayHelper.giftPlayer((EntityPlayerMP) event.getEntity());
        }
    }
    
    //@SubscribeEvent
    public void onLivingDeathEvent (LivingDeathEvent  event) {
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
    
    private GameRules getGameRules(MinecraftServer server) {
        return server.getWorld(0).getGameRules();
    }
    
    //@SubscribeEvent
    public void onLivingDeathEvent (PlayerEvent.Clone  event) {
        NBTTagCompound compound = new NBTTagCompound();
        ExPropsPlayerSkinData oldProps = ExPropsPlayerSkinData.get(event.getOriginal());
        ExPropsPlayerSkinData newProps = ExPropsPlayerSkinData.get(event.getEntityPlayer());
        oldProps.saveNBTData(compound);
        newProps.loadNBTData(compound);
    }
}
