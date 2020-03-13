package moe.plushie.armourers_workshop.common.init.items.block;

import java.util.List;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockMannequin extends ModItemBlock {
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_IMAGE_URL = "imageUrl";
    
    public ItemBlockMannequin(Block block) {
        super(block);
        setMaxStackSize(1);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            GameProfile gameProfile = null;
            if (compound.hasKey(TAG_OWNER, 10)) {
                gameProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag(TAG_OWNER));
                String user = TranslateUtils.translate("item.armourers_workshop:rollover.user", gameProfile.getName());
                tooltip.add(user);
            }
            if (compound.hasKey(TAG_IMAGE_URL, NBT.TAG_STRING)) {
                String imageUrl = compound.getString(TAG_IMAGE_URL);
                String urlLine = TranslateUtils.translate("item.armourers_workshop:rollover.url", imageUrl);
                tooltip.add(urlLine);
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
