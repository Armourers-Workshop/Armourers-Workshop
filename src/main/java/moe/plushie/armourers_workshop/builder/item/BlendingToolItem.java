package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintingTool;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.function.Consumer;

public class BlendingToolItem extends AbstractPaintingToolItem implements IBlockPaintViewer {


    public BlendingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.INTENSITY);
        builder.accept(ToolOptions.RADIUS_SAMPLE);
        builder.accept(ToolOptions.RADIUS_EFFECT);
        //toolOptionList.add(ToolOptions.CHANGE_HUE);
        //toolOptionList.add(ToolOptions.CHANGE_SATURATION);
        //toolOptionList.add(ToolOptions.CHANGE_BRIGHTNESS);
        builder.accept(ToolOptions.PLANE_RESTRICT);
        //toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
    }

    @Override
    public void appendSettingHoverText(ItemStack itemStack, List<ITextComponent> tooltips, ITooltipFlag flags) {
        int intensity = ToolOptions.INTENSITY.get(itemStack);
        int radiusSample = ToolOptions.RADIUS_SAMPLE.get(itemStack);
        int radiusEffect = ToolOptions.RADIUS_EFFECT.get(itemStack);
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.intensity", intensity));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.sampleRadius", radiusSample, radiusSample, 1));
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.effectRadius", radiusEffect, radiusEffect, 1));
        super.appendSettingHoverText(itemStack, tooltips, flags);
    }
}

