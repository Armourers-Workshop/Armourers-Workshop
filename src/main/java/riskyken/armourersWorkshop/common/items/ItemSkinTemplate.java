package riskyken.armourersWorkshop.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.ISkinHolder;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ItemSkinTemplate extends AbstractModItem implements ISkinHolder {
    
    private static final String TAG_OWNER = "owner";
    
    public ItemSkinTemplate() {
        super(LibItemNames.EQUIPMENT_SKIN_TEMPLATE);
        setMaxStackSize(64);
        setHasSubtypes(true);
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
    public ItemStack makeStackForEquipment(Skin armourItemData) {
        return SkinNBTHelper.makeEquipmentSkinStack(armourItemData);
    }
}
