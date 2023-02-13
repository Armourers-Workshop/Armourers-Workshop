package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.item.impl.IPaintToolPicker;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkinCubeItem extends BlockItem implements IItemColorProvider, IPaintToolPicker {

    public SkinCubeItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult usePickTool(Level level, BlockPos pos, Direction dir, BlockEntity tileEntity, UseOnContext context) {
        ItemStack itemStack = context.getItemInHand();
        if (tileEntity instanceof IPaintProvider) {
            setItemColor(itemStack, ((IPaintProvider) tileEntity).getColor());
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
        // sync the all faced color into block.
        BlockEntity tileEntity = level.getBlockEntity(pos);
        CompoundTag nbt = itemStack.getTagElement(Constants.Key.BLOCK_ENTITY);
        if (nbt != null && tileEntity != null) {
            CompoundTag newNBT = tileEntity.saveWithFullMetadata();
            newNBT.put(Constants.Key.COLOR, nbt.getCompound(Constants.Key.COLOR));
            tileEntity.load(newNBT);
        }
        return super.updateCustomBlockEntityTag(pos, level, player, itemStack, blockState);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltips, TooltipFlag flags) {
        super.appendHoverText(itemStack, level, tooltips, flags);
        BlockPaintColor paintColor = getItemColors(itemStack);
        if (paintColor != null && paintColor.isPureColor()) {
            tooltips.addAll(ColorUtils.getColorTooltips(paintColor.get(Direction.NORTH), true));
        }
    }

    @Override
    public void setItemColor(ItemStack itemStack, IPaintColor paintColor) {
        CompoundTag nbt = itemStack.getOrCreateTagElement(Constants.Key.BLOCK_ENTITY);
        BlockPaintColor color = new BlockPaintColor(paintColor);
        DataSerializers.putBlockPaintColor(nbt, Constants.Key.COLOR, color, null);
        itemStack.addTagElement(Constants.Key.FLAGS, IntTag.valueOf(1));
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
