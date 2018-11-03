package moe.plushie.armourers_workshop.common.items.block;

import java.util.List;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
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
    
    /*
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player,
            World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        
        if (canPlaceBlockHere(stack, player, world, x, y, z, side, hitX, hitY, hitZ, false)) {
            if (canPlaceBlockHere(stack, player, world, x, y, z, side, hitX, hitY, hitZ, true)) {
                return true;
            }
        }
        
        return false;
    }
    */
    /*
    private boolean canPlaceBlockHere(ItemStack stack, EntityPlayer player,
            World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, boolean place) {
        
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.SNOW_LAYER && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if (block != Blocks.VINE && block != Blocks.TALLGRASS && block != Blocks.DEADBUSH && !block.isReplaceable(world, x, y, z))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }
        
        if (!place) {
            y++;
        }

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!player.canPlayerEdit(x, y, z, side, stack))
        {
            return false;
        }
        else if (y == 255 && this.block.getMaterial().isSolid())
        {
            return false;
        }
        else if (world.canPlaceEntityOnSide(this.block, x, y, z, false, side, player, stack))
        {
            int i1 = this.getMetadata(stack.getItemDamage());
            int j1 = this.block.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, i1);
            
            if (place) {
                if (placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, j1))
                {
                    world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), this.block.stepSound.func_150496_b(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getPitch() * 0.8F);
                    --stack.stackSize;
                }
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }*/
}
