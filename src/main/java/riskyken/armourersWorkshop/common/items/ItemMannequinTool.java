package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.data.BipedRotations;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.utils.NBTHelper;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class ItemMannequinTool extends AbstractModItem {
    
    private static final String TAG_ROTATION_DATA = "rotationData";

    public ItemMannequinTool() {
        super(LibItemNames.MANNEQUIN_TOOL);
    }
    
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
            EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState block = worldIn.getBlockState(pos);
        if (block != null && (block == ModBlocks.mannequin | block == ModBlocks.doll)) {
            /*
            TileEntity te;
            int meta = world.getBlockMetadata(x, y, z);
            if (meta == 0) {
                te = world.getTileEntity(x, y, z);
            } else {
                te = world.getTileEntity(x, y - 1, z);
            }
            if (te != null && te instanceof TileEntityMannequin) {
                TileEntityMannequin teMan = (TileEntityMannequin) te;
                if (playerIn.isSneaking()) {
                    setRotationDataOnStack(stack, teMan.getBipedRotations());
                } else {
                    BipedRotations bipedRotations = getRotationDataFromStack(stack);
                    if (bipedRotations != null) {
                        teMan.setBipedRotations(bipedRotations);
                    }
                }
                return EnumActionResult.PASS;
            }
            */
        }
        return EnumActionResult.FAIL;
    }
    
    private BipedRotations getRotationDataFromStack(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }
        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(TAG_ROTATION_DATA)) {
            return null;
        }
        NBTTagCompound rotationCompound = compound.getCompoundTag(TAG_ROTATION_DATA);
        BipedRotations bipedRotations = new BipedRotations();
        bipedRotations.loadNBTData(rotationCompound);
        return bipedRotations;
    }
    
    private void setRotationDataOnStack(ItemStack stack, BipedRotations bipedRotations) {
        NBTTagCompound compound = NBTHelper.getNBTForStack(stack);
        NBTTagCompound rotationCompound = new NBTTagCompound();
        bipedRotations.saveNBTData(rotationCompound);
        compound.setTag(TAG_ROTATION_DATA, rotationCompound);
        stack.setTagCompound(compound);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        if (stack.hasTagCompound()) {
            String settingsSaved = TranslateUtils.translate("item.armourersworkshop:rollover.settingsSaved");
            list.add(settingsSaved);
        } else {
            String noSettingsSaved = TranslateUtils.translate("item.armourersworkshop:rollover.noSettingsSaved");
            list.add(noSettingsSaved);
        }
    }
}
