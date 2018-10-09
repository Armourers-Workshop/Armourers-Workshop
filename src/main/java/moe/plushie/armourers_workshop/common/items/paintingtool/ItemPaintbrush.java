package moe.plushie.armourers_workshop.common.items.paintingtool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.lib.LibGuiIds;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.lib.LibSounds;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOption;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOptions;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import moe.plushie.armourers_workshop.common.undo.UndoManager;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
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

public class ItemPaintbrush extends AbstractPaintingTool implements IConfigurableTool {
    
    public ItemPaintbrush() {
        super(LibItemNames.PAINTBRUSH);
        setSortPriority(20);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        ItemStack stack = player.getHeldItem(hand);
        
        if (player.isSneaking() & state.getBlock() == ModBlocks.colourMixer) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te instanceof IPantable) {
                if (!worldIn.isRemote) {
                    int colour = ((IPantable)te).getColour(0);
                    PaintType paintType = ((IPantable)te).getPaintType(0);
                    setToolColour(stack, colour);
                    setToolPaintType(stack, paintType);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        
        if (state.getBlock() instanceof IPantableBlock) {
            int newColour = getToolColour(stack);
            if (!worldIn.isRemote) {
                UndoManager.begin(player);
                if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
                    for (int i = 0; i < 6; i++) {
                        usedOnBlockSide(stack, player, worldIn, pos, state.getBlock(), EnumFacing.VALUES[i]);
                    }
                } else {
                    usedOnBlockSide(stack, player, worldIn, pos, state.getBlock(), facing);
                }
                UndoManager.end(player);
                if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
                    worldIn.playSound(null, pos, new SoundEvent(new ResourceLocation(LibSounds.PAINT)), SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
                } else {
                    worldIn.playSound(null, pos, new SoundEvent(new ResourceLocation(LibSounds.PAINT)), SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 1.5F);
                }
            } else {
                spawnPaintParticles(worldIn, pos, facing, newColour);
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
            ModLogger.log("armourer");
            return EnumActionResult.SUCCESS;
        }
        
        return EnumActionResult.PASS;
    }
    
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing side) {
        int colour = getToolColour(stack);
        PaintType paintType = getToolPaintType(stack);
        IPantableBlock worldColourable = (IPantableBlock) block;
        int oldColour = worldColourable.getColour(world, pos, side);
        byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, side).getKey();
        UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, side);
        ((IPantableBlock)block).setColour(world, pos, colour, side);
        ((IPantableBlock)block).setPaintType(world, pos, paintType, side);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (playerIn.isSneaking()) {
            if (worldIn.isRemote) {
                playerIn.openGui(ArmourersWorkshop.getInstance(), LibGuiIds.TOOL_OPTIONS, worldIn, 0, 0, 0);
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
    
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        Color c = new Color(getToolColour(stack));
        PaintType paintType = getToolPaintType(stack);
        String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        String colourText = TranslateUtils.translate("item.armourers_workshop:rollover.colour", c.getRGB());
        String hexText = TranslateUtils.translate("item.armourers_workshop:rollover.hex", hex);
        String paintText = TranslateUtils.translate("item.armourers_workshop:rollover.paintType", paintType.getLocalizedName());
        
        tooltip.add(colourText);
        tooltip.add(hexText);
        tooltip.add(paintText);
        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.openSettings"));
    }
    
    @Override
    public void getToolOptions(ArrayList<ToolOption<?>> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
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
