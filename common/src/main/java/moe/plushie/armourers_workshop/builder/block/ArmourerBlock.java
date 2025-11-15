package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.compat.core.block.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ArmourerBlock extends AbstractHorizontalBlock implements AbstractBlockEntityProvider {

    public ArmourerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity abi$createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.ARMOURER.get().create(level, blockPos, blockState);
    }

    @Override
    protected OpenInteractionResult abi$useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult) {
        return player.openMenu(ModMenuTypes.ARMOURER, level, blockPos);
    }

    @Override
    protected BlockState abi$rotate(BlockState blockState, Rotation rotation) {
        return blockState; // can't rotate
    }

    @Override
    protected BlockState abi$mirror(BlockState blockState, Mirror mirror) {
        return blockState; // can't mirror
    }

    @Override
    protected void abi$setPlacedBy(BlockState blockState, Level level, BlockPos blockPos, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.abi$setPlacedBy(blockState, level, blockPos, livingEntity, itemStack);
        this.applyTitleEntity(level, blockPos, te -> te.onPlace(level, blockPos, blockState, livingEntity));
    }

    @Override
    protected void abi$onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        this.applyTitleEntity(level, blockPos, te -> te.onRemove(level, blockPos, te.getBlockState()));
        super.abi$onRemove(blockState, level, blockPos, blockState2, bl);
    }

    private void applyTitleEntity(Level level, BlockPos pos, Consumer<ArmourerBlockEntity> consumer) {
        if (level.getBlockEntity(pos) instanceof ArmourerBlockEntity blockEntity) {
            consumer.accept(blockEntity);
        }
    }
}
