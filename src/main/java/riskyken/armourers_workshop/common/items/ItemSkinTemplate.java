package riskyken.armourers_workshop.common.items;

import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.common.lib.LibItemNames;
import riskyken.armourers_workshop.common.skin.ISkinHolder;
import riskyken.armourers_workshop.common.skin.data.Skin;
import riskyken.armourers_workshop.utils.SkinNBTHelper;

public class ItemSkinTemplate extends AbstractModItem implements ISkinHolder {
    
    private static final String TAG_OWNER = "owner";
    
    public ItemSkinTemplate() {
        super(LibItemNames.EQUIPMENT_SKIN_TEMPLATE);
        setMaxStackSize(64);
        setHasSubtypes(true);
    }
    /*
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (stack.getItemDamage() == 1000) {
                ItemStack giftStack = new ItemStack(ModBlocks.doll, 1);
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.writeGameProfile(profileTag, player.getGameProfile());
                giftStack.setTagCompound(new NBTTagCompound());
                giftStack.getTagCompound().setTag(TAG_OWNER, profileTag);
                if (player.inventory.addItemStackToInventory(giftStack)) {
                    stack.stackSize--;
                } else {
                    player.sendMessage(new TextComponentString(I18n.format("chat.armourersworkshop:inventoryFull")));
                }
            }
        }
        return super.onItemRightClick(stack, world, player);
    }
*/
    @Override
    public ItemStack makeStackForEquipment(Skin armourItemData) {
        return SkinNBTHelper.makeEquipmentSkinStack(armourItemData);
    }
}
