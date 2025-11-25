package moe.plushie.armourers_workshop.compat.core.item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.compat.core.AbstractDirection;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionHand;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@Available("[1.16, )")
public class AbstractUseOnContext implements IUseOnContext {

    private final UseOnContext context;

    public AbstractUseOnContext(UseOnContext context) {
        this.context = context;
    }

    public static UseOnContext unwrap(IUseOnContext context) {
        if (context instanceof AbstractUseOnContext context1) {
            return context1.context;
        }
        throw new RuntimeException("not supported class " + context.getClass());
    }

    public static AbstractUseOnContext wrap(UseOnContext context) {
        return new AbstractUseOnContext(context);
    }

    public static AbstractUseOnContext create(Level level, @Nullable Player player, OpenInteractionHand hand, ItemStack itemStack, BlockPos pos) {
        var traceResult = BlockHitResult.miss(Vec3.ZERO, Direction.NORTH, pos);
        return create(level, player, hand, itemStack, traceResult);
    }

    public static AbstractUseOnContext create(Level level, @Nullable Player player, OpenInteractionHand hand, ItemStack itemStack, BlockHitResult traceResult) {
        return wrap(new UseOnContext(level, player, AbstractInteractionHand.unwrap(hand), itemStack, traceResult));
    }

    @Override
    public Level level() {
        return context.getLevel();
    }

    @Override
    public OpenDirection clickedFace() {
        return AbstractDirection.wrap(context.getClickedFace());
    }

    @Override
    public OpenVector3d clickLocation() {
        var pos = context.getClickLocation();
        return new OpenVector3d(pos.x(), pos.y(), pos.z());
    }

    @Override
    public BlockPos clickedPos() {
        return context.getClickedPos();
    }

    @Override
    public Player player() {
        return context.getPlayer();
    }

    @Override
    public OpenInteractionHand hand() {
        return AbstractInteractionHand.wrap(context.getHand());
    }

    @Override
    public ItemStack itemInHand() {
        return context.getItemInHand();
    }

    @Nullable
    @Override
    public BlockHitResult hitResult() {
        return new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside());
    }
}
