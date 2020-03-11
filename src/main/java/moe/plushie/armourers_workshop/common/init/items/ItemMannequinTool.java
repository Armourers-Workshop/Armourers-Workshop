package moe.plushie.armourers_workshop.common.init.items;

import java.util.List;

import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.init.blocks.BlockMannequin;
import moe.plushie.armourers_workshop.common.init.blocks.BlockMannequin.EnumPartType;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.utils.NBTHelper;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMannequinTool extends AbstractModItem {
    
    private static final String TAG_ROTATION_DATA = "rotationData";

    public ItemMannequinTool() {
        super(LibItemNames.MANNEQUIN_TOOL);
        setSortPriority(10);
    }
    
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == ModBlocks.MANNEQUIN | state.getBlock() == ModBlocks.DOLL) {
            if (state.getBlock() == ModBlocks.MANNEQUIN) {
                if (state.getValue(BlockMannequin.STATE_PART) == EnumPartType.TOP) {
                    pos = pos.offset(EnumFacing.DOWN);
                }
            }
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityMannequin) {
                TileEntityMannequin teMan = (TileEntityMannequin) te;
                if (player.isSneaking()) {
                    setRotationDataOnStack(stack, teMan.PROP_BIPED_ROTATIONS.get());
                    return EnumActionResult.SUCCESS;
                } else {
                    BipedRotations bipedRotations = getRotationDataFromStack(stack);
                    if (bipedRotations != null) {
                        teMan.PROP_BIPED_ROTATIONS.set(bipedRotations);
                        return EnumActionResult.SUCCESS;
                    }
                }
                
            }
        } else if (side == EnumFacing.UP) {
            if (!world.isRemote) {
                pos = pos.offset(side);
                int l = MathHelper.floor(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
                EntityMannequin entityMannequin = new EntityMannequin(world);
                entityMannequin.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
                double angle = TrigUtils.getAngleDegrees(player.posX, player.posZ, pos.getX() + 0.5F, pos.getZ() + 0.5F) + 90D;
                entityMannequin.setRotation((float) angle);
                entityMannequin.setScale(0.25F + world.rand.nextFloat() * 9.45F);
                //entityMannequin.setTextureData(new TextureData(player.getGameProfile()));
                world.spawnEntity(entityMannequin);
            }
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_ROTATION_DATA, NBT.TAG_COMPOUND)) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.settingsSaved"));
        } else {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.noSettingsSaved"));
        }
    }
}
