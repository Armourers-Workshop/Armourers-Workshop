package moe.plushie.armourers_workshop.common.init.items;

import java.awt.Color;
import java.util.List;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.painting.IPaintingTool;
import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDyeBottle extends AbstractModItem implements IPaintingTool {

    public ItemDyeBottle() {
        super(LibItemNames.DYE_BOTTLE);
        setSortPriority(11);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        if (player.isSneaking() & state.getBlock() == ModBlocks.COLOUR_MIXER) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te instanceof IPantable) {
                if (!worldIn.isRemote) {
                    ItemStack stack = player.getHeldItem(hand);
                    int colour = ((IPantable)te).getColour(0);
                    IPaintType paintType = ((IPantable)te).getPaintType(0);
                    setToolColour(stack, colour);
                    setToolPaintType(stack, paintType);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
    
    @Override
    public boolean hasEffect(ItemStack stack) {
        IPaintType paintType = PaintingHelper.getToolPaintType(stack);
        if (paintType != PaintTypeRegistry.PAINT_TYPE_NORMAL) {
            return true;
        }
        return super.hasEffect(stack);
    }
    
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (getToolHasColour(stack)) {
            Color c = new Color(getToolColour(stack));
            IPaintType paintType = getToolPaintType(stack);
            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            String colourText = TranslateUtils.translate("item.armourers_workshop:rollover.colour", c.getRGB());
            String hexText = TranslateUtils.translate("item.armourers_workshop:rollover.hex", hex);
            String paintText = TranslateUtils.translate("item.armourers_workshop:rollover.paintType", paintType.getLocalizedName());
            
            tooltip.add(colourText);
            tooltip.add(hexText);
            tooltip.add(paintText);
        } else {
            String emptyText = TranslateUtils.translate("item.armourers_workshop:rollover.empty");
            tooltip.add(emptyText);
        }
    }
    
    @Override
    public boolean getToolHasColour(ItemStack stack) {
        return PaintingHelper.getToolHasPaint(stack);
    }

    @Override
    public int getToolColour(ItemStack stack) {
        return PaintingHelper.getToolPaintColourRGB(stack);
    }

    @Override
    public void setToolColour(ItemStack stack, int colour) {
        PaintingHelper.setToolPaintColour(stack, colour);
    }
    
    @Override
    public void setToolPaintType(ItemStack stack, IPaintType paintType) {
        PaintingHelper.setToolPaint(stack, paintType);
    }
    
    @Override
    public IPaintType getToolPaintType(ItemStack stack) {
        return PaintingHelper.getToolPaintType(stack) ;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                if (getToolHasColour(stack)) {
                    return new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "inventory");
                } else {
                    return new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "-empty"), "inventory");
                }
            }
        });
        ModelBakery.registerItemVariants(this,
                new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "inventory"),
                new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "-empty"), "inventory"));
    }
}
