package riskyken.armourersWorkshop.client.handler;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemTooltipHandler {

    public ItemTooltipHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onItemTooltipEvent (ItemTooltipEvent event) {
        if (event.itemStack == null) {
            return;
        }
        if (event.itemStack.getItem() == ModItems.equipmentSkin) {
            return;
        }
        ItemStack stack = event.itemStack;
        List<String> list = event.toolTip;
        
        String cGray = EnumChatFormatting.GRAY.toString();
        String cGold = EnumChatFormatting.GOLD.toString();
        String cYellow = EnumChatFormatting.YELLOW.toString();
        
        if (EquipmentNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = EquipmentNBTHelper.getSkinPointerFromStack(stack);
            if (skinData.lockSkin) {
                list.add(cGold + "Has armouerer's skin" + cGray);
                if (ClientEquipmentModelCache.INSTANCE.isEquipmentInCache(skinData.skinId)) {
                    Skin data = ClientEquipmentModelCache.INSTANCE.getEquipmentItemData(skinData.skinId);
                    if (!data.getCustomName().trim().isEmpty()) {
                        list.add(cGold + "Name: " + cGray + data.getCustomName());
                    }
                    if (!data.getAuthorName().trim().isEmpty()) {
                        list.add(cGold + "Author: " + cGray + data.getAuthorName());
                    }
                } else {
                    list.add("Downloading skin...");
                }
            }
        }
    }
}
