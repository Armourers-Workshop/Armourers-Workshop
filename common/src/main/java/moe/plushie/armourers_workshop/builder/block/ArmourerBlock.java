package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.compatibility.core.AbstractBlockEntityProvider;
import moe.plushie.armourers_workshop.compatibility.core.AbstractHorizontalBlock;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
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
    public BlockEntity createBlockEntity(BlockGetter level, BlockPos blockPos, BlockState blockState) {
        return ModBlockEntityTypes.ARMOURER.get().create(level, blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        return ModMenuTypes.ARMOURER.get().openMenu(player, level.getBlockEntity(blockPos));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation p_185499_2_) {
        return state; // can't rotate
    }

    @Override
    public BlockState mirror(BlockState state, Mirror p_185471_2_) {
        return state; // can't mirror
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        this.applyTitleEntity(level, blockPos, te -> te.onPlace(level, blockPos, blockState, livingEntity));
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        this.applyTitleEntity(level, blockPos, te -> te.onRemove(level, blockPos, te.getBlockState()));
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    private void applyTitleEntity(Level level, BlockPos pos, Consumer<ArmourerBlockEntity> consumer) {
        if (level.getBlockEntity(pos) instanceof ArmourerBlockEntity blockEntity) {
            consumer.accept(blockEntity);
        }
    }
}
