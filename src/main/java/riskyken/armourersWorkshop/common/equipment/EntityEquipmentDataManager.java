package riskyken.armourersWorkshop.common.equipment;

import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.HolidayHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;

public final class EntityEquipmentDataManager {
    
    public static EntityEquipmentDataManager INSTANCE;
    
    private final HashSet<String> swordSkinItems;
    private final HashSet<String> bowSkinItems;
    
    public static void init() {
        INSTANCE = new EntityEquipmentDataManager();
    }
    
    public EntityEquipmentDataManager() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        swordSkinItems = new HashSet<String>();
        bowSkinItems = new HashSet<String>();
    }
    
    public void addSwordRenderClass(String className) {
        swordSkinItems.add(className);
    }
    
    public void addBowRenderClass(String className) {
        bowSkinItems.add(className);
    }
    
    private boolean isSwordRenderItem(Item item) {
        if (swordSkinItems.contains(item.getClass().getName())) {
            return true;
        }
        if (item instanceof ItemSword) {
            return true;
        }
        return false;
    }
    
    private boolean isBowRenderItem(Item item) {
        if (bowSkinItems.contains(item.getClass().getName())) {
            return true;
        }
        if (item instanceof ItemBow) {
            return true;
        }
        return false;
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER & event.type == Type.PLAYER & event.phase == Phase.END) {
            EntityPlayer player = event.player;
            ExtendedPropsPlayerEquipmentData props = ExtendedPropsPlayerEquipmentData.get(player);
            if (props == null) {
                return;
            }
            updateWeaponNBT(player, props);
        }
    }
    
    private void updateWeaponNBT(EntityPlayer player, ExtendedPropsPlayerEquipmentData props) {
        InventoryPlayer inventory = player.inventory;
        EntityEquipmentData equipmentData = props.getEquipmentData();
        ItemStack stack = inventory.getCurrentItem();
        if (stack != null) {
            if (isSwordRenderItem(stack.getItem())) {
                if (equipmentData.haveEquipment(EnumEquipmentType.SWORD)) {
                    EquipmentNBTHelper.addRenderIdToStack(stack, equipmentData.getEquipmentId(EnumEquipmentType.SWORD));
                } else {
                    EquipmentNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isBowRenderItem(stack.getItem())) {
                if (equipmentData.haveEquipment(EnumEquipmentType.BOW)) {
                    EquipmentNBTHelper.addRenderIdToStack(stack, equipmentData.getEquipmentId(EnumEquipmentType.BOW));
                } else {
                    EquipmentNBTHelper.removeRenderIdFromStack(stack);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.target instanceof EntityPlayerMP) {
            EntityPlayerMP targetPlayer = (EntityPlayerMP) event.target;
            ExtendedPropsPlayerEquipmentData.get((EntityPlayer) event.entity).sendCustomArmourDataToPlayer(targetPlayer);
        }
    }
    
    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking event) {
        if (event.target instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.entity;
        }
    }
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && ExtendedPropsPlayerEquipmentData.get((EntityPlayer) event.entity) == null) {
            ExtendedPropsPlayerEquipmentData.register((EntityPlayer) event.entity);
        }
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            ExtendedPropsPlayerEquipmentData playerData = ExtendedPropsPlayerEquipmentData.get((EntityPlayer) event.entity);
            playerData.sendCustomArmourDataToPlayer((EntityPlayerMP) event.entity);
            HolidayHelper.giftPlayer((EntityPlayerMP) event.entity);
        }
    }
    
    @SubscribeEvent
    public void onLivingDeathEvent (LivingDeathEvent  event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            GameRules gr = getGameRules();
            boolean keepInventory = false;
            if (gr.hasRule("keepInventory")) {
                keepInventory = gr.getGameRuleBooleanValue("keepInventory");
            }
            ExtendedPropsPlayerEquipmentData playerData = ExtendedPropsPlayerEquipmentData.get((EntityPlayer) event.entity);
            if (!keepInventory) {
                playerData.dropItems();
            }
        }
    }
    
    private GameRules getGameRules() {
        return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
    }
    
    @SubscribeEvent
    public void onLivingDeathEvent (PlayerEvent.Clone  event) {
        NBTTagCompound compound = new NBTTagCompound();
        ExtendedPropsPlayerEquipmentData oldProps = ExtendedPropsPlayerEquipmentData.get(event.original);
        ExtendedPropsPlayerEquipmentData newProps = ExtendedPropsPlayerEquipmentData.get(event.entityPlayer);
        oldProps.saveNBTData(compound);
        newProps.loadNBTData(compound);
    }
}
