package moe.plushie.armourers_workshop.builder.item;

import com.google.common.collect.Maps;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.core.item.impl.IPaintPicker;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TileEntityUpdateCombiner;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SkinCubeItem extends BlockItem implements IPaintPicker {

    public SkinCubeItem(Block p_i48527_1_, Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    @Override
    public boolean shouldPickColor(ItemUseContext context) {
        return true;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack itemStack, BlockState blockState) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null && tileEntity instanceof IPaintable) {
            ((IPaintable) tileEntity).setColors(Maps.toMap(Arrays.asList(Direction.values()), dir -> paintColor));
        }
        return super.updateCustomBlockEntityTag(pos, world, player, itemStack, blockState);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, tooltips, flags);
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            tooltips.addAll(ColorUtils.getColorTooltips(paintColor, true));
        }
    }
}
