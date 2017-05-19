package riskyken.armourersWorkshop.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;

public class ItemSkinUnlock extends AbstractModItem {

    private final ISkinType[] VALID_SKINS = {
            SkinTypeRegistry.skinHead,
            SkinTypeRegistry.skinChest,
            SkinTypeRegistry.skinLegs,
            SkinTypeRegistry.skinFeet
            };
    
    public ItemSkinUnlock() {
        super(LibItemNames.SKIN_UNLOCK);
        setHasSubtypes(true);
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 0; i < VALID_SKINS.length;i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        //itemIcon = register.registerIcon(LibItemResources.SKIN_UNLOCK);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return itemStack;
        }
        ISkinType skinType = getSkinTypeFormStack(itemStack);
        
        ExPropsPlayerEquipmentData equipmentData = ExPropsPlayerEquipmentData.get(player);
        int count = equipmentData.getEquipmentWardrobeData().getUnlockedSlotsForSkinType(skinType);
        count++;
        
        if (count <= ExPropsPlayerEquipmentData.MAX_SLOTS_PER_SKIN_TYPE) {
            equipmentData.setSkinColumnCount(skinType, count);
            ModLogger.log(skinType);
            player.addChatComponentMessage(new ChatComponentText("DEBUG: unlocked slot for " + skinType.getRegistryName()));
        } else {
            player.addChatComponentMessage(new ChatComponentText("DEBUG: slots already at max"));
        }
        

        
        return itemStack;
    }
    
    private ISkinType getSkinTypeFormStack(ItemStack itemStack) {
        int damage = itemStack.getItemDamage();
        if (damage >= 0 & damage < VALID_SKINS.length) {
            return VALID_SKINS[damage];
        }
        return VALID_SKINS[0];
    }
}
