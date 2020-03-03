package moe.plushie.armourers_workshop.common.init.items.paintingtool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.painting.IPaintingTool;
import moe.plushie.armourers_workshop.api.common.painting.IPantable;
import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.init.items.AbstractModItem;
import moe.plushie.armourers_workshop.common.init.sounds.ModSounds;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiToolOptionUpdate;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOption;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOptions;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
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

public class ItemColourPicker extends AbstractModItem implements IPaintingTool, IConfigurableTool {
    
    public ItemColourPicker() {
        super(LibItemNames.COLOUR_PICKER);
        setCreativeTab(ArmourersWorkshop.TAB_PAINTING_TOOLS);
        setSortPriority(12);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        IPaintType paintType = PaintingHelper.getToolPaintType(stack);
        if (paintType != PaintTypeRegistry.PAINT_TYPE_NORMAL) {
            return true;
        }
        return false;
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        ItemStack stack = player.getHeldItem(hand);
        boolean changePaintType = ToolOptions.CHANGE_PAINT_TYPE.getValue(stack);
        IPaintType paintType = getToolPaintType(stack);
        
        if (player.isSneaking() & state.getBlock() == ModBlocks.COLOUR_MIXER & getToolHasColour(stack)) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te instanceof IPantable) {
                if (!worldIn.isRemote) {
                    int colour = getToolColour(stack);;
                    ((IPantable)te).setColour(colour);
                    ((IPantable)te).setPaintType(paintType, 0);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        
        if (state.getBlock() instanceof IPantableBlock) {
            IPantableBlock paintable = (IPantableBlock) state.getBlock();
            IPaintType targetPaintType = paintable.getPaintType(worldIn, pos, facing);
            
            if (paintable.isRemoteOnly(worldIn, pos, facing) & worldIn.isRemote) {
                int colour = paintable.getColour(worldIn, pos, facing);
                NBTTagCompound compound = new NBTTagCompound();
                byte[] paintData = new byte[4];
                Color c = new Color(colour);
                paintData[0] = (byte) c.getRed();
                paintData[1] = (byte) c.getGreen();
                paintData[2] = (byte) c.getBlue();
                if (changePaintType) {
                    paintData[3] = (byte) targetPaintType.getId();
                } else {
                    paintData[3] = (byte) paintType.getId();
                }
                
                PaintingHelper.setPaintData(compound, paintData);
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate(compound));
            } else if (!paintable.isRemoteOnly(worldIn, pos, facing) & !worldIn.isRemote) {
                setToolColour(stack, ((IPantableBlock)state.getBlock()).getColour(worldIn, pos, facing));
                if (changePaintType) {
                    setToolPaintType(stack, targetPaintType);
                } else {
                    setToolPaintType(stack, paintType);
                }
            }
            
            if (!worldIn.isRemote) {
                worldIn.playSound(null, pos, ModSounds.PICKER, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (getToolHasColour(stack)) {
            Color c = new Color(getToolColour(stack));
            IPaintType paintType = getToolPaintType(stack);
            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.colour", c.getRGB()));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.hex", hex));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.paintType", paintType.getLocalizedName()));
        } else {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.nopaint"));
        }
        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.openSettings"));
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (playerIn.isSneaking()) {
            if (worldIn.isRemote) {
                playerIn.openGui(ArmourersWorkshop.getInstance(), EnumGuiId.TOOL_OPTIONS.ordinal(), worldIn, 0, 0, 0);
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
    
    @Override
    public void getToolOptions(ArrayList<ToolOption<?>> toolOptionList) {
        toolOptionList.add(ToolOptions.CHANGE_PAINT_TYPE);
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
