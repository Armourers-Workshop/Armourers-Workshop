package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemModelPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.core.item.impl.IPaintPicker;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import moe.plushie.armourers_workshop.init.common.AWCore;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorPickerItem extends AbstractPaintingToolItem implements IItemTintColorProvider, IItemModelPropertiesProvider, IPaintPicker, IBlockPaintViewer {

    public ColorPickerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getLevel();
        if (pickColor(context)) {
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        if (applyColor(context)) {
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean pickColor(ItemUseContext context) {
        return pickColor(context.getLevel(), context.getClickedPos(), context.getItemInHand(), context.getPlayer());
    }

    @Override
    public boolean pickColor(World worldIn, BlockPos blockPos, ItemStack itemStack, @Nullable PlayerEntity player) {
        TileEntity tileEntity = worldIn.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintable) {
            IPaintColor color = ((IPaintable) tileEntity).getColor(Direction.NORTH);
            ColorUtils.setColor(itemStack, color);
            return true;
        }
        // if trigger not by the player, we check again to try could be applied
        if (player == null) {
            return applyColor(worldIn, blockPos, Direction.NORTH, itemStack, null);
        }
        return false;
    }

    @Override
    public boolean applyColor(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.isShiftKeyDown()) {
            return false;
        }
        return super.applyColor(context);
    }

    @Override
    public boolean applyColor(World worldIn, BlockPos blockPos, Direction direction, ItemStack itemStack, @Nullable PlayerEntity player) {
        IPaintColor color = ColorUtils.getColor(itemStack);
        if (color == null) {
            return false;
        }
        TileEntity tileEntity = worldIn.getBlockEntity(blockPos);
        if (tileEntity instanceof IPaintProvider) {
            IPaintProvider provider = (IPaintProvider)tileEntity;
            if (!ToolOptions.CHANGE_PAINT_TYPE.get(itemStack)) {
                color = PaintColor.of(color.getRGB(), provider.getColor().getPaintType());
            }
            provider.setColor(color);
            return true;
        }
        return false;
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(AWCore.resource("empty"), (itemStack, world, entity) -> ColorUtils.hasColor(itemStack) ? 0 : 1);
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.CHANGE_PAINT_TYPE);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<ITextComponent> tooltips, ITooltipFlag flags) {
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            tooltips.addAll(ColorUtils.getColorTooltips(paintColor, false));
        } else {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.empty"));
        }
    }

    @Override
    public int getTintColor(ItemStack itemStack, int index) {
        if (index == 1) {
            return ColorUtils.getDisplayRGB(itemStack);
        }
        return 0xffffffff;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            return paintColor.getPaintType() != SkinPaintTypes.NORMAL;
        }
        return false;
    }

}
//public class ItemColourPicker extends AbstractModItem implements IPaintingTool, IConfigurableTool {
//
//    public ItemColourPicker() {
//        super(LibItemNames.COLOUR_PICKER);
//        setCreativeTab(ArmourersWorkshop.TAB_PAINTING_TOOLS);
//        setSortPriority(12);
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public boolean hasEffect(ItemStack stack) {
//        IPaintType paintType = PaintingHelper.getToolPaintType(stack);
//        if (paintType != PaintTypeRegistry.PAINT_TYPE_NORMAL) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        IBlockState state = worldIn.getBlockState(pos);
//        ItemStack stack = player.getHeldItem(hand);
//        boolean changePaintType = ToolOptions.CHANGE_PAINT_TYPE.getValue(stack);
//        IPaintType paintType = getToolPaintType(stack);
//
//        if (player.isSneaking() & state.getBlock() == ModBlocks.COLOUR_MIXER & getToolHasColour(stack)) {
//            TileEntity te = worldIn.getTileEntity(pos);
//            if (te != null && te instanceof IPantable) {
//                if (!worldIn.isRemote) {
//                    int colour = getToolColour(stack);;
//                    ((IPantable)te).setColour(colour);
//                    ((IPantable)te).setPaintType(paintType, 0);
//                }
//            }
//            return EnumActionResult.SUCCESS;
//        }
//
//        if (state.getBlock() instanceof IPantableBlock) {
//            IPantableBlock paintable = (IPantableBlock) state.getBlock();
//            IPaintType targetPaintType = paintable.getPaintType(worldIn, pos, facing);
//
//            if (paintable.isRemoteOnly(worldIn, pos, facing) & worldIn.isRemote) {
//                int colour = paintable.getColour(worldIn, pos, facing);
//                NBTTagCompound compound = new NBTTagCompound();
//                byte[] paintData = new byte[4];
//                Color c = new Color(colour);
//                paintData[0] = (byte) c.getRed();
//                paintData[1] = (byte) c.getGreen();
//                paintData[2] = (byte) c.getBlue();
//                if (changePaintType) {
//                    paintData[3] = (byte) targetPaintType.getId();
//                } else {
//                    paintData[3] = (byte) paintType.getId();
//                }
//
//                PaintingHelper.setPaintData(compound, paintData);
//                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate(compound));
//            } else if (!paintable.isRemoteOnly(worldIn, pos, facing) & !worldIn.isRemote) {
//                setToolColour(stack, ((IPantableBlock)state.getBlock()).getColour(worldIn, pos, facing));
//                if (changePaintType) {
//                    setToolPaintType(stack, targetPaintType);
//                } else {
//                    setToolPaintType(stack, paintType);
//                }
//            }
//
//            if (!worldIn.isRemote) {
//                worldIn.playSound(null, pos, ModSounds.PICKER, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
//            }
//            return EnumActionResult.SUCCESS;
//        }
//        return EnumActionResult.PASS;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
//        super.addInformation(stack, worldIn, tooltip, flagIn);
//        if (getToolHasColour(stack)) {
//            Color c = new Color(getToolColour(stack));
//            IPaintType paintType = getToolPaintType(stack);
//            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
//            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.colour", c.getRGB()));
//            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.hex", hex));
//            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.paintType", paintType.getLocalizedName()));
//        } else {
//            tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.nopaint"));
//        }
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.openSettings"));
//    }
//
//    @Override
//    public boolean getToolHasColour(ItemStack stack) {
//        return PaintingHelper.getToolHasPaint(stack);
//    }
//
//    @Override
//    public int getToolColour(ItemStack stack) {
//        return PaintingHelper.getToolPaintColourRGB(stack);
//    }
//
//    @Override
//    public void setToolColour(ItemStack stack, int colour) {
//        PaintingHelper.setToolPaintColour(stack, colour);
//    }
//
//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
//        if (playerIn.isSneaking()) {
//            if (worldIn.isRemote) {
//                playerIn.openGui(ArmourersWorkshop.getInstance(), EnumGuiId.TOOL_OPTIONS.ordinal(), worldIn, 0, 0, 0);
//            }
//            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
//        }
//        return super.onItemRightClick(worldIn, playerIn, handIn);
//    }
//
//    @Override
//    public void getToolOptions(ArrayList<ToolOption<?>> toolOptionList) {
//        toolOptionList.add();
//    }
//
//    @Override
//    public void setToolPaintType(ItemStack stack, IPaintType paintType) {
//        PaintingHelper.setToolPaint(stack, paintType);
//    }
//
//    @Override
//    public IPaintType getToolPaintType(ItemStack stack) {
//        return PaintingHelper.getToolPaintType(stack) ;
//    }
//
//}
