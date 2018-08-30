package riskyken.armourersWorkshop.common.items.paintingtool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.blocks.BlockColourable;
import riskyken.armourersWorkshop.common.blocks.BlockLocation;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.AbstractModItem;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.painting.IBlockPainter;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.painting.tool.ToolOptions;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.BlockUtils;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import riskyken.armourersWorkshop.utils.UtilItems;

public class ItemBlendingTool extends AbstractModItem implements IConfigurableTool, IBlockPainter {

    public ItemBlendingTool() {
        super(LibItemNames.BLENDING_TOOL);
        MinecraftForge.EVENT_BUS.register(this);
        setSortPriority(14);
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.player;
        World world = event.player.worldObj;
        MovingObjectPosition target = event.target;
        
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
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block instanceof IPantableBlock) {
            if (!world.isRemote) {
                UndoManager.begin(player);
                usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, side);
                UndoManager.end(player);
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.BURN, 1.0F, 1.0F);
            }
            return true;
        }
        
        if (block == ModBlocks.armourerBrain & player.isSneaking()) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileEntityArmourer) {
                    ((TileEntityArmourer)te).toolUsedOnArmourer(this, world, stack, player);
                }
            }
            return true;
        }
        
        return false;
    }

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
                newR = MathHelper.clamp_int((int) newR, 0, 255);
                
                float newG = g / 100F * intensity;
                newG += oldG / 100F * (100 - intensity);
                newG = MathHelper.clamp_int((int) newG, 0, 255);
                
                float newB = b / 100F * intensity;
                newB += oldB / 100F * (100 - intensity);
                newB = MathHelper.clamp_int((int) newB, 0, 255);
                
                Color newC = new Color(
                        (int)newR,
                        (int)newG,
                        (int)newB);
                
                UndoManager.blockPainted(player, world, loc.x, loc.y, loc.z, oldColour, oldPaintType, side);
                ((IPantableBlock)block).setColour(world, loc.x, loc.y, loc.z, newC.getRGB(), side);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        int radiusSample = (Integer) ToolOptions.RADIUS_SAMPLE.readFromNBT(stack.getTagCompound(), 2);
        int radiusEffect = (Integer) ToolOptions.RADIUS_EFFECT.readFromNBT(stack.getTagCompound(), 1);
        
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.intensity", intensity));
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.radius", radiusSample, radiusSample, 1));
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.radius", radiusEffect, radiusEffect, 1));
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.openSettings"));
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote & player.isSneaking()) {
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, world, 0, 0, 0);
        }
        return stack;
    }
    
    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.INTENSITY);
        toolOptionList.add(ToolOptions.RADIUS_SAMPLE);
        toolOptionList.add(ToolOptions.RADIUS_EFFECT);
    }
}