//public class ItemBlendingTool extends AbstractModItem implements IConfigurableTool, IBlockPainter {
//
//    public ItemBlendingTool() {
//        super(LibItemNames.BLENDING_TOOL);
//        setCreativeTab(ArmourersWorkshop.TAB_PAINTING_TOOLS);
//        MinecraftForge.EVENT_BUS.register(this);
//        setSortPriority(14);
//    }
//
//    @SubscribeEvent
//    @SideOnly(Side.CLIENT)
//    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
//        EntityPlayer player = event.getPlayer();
//        World world = event.getPlayer().getEntityWorld();
//        RayTraceResult target = event.getTarget();
//
//        if (target != null && target.typeOfHit != RayTraceResult.Type.BLOCK) {
//            return;
//        }
//
//
//
//        BlockPos pos = target.getBlockPos();
//        EnumFacing facing = target.sideHit;
//        IBlockState stateTarget = world.getBlockState(pos);
//        ItemStack stack = player.getHeldItemMainhand();
//
//        if (stack.getItem() != this) {
//            return;
//        }
//        if (!(stateTarget.getBlock() instanceof IPantableBlock)) {
//            return;
//        }
//
//        int radiusSample = ToolOptions.RADIUS_SAMPLE.getValue(stack);
//        int radiusEffect = ToolOptions.RADIUS_EFFECT.getValue(stack);
//        boolean restrictPlane = ToolOptions.PLANE_RESTRICT.getValue(stack);
//
//        ArrayList<BlockPos> blockSamples = BlockUtils.findTouchingBlockFaces(world, pos, facing, radiusSample, restrictPlane);
//        ArrayList<BlockPos> blockEffects = BlockUtils.findTouchingBlockFaces(world, pos, facing, radiusEffect, restrictPlane);
//
//        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
//        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
//        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
//        float f1 = 0.002F;
//
//        for (BlockPos posSample : blockSamples) {
//            AxisAlignedBB aabb = new AxisAlignedBB(posSample, posSample.add(1, 1, 1));
//            aabb = aabb.offset(-xOff, -yOff, -zOff);
//            GlStateManager.enableBlend();
//            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
//            GlStateManager.glLineWidth(2F);
//            GlStateManager.disableDepth();
//            GlStateManager.disableTexture2D();
//            GlStateManager.disableAlpha();
//            RenderGlobal.drawSelectionBoundingBox(aabb.expand(f1, f1, f1), 1F, 0F, 0F, 0.5F);
//            GlStateManager.enableAlpha();
//            GlStateManager.enableTexture2D();
//            GlStateManager.enableDepth();
//            GlStateManager.disableBlend();;
//        }
//
//        for (BlockPos posEffect : blockEffects) {
//            AxisAlignedBB aabb = new AxisAlignedBB(posEffect, posEffect.add(1, 1, 1));
//            aabb = aabb.offset(0.1F, 0.1F, 0.1F).contract(0.2F, 0.2F, 0.2F);
//            aabb = aabb.offset(-xOff, -yOff, -zOff);
//            GlStateManager.enableBlend();
//            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
//            GlStateManager.glLineWidth(2F);
//            GlStateManager.disableDepth();
//            GlStateManager.disableTexture2D();
//            GlStateManager.disableAlpha();
//            RenderGlobal.drawSelectionBoundingBox(aabb.expand(f1, f1, f1), 0F, 1F, 0F, 0.5F);
//            GlStateManager.enableAlpha();
//            GlStateManager.enableTexture2D();
//            GlStateManager.enableDepth();
//            GlStateManager.disableBlend();;
//        }
//
//        event.setCanceled(true);
//    }
//
//    @Override
//    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        IBlockState state = worldIn.getBlockState(pos);
//        ItemStack stack = player.getHeldItem(hand);
//
//        if (state.getBlock() instanceof IPantableBlock) {
//            if (!worldIn.isRemote) {
//                UndoManager.begin(player);
//                usedOnBlockSide(stack, player, worldIn, pos, state.getBlock(), facing, false);
//                UndoManager.end(player);
//                worldIn.playSound(null, pos, ModSounds.PAINT, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.2F + 0.9F);
//            }
//            return EnumActionResult.SUCCESS;
//        }
//
//        if (state.getBlock() == ModBlocks.ARMOURER & player.isSneaking()) {
//            if (!worldIn.isRemote) {
//                TileEntity te = worldIn.getTileEntity(pos);
//                if (te != null && te instanceof TileEntityArmourer) {
//                    ((TileEntityArmourer)te).toolUsedOnArmourer(this, worldIn, stack, player);
//                }
//            }
//            return EnumActionResult.SUCCESS;
//        }
//        return EnumActionResult.PASS;
//    }
//
//    @Override
//    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing face, boolean spawnParticles) {
//        int intensity = ToolOptions.INTENSITY.getValue(stack);
//        int radiusSample = ToolOptions.RADIUS_SAMPLE.getValue(stack);
//        int radiusEffect = ToolOptions.RADIUS_EFFECT.getValue(stack);
//        boolean restrictPlane = ToolOptions.PLANE_RESTRICT.getValue(stack);
//
//        ArrayList<BlockPos> blockSamples = BlockUtils.findTouchingBlockFaces(world, pos, face, radiusSample, restrictPlane);
//        ArrayList<BlockPos> blockEffects = BlockUtils.findTouchingBlockFaces(world, pos, face, radiusEffect, restrictPlane);
//
//        if (blockSamples.size() == 0 | blockEffects.size() == 0) {
//            return;
//        }
//
//        int r = 0;
//        int g = 0;
//        int b = 0;
//
//        int validSamples = 0;
//
//        for (BlockPos posSample : blockSamples) {
//            IBlockState stateTarget = world.getBlockState(posSample);
//            if (stateTarget.getBlock() instanceof IPantableBlock) {
//                IPantableBlock pBlock = (IPantableBlock) stateTarget.getBlock();
//                ICubeColour c = pBlock.getColour(world, posSample);
//                if (c != null) {
//                    r += c.getRed(face.ordinal()) & 0xFF;
//                    g += c.getGreen(face.ordinal()) & 0xFF;
//                    b += c.getBlue(face.ordinal()) & 0xFF;
//                    validSamples++;
//                }
//            }
//        }
//
//        if (validSamples == 0) {
//            return;
//        }
//
//        r = r / validSamples;
//        g = g / validSamples;
//        b = b / validSamples;
//
//        for (BlockPos posEffect : blockEffects) {
//            IBlockState stateTarget = world.getBlockState(posEffect);
//            if (stateTarget.getBlock() instanceof IPantableBlock) {
//                IPantableBlock pBlock = (IPantableBlock) stateTarget.getBlock();
//                int oldColour = pBlock.getColour(world, posEffect, face);
//                byte oldPaintType = (byte) pBlock.getPaintType(world, posEffect, face).getId();
//
//                Color oldC = new Color(oldColour);
//                int oldR = oldC.getRed();
//                int oldG = oldC.getGreen();
//                int oldB = oldC.getBlue();
//
//                float newR = r / 100F * intensity;
//                newR += oldR / 100F * (100 - intensity);
//                newR = MathHelper.clamp((int) newR, 0, 255);
//
//                float newG = g / 100F * intensity;
//                newG += oldG / 100F * (100 - intensity);
//                newG = MathHelper.clamp((int) newG, 0, 255);
//
//                float newB = b / 100F * intensity;
//                newB += oldB / 100F * (100 - intensity);
//                newB = MathHelper.clamp((int) newB, 0, 255);
//
//                Color newC = new Color(
//                        (int)newR,
//                        (int)newG,
//                        (int)newB);
//
//                UndoManager.blockPainted(player, world, posEffect, oldColour, oldPaintType, face);
//                ((IPantableBlock)block).setColour(world, posEffect, newC.getRGB(), face);
//            }
//        }
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
//        super.addInformation(stack, worldIn, tooltip, flagIn);
//    }
//
//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
//        if (worldIn.isRemote & playerIn.isSneaking()) {
//            playerIn.openGui(ArmourersWorkshop.getInstance(), EnumGuiId.TOOL_OPTIONS.ordinal(), worldIn, 0, 0, 0);
//        }
//        return super.onItemRightClick(worldIn, playerIn, handIn);
//    }
//
//    @Override
//    public void getToolOptions(ArrayList<ToolOption<?>> toolOptionList) {
//    }
//}
