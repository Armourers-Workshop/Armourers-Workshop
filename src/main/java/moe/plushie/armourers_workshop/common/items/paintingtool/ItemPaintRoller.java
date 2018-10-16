package moe.plushie.armourers_workshop.common.items.paintingtool;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.lib.LibSounds;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOption;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOptions;
import moe.plushie.armourers_workshop.common.undo.UndoManager;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPaintRoller extends AbstractPaintingTool implements IConfigurableTool {
    
    public ItemPaintRoller() {
        super(LibItemNames.PAINT_ROLLER);
        setSortPriority(19);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        ItemStack stack = player.getHeldItem(hand);
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
    @Override
    public void onPaint(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing side) {
        paintArea(world, player, block, stack, pos, side);
    }
    
    private void paintArea(World world, EntityPlayer player, Block targetBlock, ItemStack stack, BlockPos pos, EnumFacing facing) {
        
        int radius = ToolOptions.RADIUS.getValue(stack);
        for (int i = -radius + 1; i < radius; i++ ) {
            for (int j = -radius + 1; j < radius; j++ ) {
                BlockPos target = pos;
                switch (facing) {
                case DOWN:
                    target = pos.add(j, 0, i);
                    break;
                case UP:
                    target = pos.add(j, 0, i);
                    break;
                case NORTH:
                    target = pos.add(i, j, 0);
                    break;
                case SOUTH:
                    target = pos.add(i, j, 0);
                    break;
                case WEST:
                    target = pos.add(0, i, j);
                    break;
                case EAST:
                    target = pos.add(0, i, j);
                    break;
                }
                Block block = world.getBlockState(target).getBlock();
                if ((targetBlock != ModBlocks.boundingBox & block != ModBlocks.boundingBox) |
                        (targetBlock == ModBlocks.boundingBox & block == ModBlocks.boundingBox)) {
                    usedOnBlockSide(stack, player, world, target, block, facing);
                }
            }
        }
    }
    
    @Override
    public void playToolSound(World world, BlockPos pos, ItemStack stack) {
        world.playSound(null, pos, new SoundEvent(new ResourceLocation(LibSounds.PAINT)), SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing facing) {
        if (block instanceof IPantableBlock) {
            int newColour = getToolColour(stack);
            PaintType paintType = getToolPaintType(stack);
            if (!world.isRemote) {
                IPantableBlock worldColourable = (IPantableBlock) block;
                if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
                    for (int i = 0; i < 6; i++) {
                        EnumFacing face = EnumFacing.VALUES[i];
                        int oldColour = worldColourable.getColour(world, pos, face);
                        byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, face).getKey();
                        UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, face);
                        ((IPantableBlock)block).setColour(world, pos, newColour, face);
                        ((IPantableBlock)block).setPaintType(world, pos, paintType, face);
                    }
                } else {
                    int oldColour = worldColourable.getColour(world, pos, facing);
                    byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, facing).getKey();
                    UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, facing);
                    ((IPantableBlock)block).setColour(world, pos, newColour, facing);
                    ((IPantableBlock)block).setPaintType(world, pos, paintType, facing);
                }
            } else {
                spawnPaintParticles(world, pos, facing, newColour);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        int radius = ToolOptions.RADIUS.getValue(stack);
        String radiusText = TranslateUtils.translate("item.armourersworkshop:rollover.radius", radius * 2 - 1, radius * 2 - 1, 1);
        tooltip.add(radiusText);
        addOpenSettingsInformation(stack, worldIn, tooltip, flagIn);
    }
    
    @Override
    public void getToolOptions(ArrayList<ToolOption<?>> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
        toolOptionList.add(ToolOptions.RADIUS);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
                    return new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "inventory");
                } else {
                    return new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "-small"), "inventory");
                }
            }
        });
        ModelBakery.registerItemVariants(this, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "inventory"), new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "-small"), "inventory"));
    }
}
