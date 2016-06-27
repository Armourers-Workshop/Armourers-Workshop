package riskyken.armourersWorkshop.common.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
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
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (!worldIn.isRemote) {
            if (itemStackIn.getItemDamage() == 1000) {
                ItemStack giftStack = new ItemStack(ModBlocks.doll, 1);
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.writeGameProfile(profileTag, playerIn.getGameProfile());
                giftStack.setTagCompound(new NBTTagCompound());
                giftStack.getTagCompound().setTag(TAG_OWNER, profileTag);
                if (playerIn.inventory.addItemStackToInventory(giftStack)) {
                    itemStackIn.stackSize--;
                } else {
                    playerIn.addChatMessage(new TextComponentString(I18n.format("chat.armourersworkshop:inventoryFull")));
                }
            }
        }
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
    }

    @Override
    public ItemStack makeStackForEquipment(Skin armourItemData) {
        return SkinNBTHelper.makeEquipmentSkinStack(armourItemData);
    }
}
