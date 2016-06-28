package riskyken.armourersWorkshop.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibItemNames;

/*@Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraft|Core")*/
public class ItemArmourersHammer extends AbstractModItem /*implements IToolWrench*/ {

    public ItemArmourersHammer() {
        super(LibItemNames.ARMOURERS_HAMMER);
    }
    
    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world,
            BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        /*
        Block block = world.getBlock(pos);
        if (block != null) {
            if (block instanceof BlockBed) {
                rotateBed(world, pos, (BlockBed) block, ForgeDirection.getOrientation(side));
                player.swingArm(hand);;
                return !world.isRemote;
            }
            
            if (block instanceof BlockChest) {
                return EnumActionResult.FAIL;
            }
            
            EnumFacing dir = EnumFacing.getOrientation(side);
            if (player.isSneaking()) {
                dir = dir.getOpposite();
            }
            if (block.rotateBlock(world, x, y, z, dir)) {
                player.swingArm(hand);;
                return !world.isRemote;
            }
        }
        */
        return EnumActionResult.FAIL;
    }
    /*
    private boolean rotateBed(World world, int x, int y, int z, BlockBed block, ForgeDirection axis) {
        int meta = world.getBlockMetadata(x, y, z);
        ForgeDirection[] bedRots = {
                ForgeDirection.SOUTH,
                ForgeDirection.WEST,
                ForgeDirection.NORTH,
                ForgeDirection.EAST};
        
        int bedDir = ((BlockBed)block).getDirection(meta);
        
        ForgeDirection bedRot = bedRots[bedDir];
        ForgeDirection otherHalf = bedRot;
        boolean isHead = block.isBlockHeadOfBed(meta);
        if (isHead) {
            otherHalf = bedRot.getOpposite();
        }
        
        if (world.getBlock(x + otherHalf.offsetX, y + otherHalf.offsetY, z + otherHalf.offsetZ) == block) {
            int otherMeta = world.getBlockMetadata(x + otherHalf.offsetX, y + otherHalf.offsetY, z + otherHalf.offsetZ);
            int newMeta = (meta & 3) + 2 % 3;
            if (!isHead) {
                newMeta += 8;
            }
            world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
            newMeta = (otherMeta & 3) + 2 % 3;
            if (isHead) {
                newMeta += 8;
            }
            world.setBlockMetadataWithNotify(x + otherHalf.offsetX, y + otherHalf.offsetY, z + otherHalf.offsetZ, newMeta, 3);
            return true;
        }
        return false;
    }*/
    
    /*
    @Optional.Method(modid = "BuildCraft|Core")
    @Override
    public boolean canWrench(EntityPlayer player, int x, int y, int z) {
        return true;
    }
    
    @Optional.Method(modid = "BuildCraft|Core")
    @Override
    public void wrenchUsed(EntityPlayer player, int x, int y, int z) {
        player.swingItem();
    }
    */
    
    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }
}
