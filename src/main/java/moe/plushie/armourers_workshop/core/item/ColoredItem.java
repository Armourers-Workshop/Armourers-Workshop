package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintColorProvider;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.color.PaintColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.awt.*;

@SuppressWarnings("NullableProblems")
public abstract class ColoredItem extends FlavouredItem {

    public ColoredItem(Item.Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public static float isEmpty(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return hasColor(itemStack) ? 0 : 1;
    }

    public static boolean hasColor(ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        return tag != null && tag.contains(AWConstants.NBT.COLOR, Constants.NBT.TAG_INT);
    }

    public static void setColor(ItemStack itemStack, PaintColor color) {
        AWDataSerializers.putPaintColor(itemStack.getOrCreateTag(), AWConstants.NBT.COLOR, color, null);
    }

    @Nullable
    public static PaintColor getColor(ItemStack itemStack) {
        if (!hasColor(itemStack)) {
            return null;
        }
        return PaintColor.of(getColorValue(itemStack));
    }

    public static int getColorValue(ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        if (tag != null && tag.contains(AWConstants.NBT.COLOR)) {
            INBT nbt = tag.get(AWConstants.NBT.COLOR);
            if (nbt instanceof NumberNBT) {
                return ((NumberNBT) nbt).getAsInt();
            }
            if (nbt instanceof StringNBT) {
                Color color = ColorUtils.parseColor(nbt.getAsString());
                tag.putInt(AWConstants.NBT.COLOR, color.getRGB());
                return color.getRGB();
            }
            tag.remove(AWConstants.NBT.COLOR);
        }
        return 0;
    }

    public static ISkinPaintType getPaintType(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return SkinPaintTypes.NONE;
        }
        int color = getColorValue(itemStack);
        return PaintColor.getPaintType(color);
    }


    public static IItemColor getColorProvider(int tintIndex) {
        return (itemStack, tintIndex1) -> {
            if (tintIndex == tintIndex1) {
                PaintColor paintColor = getColor(itemStack);
                if (paintColor != null) {
                    return ColorUtils.getDisplayRGB(paintColor) | 0xff000000;
                }
            }
            return 0xffffffff;
        };
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        if (pickColor(player, world, pos, itemStack)) {
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return ActionResultType.PASS;
    }

    public boolean pickColor(@Nullable PlayerEntity player, World worldIn, BlockPos pos, ItemStack itemStack) {
        if (player != null && player.isShiftKeyDown()) {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof IPaintColorProvider) {
                IPaintColor color = ((IPaintColorProvider) tileEntity).getColor();
                ColoredItem.setColor(itemStack, PaintColor.of(color));
                return true;
            }
        }
        return false;
    }
}
