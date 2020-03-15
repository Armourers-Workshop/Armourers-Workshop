package moe.plushie.armourers_workshop.common.init.items.paintingtool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.init.items.AbstractModItem;
import moe.plushie.armourers_workshop.common.init.sounds.ModSounds;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.painting.IBlockPainter;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOption;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOptions;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import moe.plushie.armourers_workshop.common.world.undo.UndoManager;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlendingTool extends AbstractModItem implements IConfigurableTool, IBlockPainter {

    public ItemBlendingTool() {
        super(LibItemNames.BLENDING_TOOL);
        setCreativeTab(ArmourersWorkshop.TAB_PAINTING_TOOLS);
        MinecraftForge.EVENT_BUS.register(this);
        setSortPriority(14);
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.getPlayer();
        World world = event.getPlayer().getEntityWorld();
        RayTraceResult target = event.getTarget();
        
        if (target != null && target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }
        

        
        BlockPos pos = target.getBlockPos();
        EnumFacing facing = target.sideHit;
        IBlockState stateTarget = world.getBlockState(pos);
        ItemStack stack = player.getHeldItemMainhand();
        
        if (stack.getItem() != this) {
            return;
        }
        if (!(stateTarget.getBlock() instanceof IPantableBlock)) {
            return;
        }
        
        int radiusSample = ToolOptions.RADIUS_SAMPLE.getValue(stack);
        int radiusEffect = ToolOptions.RADIUS_EFFECT.getValue(stack);
        boolean restrictPlane = ToolOptions.PLANE_RESTRICT.getValue(stack);
        
        ArrayList<BlockPos> blockSamples = BlockUtils.findTouchingBlockFaces(world, pos, facing, radiusSample, restrictPlane);
        ArrayList<BlockPos> blockEffects = BlockUtils.findTouchingBlockFaces(world, pos, facing, radiusEffect, restrictPlane);
        
        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
        float f1 = 0.002F;
        
        for (BlockPos posSample : blockSamples) {
            AxisAlignedBB aabb = new AxisAlignedBB(posSample, posSample.add(1, 1, 1));
            aabb = aabb.offset(-xOff, -yOff, -zOff);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.glLineWidth(2F);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            RenderGlobal.drawSelectionBoundingBox(aabb.expand(f1, f1, f1), 1F, 0F, 0F, 0.5F);
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();;
        }
        
        for (BlockPos posEffect : blockEffects) {
            AxisAlignedBB aabb = new AxisAlignedBB(posEffect, posEffect.add(1, 1, 1));
            aabb = aabb.offset(0.1F, 0.1F, 0.1F).contract(0.2F, 0.2F, 0.2F);
            aabb = aabb.offset(-xOff, -yOff, -zOff);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.glLineWidth(2F);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            RenderGlobal.drawSelectionBoundingBox(aabb.expand(f1, f1, f1), 0F, 1F, 0F, 0.5F);
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();;
        }
        
        event.setCanceled(true);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        ItemStack stack = player.getHeldItem(hand);

        if (state.getBlock() instanceof IPantableBlock) {
            if (!worldIn.isRemote) {
                UndoManager.begin(player);
                usedOnBlockSide(stack, player, worldIn, pos, state.getBlock(), facing, false);
                UndoManager.end(player);
                worldIn.playSound(null, pos, ModSounds.PAINT, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.2F + 0.9F);
            }
            return EnumActionResult.SUCCESS;
        }
        
        if (state.getBlock() == ModBlocks.ARMOURER & player.isSneaking()) {
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
    
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing face, boolean spawnParticles) {
        int intensity = ToolOptions.INTENSITY.getValue(stack);
        int radiusSample = ToolOptions.RADIUS_SAMPLE.getValue(stack);
        int radiusEffect = ToolOptions.RADIUS_EFFECT.getValue(stack);
        boolean restrictPlane = ToolOptions.PLANE_RESTRICT.getValue(stack);
        
        ArrayList<BlockPos> blockSamples = BlockUtils.findTouchingBlockFaces(world, pos, face, radiusSample, restrictPlane);
        ArrayList<BlockPos> blockEffects = BlockUtils.findTouchingBlockFaces(world, pos, face, radiusEffect, restrictPlane);
        
        if (blockSamples.size() == 0 | blockEffects.size() == 0) {
            return;
        }
        
        int r = 0;
        int g = 0;
        int b = 0;
        
        int validSamples = 0;
        
        for (BlockPos posSample : blockSamples) {
            IBlockState stateTarget = world.getBlockState(posSample);
            if (stateTarget.getBlock() instanceof IPantableBlock) {
                IPantableBlock pBlock = (IPantableBlock) stateTarget.getBlock();
                ICubeColour c = pBlock.getColour(world, posSample);
                if (c != null) {
                    r += c.getRed(face.ordinal()) & 0xFF;
                    g += c.getGreen(face.ordinal()) & 0xFF;
                    b += c.getBlue(face.ordinal()) & 0xFF;
                    validSamples++;
                }
            }
        }
        
        if (validSamples == 0) {
            return;
        }
        
        r = r / validSamples;
        g = g / validSamples;
        b = b / validSamples;
        
        for (BlockPos posEffect : blockEffects) {
            IBlockState stateTarget = world.getBlockState(posEffect);
            if (stateTarget.getBlock() instanceof IPantableBlock) {
                IPantableBlock pBlock = (IPantableBlock) stateTarget.getBlock();
                int oldColour = pBlock.getColour(world, posEffect, face);
                byte oldPaintType = (byte) pBlock.getPaintType(world, posEffect, face).getId();
                
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
                
                UndoManager.blockPainted(player, world, posEffect, oldColour, oldPaintType, face);
                ((IPantableBlock)block).setColour(world, posEffect, newC.getRGB(), face);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        int intensity = ToolOptions.INTENSITY.getValue(stack);
        int radiusSample = ToolOptions.RADIUS_SAMPLE.getValue(stack);
        int radiusEffect = ToolOptions.RADIUS_EFFECT.getValue(stack);
        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.intensity", intensity));
        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.radius.sample", radiusSample, radiusSample, 1));
        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.radius.effect", radiusEffect, radiusEffect, 1));
        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.openSettings"));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (worldIn.isRemote & playerIn.isSneaking()) {
            playerIn.openGui(ArmourersWorkshop.getInstance(), EnumGuiId.TOOL_OPTIONS.ordinal(), worldIn, 0, 0, 0);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
    
    @Override
    public void getToolOptions(ArrayList<ToolOption<?>> toolOptionList) {
        toolOptionList.add(ToolOptions.INTENSITY);
        toolOptionList.add(ToolOptions.RADIUS_SAMPLE);
        toolOptionList.add(ToolOptions.RADIUS_EFFECT);
        //toolOptionList.add(ToolOptions.CHANGE_HUE);
        //toolOptionList.add(ToolOptions.CHANGE_SATURATION);
        //toolOptionList.add(ToolOptions.CHANGE_BRIGHTNESS);
        toolOptionList.add(ToolOptions.PLANE_RESTRICT);
        //toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
        
    }
}
