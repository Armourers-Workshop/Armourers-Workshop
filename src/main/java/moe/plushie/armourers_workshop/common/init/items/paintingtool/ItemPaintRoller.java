package moe.plushie.armourers_workshop.common.init.items.paintingtool;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.init.sounds.ModSounds;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOption;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOptions;
import moe.plushie.armourers_workshop.common.world.undo.UndoManager;
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
    public void onPaint(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing usedFace) {
        paintArea(world, player, block, stack, pos, usedFace);
    }
    
    private void paintArea(World world, EntityPlayer player, Block targetBlock, ItemStack stack, BlockPos pos, EnumFacing face) {
        int radius = ToolOptions.RADIUS.getValue(stack);
        for (int i = -radius + 1; i < radius; i++ ) {
            for (int j = -radius + 1; j < radius; j++ ) {
                BlockPos target = pos;
                switch (face) {
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
                if ((targetBlock != ModBlocks.BOUNDING_BOX & block != ModBlocks.BOUNDING_BOX) |
                        (targetBlock == ModBlocks.BOUNDING_BOX & block == ModBlocks.BOUNDING_BOX)) {
                    usedOnBlockSide(stack, player, world, target, block, face, true);
                }
            }
        }
    }
    
    @Override
    public void playToolSound(EntityPlayer player, World world, BlockPos pos, ItemStack stack) {
        world.playSound(null, pos, ModSounds.PAINT, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
    }
    
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing facing, boolean spawnParticles) {
        boolean fullBlock = false;
        if (this instanceof IConfigurableTool) {
            ArrayList<ToolOption<?>> toolOptionList = new ArrayList<ToolOption<?>>();
            ((IConfigurableTool)this).getToolOptions(toolOptionList);
            if (toolOptionList.contains(ToolOptions.FULL_BLOCK_MODE)) {
                fullBlock = ToolOptions.FULL_BLOCK_MODE.getValue(stack);
            }
        }
        
        if (block instanceof IPantableBlock) {
            int newColour = getToolColour(stack);
            IPaintType paintType = getToolPaintType(stack);
            if (!world.isRemote) {
                IPantableBlock worldColourable = (IPantableBlock) block;
                if (fullBlock) {
                    for (int i = 0; i < 6; i++) {
                        int oldColour = worldColourable.getColour(world, pos, EnumFacing.VALUES[i]);
                        byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, EnumFacing.VALUES[i]).getId();
                        UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, EnumFacing.VALUES[i]);
                        ((IPantableBlock)block).setColour(world, pos, newColour, EnumFacing.VALUES[i]);
                        ((IPantableBlock)block).setPaintType(world, pos, paintType, EnumFacing.VALUES[i]);
                    }
                } else {
                    int oldColour = worldColourable.getColour(world, pos, facing);
                    byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, facing).getId();
                    UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, facing);
                    ((IPantableBlock)block).setColour(world, pos, newColour, facing);
                    ((IPantableBlock)block).setPaintType(world, pos, paintType, facing);
                }
            } else {
                if (spawnParticles) {
                    spawnPaintParticles(world, pos, facing, newColour);
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        int radius = ToolOptions.RADIUS.getValue(stack);
        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.area", radius * 2 - 1, radius * 2 - 1, 1));
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
                    return new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "normal");
                } else {
                    return new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "-small"), "normal");
                }
            }
        });
        ModelBakery.registerItemVariants(this, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "normal"), new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "-small"), "normal"));
    }
}
