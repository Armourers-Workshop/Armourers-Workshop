package riskyken.armourersWorkshop.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class ItemSkinUnlock extends AbstractModItem {

    private final ISkinType[] VALID_SKINS = {
            SkinTypeRegistry.skinHead,
            SkinTypeRegistry.skinChest,
            SkinTypeRegistry.skinLegs,
            SkinTypeRegistry.skinFeet,
            SkinTypeRegistry.skinWings
            };
    
    public ItemSkinUnlock() {
        super(LibItemNames.SKIN_UNLOCK);
        setHasSubtypes(true);
        setSortPriority(7);
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 0; i < VALID_SKINS.length;i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }
    
    @SideOnly(Side.CLIENT)
    IIcon iconChest;
    @SideOnly(Side.CLIENT)
    IIcon iconLegs;
    @SideOnly(Side.CLIENT)
    IIcon iconFeet;
    @SideOnly(Side.CLIENT)
    IIcon iconWings;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.SKIN_UNLOCK_HEAD);
        iconChest = register.registerIcon(LibItemResources.SKIN_UNLOCK_CHEST);
        iconLegs = register.registerIcon(LibItemResources.SKIN_UNLOCK_LEGS);
        iconFeet = register.registerIcon(LibItemResources.SKIN_UNLOCK_FEET);
        iconWings = register.registerIcon(LibItemResources.SKIN_UNLOCK_WINGS);
    }
    
    @Override
    public IIcon getIconFromDamage(int damage) {
        switch (damage) {
        case 0:
            return itemIcon;
        case 1:
            return iconChest;
        case 2:
            return iconLegs;
        case 3:
            return iconFeet;
        case 4:
            return iconWings;
        default:
            return itemIcon;
        }
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
        
        String localizedSkinName = SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinType);
        
        if (count <= ExPropsPlayerEquipmentData.MAX_SLOTS_PER_SKIN_TYPE) {
            equipmentData.setSkinColumnCount(skinType, count);
            player.addChatComponentMessage(new ChatComponentTranslation("chat.armourersworkshop:slotUnlocked", localizedSkinName.toLowerCase(), Integer.toString(count)));
            itemStack.stackSize--;
        } else {
            player.addChatComponentMessage(new ChatComponentTranslation("chat.armourersworkshop:slotUnlockedFailed", localizedSkinName));
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
