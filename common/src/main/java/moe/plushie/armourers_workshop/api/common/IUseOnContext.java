package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.core.utils.IDirection;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public interface IUseOnContext {

    Level level();

    IDirection clickedFace();

    OpenVector3d clickLocation();

    BlockPos clickedPos();

    Player player();

    OpenInteractionHand hand();

    ItemStack itemInHand();

    @Nullable
    BlockHitResult hitResult();
}
