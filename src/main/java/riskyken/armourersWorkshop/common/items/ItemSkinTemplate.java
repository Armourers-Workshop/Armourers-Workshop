package riskyken.armourersWorkshop.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.ISkinHolder;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSkinTemplate extends AbstractModItem implements ISkinHolder {
    
    private static final String TAG_OWNER = "owner";
    
    public ItemSkinTemplate() {
        super(LibItemNames.EQUIPMENT_SKIN_TEMPLATE);
        setMaxStackSize(64);
        setHasSubtypes(true);
    }
    
    @SideOnly(Side.CLIENT)
    IIcon giftIcon;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.TEMPLATE_BLANK);
        giftIcon = register.registerIcon(LibItemResources.GIFT_SACK);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (stack.getItemDamage() == 1000) {
                ItemStack giftStack = new ItemStack(ModBlocks.doll, 1);
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.func_152460_a(profileTag, player.getGameProfile());
                giftStack.setTagCompound(new NBTTagCompound());
                giftStack.getTagCompound().setTag(TAG_OWNER, profileTag);
                if (player.inventory.addItemStackToInventory(giftStack)) {
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

    @Override
    public ItemStack makeStackForEquipment(Skin armourItemData) {
        return SkinNBTHelper.makeEquipmentSkinStack(armourItemData);
    }
}
