package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.item.impl.IPaintPicker;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.color.BlockPaintColor;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class SkinCubeItem extends BlockItem implements IItemColorProvider, IPaintPicker {

    public SkinCubeItem(Block p_i48527_1_, Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    @Override
    public boolean shouldPickColor(ItemUseContext context) {
        return true;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack itemStack, BlockState blockState) {
        if (world.isClientSide()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            CompoundNBT nbt = itemStack.getTagElement(AWConstants.NBT.BLOCK_ENTITY);
            if (nbt != null && tileEntity != null) {
                CompoundNBT newNBT = tileEntity.save(new CompoundNBT());
                newNBT.put(AWConstants.NBT.COLOR, nbt.getCompound(AWConstants.NBT.COLOR));
                tileEntity.load(blockState, newNBT);
            }
        }
        return super.updateCustomBlockEntityTag(pos, world, player, itemStack, blockState);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, tooltips, flags);
        BlockPaintColor paintColor = getItemColors(itemStack);
        if (paintColor != null && paintColor.isPureColor()) {
            tooltips.addAll(ColorUtils.getColorTooltips(paintColor.get(Direction.NORTH), true));
        }
    }

    @Override
    public void setPickedColor(ItemStack itemStack, IPaintColor paintColor, ItemUseContext context) {
        CompoundNBT nbt = itemStack.getOrCreateTagElement(AWConstants.NBT.BLOCK_ENTITY);
        BlockPaintColor color = new BlockPaintColor(paintColor);
        AWDataSerializers.putBlockPaintColor(nbt, AWConstants.NBT.COLOR, color, null);
        itemStack.addTagElement(AWConstants.NBT.FLAGS, IntNBT.valueOf(1));
    }

    @Override
    public IPaintColor getItemColor(ItemStack itemStack) {
        return ObjectUtils.defaultIfNull(ColorUtils.getColor(itemStack), PaintColor.WHITE);
    }

    @Nullable
    public BlockPaintColor getItemColors(ItemStack itemStack) {
        return ColorUtils.getBlockColor(itemStack);
    }
}
