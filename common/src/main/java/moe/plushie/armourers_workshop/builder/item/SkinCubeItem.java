package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.data.TypedEntityData;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.paint.IItemPaintable;
import moe.plushie.armourers_workshop.core.data.paint.IPaintProvider;
import moe.plushie.armourers_workshop.core.data.paint.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.item.FlavouredBlockItem;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkinCubeItem extends FlavouredBlockItem implements IItemPaintable, IPaintToolPicker {

    public SkinCubeItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public OpenInteractionResult usePickTool(Level level, BlockPos pos, OpenDirection dir, BlockEntity blockEntity, UseOnContext context) {
        var itemStack = context.getItemInHand();
        if (blockEntity instanceof IPaintProvider provider) {
            setItemColor(itemStack, provider.color());
            return OpenInteractionResult.sidedSuccess(level.isClientSide());
        }
        return OpenInteractionResult.PASS;
    }

    @Override
    public void setItemColor(ItemStack itemStack, SkinPaintColor paintColor) {
        var entityType = Registries.BLOCK_ENTITY_TYPES.getValue(Registries.BLOCKS.getKey(getBlock()));
        if (entityType == null) {
            return; // can't found the block entity.
        }
        var entityTag = new CompoundTag();
        var oldEntityData = itemStack.get(ModDataComponents.BLOCK_ENTITY_DATA.get());
        if (oldEntityData != null) {
            entityTag.merge(oldEntityData.tag());
        }
        var color = new BlockPaintColor(paintColor);
        var serializer = new TagSerializer();
        color.serialize(serializer);
        entityTag.put(Constants.Key.COLOR, serializer.tag());
        itemStack.set(ModDataComponents.TOOL_FLAGS.get(), 1);
        itemStack.set(ModDataComponents.BLOCK_ENTITY_DATA.get(), TypedEntityData.of(entityType, entityTag));
    }

    @Override
    public SkinPaintColor getItemColor(ItemStack itemStack) {
        return itemStack.getOrDefault(ModDataComponents.TOOL_COLOR.get(), SkinPaintColor.WHITE);
    }

    @Nullable
    public BlockPaintColor getItemColors(ItemStack itemStack) {
        return Colors.getBlockColor(itemStack);
    }

    @Override
    protected boolean abi$updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
        // sync the all faced color into block.
        var blockEntity = level.getBlockEntity(pos);
        var entityData = itemStack.get(ModDataComponents.BLOCK_ENTITY_DATA.get());
        if (entityData != null && blockEntity != null) {
            var serializer = new TagSerializer(SerializationContext.from(level));
            blockEntity.saveFullData(serializer);
            serializer.write(CodingKeys.COLOR, new TagSerializer(entityData.tag(), SerializationContext.from(blockEntity)).read(CodingKeys.COLOR));
            blockEntity.loadFullData(serializer);
        }
        return super.abi$updateCustomBlockEntityTag(pos, level, player, itemStack, blockState);
    }

    @Override
    protected void abi$appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.abi$appendHoverText(itemStack, tooltips, context);
        var paintColor = getItemColors(itemStack);
        if (paintColor != null && paintColor.isPureColor()) {
            tooltips.addAll(Colors.getColorTooltips(paintColor.get(OpenDirection.NORTH), true));
        }
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<CompoundTag> COLOR = IDataSerializerKey.create("Color", ExtraCodecs.COMPOUND_TAG);
    }
}
