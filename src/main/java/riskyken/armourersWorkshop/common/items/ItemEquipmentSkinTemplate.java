package riskyken.armourersWorkshop.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEquipmentSkinTemplate extends AbstractModItem {
    
    public ItemEquipmentSkinTemplate() {
        super(LibItemNames.EQUIPMENT_SKIN_TEMPLATE);
        setMaxStackSize(64);
        setHasSubtypes(true);
    }
    
    @SideOnly(Side.CLIENT)
    IIcon giftIcon;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibModInfo.ID + ":" + "templateBlank");
        giftIcon = register.registerIcon(LibModInfo.ID + ":" + "giftSack");
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (stack.getItemDamage() == 1000) {
                ItemStack[] gifts = { new ItemStack(Items.cake, 1), new ItemStack(Items.golden_apple, 1),
                        new ItemStack(Items.painting, 1)};
                
                if (player.inventory.addItemStackToInventory(gifts[world.rand.nextInt(gifts.length)].copy())) {
                    stack.stackSize--;
                } else {
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("chat.armourersworkshop:inventoryFull")));
                }
            }
        }
        return super.onItemRightClick(stack, world, player);
    }
    
    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage == 1000) {
            return giftIcon;
        }
        return super.getIconFromDamage(damage);
    }
}
