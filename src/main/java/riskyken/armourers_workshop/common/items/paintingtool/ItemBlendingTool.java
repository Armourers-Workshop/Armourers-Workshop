package riskyken.armourers_workshop.common.items.paintingtool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.ArmourersWorkshop;
import riskyken.armourers_workshop.api.common.painting.IPantableBlock;
import riskyken.armourers_workshop.common.blocks.BlockLocation;
import riskyken.armourers_workshop.common.blocks.ModBlocks;
import riskyken.armourers_workshop.common.items.AbstractModItem;
import riskyken.armourers_workshop.common.lib.LibGuiIds;
import riskyken.armourers_workshop.common.lib.LibItemNames;
import riskyken.armourers_workshop.common.painting.IBlockPainter;
import riskyken.armourers_workshop.common.painting.tool.AbstractToolOption;
import riskyken.armourers_workshop.common.painting.tool.IConfigurableTool;
import riskyken.armourers_workshop.common.painting.tool.ToolOptions;
import riskyken.armourers_workshop.common.tileentities.TileEntityArmourer;
import riskyken.armourers_workshop.common.undo.UndoManager;
import riskyken.armourers_workshop.utils.TranslateUtils;
import riskyken.armourers_workshop.utils.UtilItems;

public class ItemBlendingTool extends AbstractModItem implements IConfigurableTool, IBlockPainter {

    public ItemBlendingTool() {
        super(LibItemNames.BLENDING_TOOL);
        //MinecraftForge.EVENT_BUS.register(this);
        setSortPriority(14);
    }
    /*
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.player;
        World world = event.player.worldObj;
        RayTraceResult target = event.getTarget();
        
        if (target != null && target.typeOfHit != MovingObjectType.BLOCK) {
            return;
        }
        
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        int side = target.sideHit;
        
        Block block = world.getBlock(x, y, z);
        ItemStack stack = player.getCurrentEquippedItem();
        
        if (stack == null || stack.getItem() != this) {
            return;
        }
        if (!(block instanceof BlockColourable)) {
            return;
        }
        
        int radiusSample = (Integer) ToolOptions.RADIUS_SAMPLE.readFromNBT(stack.getTagCompound(), 2);
        int radiusEffect = (Integer) ToolOptions.RADIUS_EFFECT.readFromNBT(stack.getTagCompound(), 1);
        
        ArrayList<BlockLocation> blockSamples = BlockUtils.findTouchingBlockFaces(world, x, y, z, side, radiusSample);
        ArrayList<BlockLocation> blockEffects = BlockUtils.findTouchingBlockFaces(world, x, y, z, side, radiusEffect);
        
        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
        float f1 = 0.002F;
        
        for (int i = 0; i < blockSamples.size(); i++) {
            int colour = 0xFF0000;
            BlockLocation blockLoc = blockSamples.get(i);
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(blockLoc.x, blockLoc.y, blockLoc.z, blockLoc.x + 1, blockLoc.y + 1, blockLoc.z + 1);
            aabb.offset(-xOff, -yOff, -zOff);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.2F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            RenderGlobal.drawOutlinedBoundingBox(aabb.expand(f1, f1, f1), colour);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
        for (int i = 0; i < blockEffects.size(); i++) {
            BlockLocation blockLoc = blockEffects.get(i);
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(blockLoc.x + 0.10F, blockLoc.y + 0.10F, blockLoc.z + 0.10F, blockLoc.x + 0.90F, blockLoc.y + 0.90F, blockLoc.z + 0.90F);
            aabb.offset(-xOff, -yOff, -zOff);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.2F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            //GL11.glDepthMask(false);
            RenderGlobal.drawOutlinedBoundingBox(aabb.expand(f1, f1, f1), 0x00FF00);
            //GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND); 
        }
        event.setCanceled(true);
    }
    */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        ItemStack stack = player.getHeldItem(hand);

        if (state.getBlock() instanceof IPantableBlock) {
            if (!worldIn.isRemote) {
                UndoManager.begin(player);
                usedOnBlockSide(stack, player, worldIn, new BlockLocation(pos.getX(), pos.getY(), pos.getZ()), state.getBlock(), facing.getIndex());
                UndoManager.end(player);
                //worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, LibSounds.BURN, 1.0F, 1.0F);
            }
            return EnumActionResult.SUCCESS;
        }
        
