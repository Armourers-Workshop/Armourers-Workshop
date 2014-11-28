package riskyken.armourersWorkshop.common.equipment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.utils.HolidayHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;

public final class EntityEquipmentDataManager {
    
    public static final EntityEquipmentDataManager INSTANCE = new EntityEquipmentDataManager();
    
    public static void init() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER & event.type == Type.PLAYER & event.phase == Phase.END) {
            EntityPlayer player = event.player;
            ExtendedPropsPlayerEquipmentData props = ExtendedPropsPlayerEquipmentData.get(player);
            if (props == null) {
                return;
            }
            InventoryPlayer inventory = player.inventory;
            EntityEquipmentData equipmentData = props.getEquipmentData();
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemSword) {
                    
                    if (equipmentData.haveEquipment(EnumEquipmentType.WEAPON)) {
                        //ModLogger.log("tick");
                        if (stack.hasTagCompound()) {
                            
                            NBTTagCompound compound = stack.getTagCompound();
                            if (compound.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {
                                //Updating the skin data
                                NBTTagCompound armourData = compound.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
                                int newId = equipmentData.getEquipmentId(EnumEquipmentType.WEAPON);
                                int oldId = armourData.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
                                if (newId != oldId) {
                                    armourData.setInteger(LibCommonTags.TAG_EQUIPMENT_ID, newId);
                                    //ModLogger.log("update skin!");
                                }
                                compound.setTag(LibCommonTags.TAG_ARMOUR_DATA, armourData);
                            } else {
                                //Setting the skin data
                                NBTTagCompound armourData = new NBTTagCompound();
                                armourData.setInteger(LibCommonTags.TAG_EQUIPMENT_ID, equipmentData.getEquipmentId(EnumEquipmentType.WEAPON));
                                compound.setTag(LibCommonTags.TAG_ARMOUR_DATA, armourData);
                                //ModLogger.log("set skin!");
                            }

                        } else {
                            stack.setTagCompound(new NBTTagCompound());
                            NBTTagCompound compound = stack.getTagCompound();
                            NBTTagCompound armourData = new NBTTagCompound();
                            armourData.setInteger(LibCommonTags.TAG_EQUIPMENT_ID, equipmentData.getEquipmentId(EnumEquipmentType.WEAPON));
                            compound.setTag(LibCommonTags.TAG_ARMOUR_DATA, armourData);
                        }
                    } else {
                        
                        if (stack.hasTagCompound()) {
                            //Removing the skin data.
                            NBTTagCompound compound = stack.getTagCompound();
                            if (compound.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {
                                compound.removeTag(LibCommonTags.TAG_ARMOUR_DATA);
                                //ModLogger.log("remove skin!");
                            }
                        }
                    }
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
            ExtendedPropsPlayerEquipmentData playerData = ExtendedPropsPlayerEquipmentData.get((EntityPlayer) event.entity);
            playerData.dropItems();
        }
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
