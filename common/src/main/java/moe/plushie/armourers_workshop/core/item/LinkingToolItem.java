package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IItemHandler;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class LinkingToolItem extends FlavouredItem implements IItemHandler, IItemPropertiesProvider {

    public LinkingToolItem(Properties properties) {
        super(properties);
    }

    public static void setLinkedBlockPos(ItemStack itemStack, BlockPos pos) {
        DataSerializers.putBlockPos(itemStack.getOrCreateTag(), Constants.Key.TILE_ENTITY_LINKED_POS, pos, null);
    }

    @Nullable
    public static BlockPos getLinkedBlockPos(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        if (tag != null) {
            return DataSerializers.getBlockPos(tag, Constants.Key.TILE_ENTITY_LINKED_POS, null);
        }
        return null;
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(ModConstants.key("empty"), (itemStack, level, entity, id) -> {
            CompoundTag tag = itemStack.getTag();
            if (tag != null && tag.contains(Constants.Key.TILE_ENTITY_LINKED_POS)) {
                return 0;
            }
            return 1;
        });
    }

    @Override
    public InteractionResult useOnFirst(ItemStack itemStack, UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }
        BlockPos linkedBlockPos = getLinkedBlockPos(itemStack);
        SkinnableBlockEntity tileEntity = getTitleEntity(level, context.getClickedPos());
        if (tileEntity != null && player.isShiftKeyDown()) {
            tileEntity.setLinkedBlockPos(null);
            player.sendSystemMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.clear"));
            return InteractionResult.SUCCESS;
        }
        if (linkedBlockPos != null) {
            setLinkedBlockPos(itemStack, null);
            if (tileEntity != null) {
                tileEntity.setLinkedBlockPos(linkedBlockPos);
                player.sendSystemMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.finish"));
                return InteractionResult.SUCCESS;
            }
            player.sendSystemMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.fail"));
            return InteractionResult.SUCCESS;
        }
        if (tileEntity != null) {
            player.sendSystemMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.linkedToSkinnable"));
            return InteractionResult.FAIL;
        }
        setLinkedBlockPos(itemStack, context.getClickedPos());
        player.sendSystemMessage(TranslateUtils.title("inventory.armourers_workshop.linking-tool.start"));
        return InteractionResult.SUCCESS;
    }

    private SkinnableBlockEntity getTitleEntity(Level level, BlockPos blockPos) {
        BlockEntity tileEntity = level.getBlockEntity(blockPos);
        if (tileEntity instanceof SkinnableBlockEntity) {
            return (SkinnableBlockEntity) tileEntity;
        }
        return null;
    }
}