        if (state.getBlock() == ModBlocks.armourerBrain & player.isSneaking()) {
            if (!worldIn.isRemote) {
                TileEntity te = worldIn.getTileEntity(pos);
                if (te != null && te instanceof TileEntityArmourer) {
                    ((TileEntityArmourer)te).toolUsedOnArmourer(this, worldIn, stack, player);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
    /*
    @SuppressWarnings("deprecation")
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block, int side) {
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        int radiusSample = (Integer) ToolOptions.RADIUS_SAMPLE.readFromNBT(stack.getTagCompound(), 2);
        int radiusEffect = (Integer) ToolOptions.RADIUS_EFFECT.readFromNBT(stack.getTagCompound(), 1);
        
        ArrayList<BlockLocation> blockSamples = BlockUtils.findTouchingBlockFaces(world, bl.x, bl.y, bl.z, side, radiusSample);
        ArrayList<BlockLocation> blockEffects = BlockUtils.findTouchingBlockFaces(world, bl.x, bl.y, bl.z, side, radiusEffect);
        
        if (blockSamples.size() == 0 | blockEffects.size() == 0) {
            return;
        }
        
        int r = 0;
        int g = 0;
        int b = 0;
        
        for (int i = 0; i < blockSamples.size(); i++) {
            BlockLocation loc = blockSamples.get(i);
            Block tarBlock = world.getBlock(loc.x, loc.y, loc.z);
            if (tarBlock instanceof IPantableBlock) {
                IPantableBlock pBlock = (IPantableBlock) tarBlock;
                ICubeColour c = pBlock.getColour(world, loc.x, loc.y, loc.z);
                r += c.getRed(side) & 0xFF;
                g += c.getGreen(side) & 0xFF;
                b += c.getBlue(side) & 0xFF;
            }
        }
        
        r = r / blockSamples.size();
        g = g / blockSamples.size();
        b = b / blockSamples.size();
        
        
        
        for (int i = 0; i < blockEffects.size(); i++) {
            BlockLocation loc = blockEffects.get(i);
            Block tarBlock = world.getBlock(loc.x, loc.y, loc.z);
            if (tarBlock instanceof IPantableBlock) {
                IPantableBlock worldColourable = (IPantableBlock) tarBlock;
                int oldColour = worldColourable.getColour(world, loc.x, loc.y, loc.z, side);
                byte oldPaintType = (byte) worldColourable.getPaintType(world, loc.x, loc.y, loc.z, side).getKey();
                
                Color oldC = new Color(oldColour);
                int oldR = oldC.getRed();
                int oldG = oldC.getGreen();
                int oldB = oldC.getBlue();
                
                float newR = r / 100F * intensity;
                newR += oldR / 100F * (100 - intensity);
                newR = MathHelper.clamp((int) newR, 0, 255);
                
                float newG = g / 100F * intensity;
                newG += oldG / 100F * (100 - intensity);
                newG = MathHelper.clamp((int) newG, 0, 255);
                
                float newB = b / 100F * intensity;
                newB += oldB / 100F * (100 - intensity);
                newB = MathHelper.clamp((int) newB, 0, 255);
                
                Color newC = new Color(
                        (int)newR,
                        (int)newG,
                        (int)newB);
                
                UndoManager.blockPainted(player, world, loc.x, loc.y, loc.z, oldColour, oldPaintType, side);
                ((IPantableBlock)block).setColour(world, loc.x, loc.y, loc.z, newC.getRGB(), side);
            }
        }
    }
    */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        int radiusSample = (Integer) ToolOptions.RADIUS_SAMPLE.readFromNBT(stack.getTagCompound(), 2);
        int radiusEffect = (Integer) ToolOptions.RADIUS_EFFECT.readFromNBT(stack.getTagCompound(), 1);
        
        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.intensity", intensity));
        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.radius", radiusSample, radiusSample, 1));
        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.radius", radiusEffect, radiusEffect, 1));
        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.openSettings"));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (worldIn.isRemote & playerIn.isSneaking()) {
            playerIn.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, worldIn, 0, 0, 0);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
    
    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.INTENSITY);
        toolOptionList.add(ToolOptions.RADIUS_SAMPLE);
        toolOptionList.add(ToolOptions.RADIUS_EFFECT);
    }
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block,
            int side) {
        // TODO Auto-generated method stub
        
    }
}
