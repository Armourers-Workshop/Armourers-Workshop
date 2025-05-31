package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockItem;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.paint.IItemPaintable;
import moe.plushie.armourers_workshop.core.data.paint.IPaintProvider;
import moe.plushie.armourers_workshop.core.data.paint.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkinCubeItem extends AbstractBlockItem implements IItemPaintable, IPaintToolPicker {

    public SkinCubeItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult usePickTool(Level level, BlockPos pos, OpenDirection dir, BlockEntity blockEntity, UseOnContext context) {
        var itemStack = context.getItemInHand();
        if (blockEntity instanceof IPaintProvider provider) {
            setItemColor(itemStack, provider.getColor());
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
        // sync the all faced color into block.
        var blockEntity = level.getBlockEntity(pos);
        var entityTag = itemStack.get(ModDataComponents.BLOCK_ENTITY_DATA.get());
        if (entityTag != null && blockEntity != null) {
            CompoundTag newNBT = blockEntity.saveFullData(level.registryAccess());
            newNBT.put(Constants.Key.COLOR, entityTag.getCompound(Constants.Key.COLOR));
            blockEntity.loadFullData(newNBT, level.registryAccess());
        }
        return super.updateCustomBlockEntityTag(pos, level, player, itemStack, blockState);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.appendHoverText(itemStack, tooltips, context);
        var paintColor = getItemColors(itemStack);
        if (paintColor != null && paintColor.isPureColor()) {
            tooltips.addAll(ColorUtils.getColorTooltips(paintColor.get(OpenDirection.NORTH), true));
        }
    }

    @Override
    public void setItemColor(ItemStack itemStack, SkinPaintColor paintColor) {
        var entityTag = new CompoundTag();
        var oldEntityTag = itemStack.get(ModDataComponents.BLOCK_ENTITY_DATA.get());
        if (oldEntityTag != null) {
            entityTag.merge(oldEntityTag);
        }
        entityTag.putString(Constants.Key.ID, TypedRegistry.findKey(getBlock()).toString());
        var color = new BlockPaintColor(paintColor);
        var serializer = new TagSerializer();
        color.serialize(serializer);
        entityTag.put(Constants.Key.COLOR, serializer.getTag());
        itemStack.set(ModDataComponents.TOOL_FLAGS.get(), 1);
        itemStack.set(ModDataComponents.BLOCK_ENTITY_DATA.get(), entityTag);
    }

    @Override
    public SkinPaintColor getItemColor(ItemStack itemStack) {
        return itemStack.getOrDefault(ModDataComponents.TOOL_COLOR.get(), SkinPaintColor.WHITE);
    }

    @Nullable
    public BlockPaintColor getItemColors(ItemStack itemStack) {
        return ColorUtils.getBlockColor(itemStack);
    }
}
