package moe.plushie.armourers_workshop.common.skin;

import moe.plushie.armourers_workshop.common.addons.ModAddon.ItemOverrideType;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;

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
        return ModAddonManager.isOverrideItem(type, item);
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
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinDescriptor) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 0));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.BOW, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinBow, 0)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinDescriptor) equipmentData.getSkinPointer(SkinTypeRegistry.skinBow, 0));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.PICKAXE, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 1)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinDescriptor) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 1));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.AXE, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 2)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinDescriptor) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 2));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.SHOVEL, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 3)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinDescriptor) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 3));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
            if (isRenderItem(ItemOverrideType.HOE, stack.getItem())) {
                if (equipmentData.haveEquipment(SkinTypeRegistry.skinSword, 4)) {
                    SkinNBTHelper.addSkinPointerToStack(stack, (SkinDescriptor) equipmentData.getSkinPointer(SkinTypeRegistry.skinSword, 4));
                } else {
                    SkinNBTHelper.removeRenderIdFromStack(stack);
                }
            }
        }
    }
    
}
