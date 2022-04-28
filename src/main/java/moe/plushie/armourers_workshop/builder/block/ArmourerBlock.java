package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.core.block.AbstractHorizontalBlock;
import net.minecraft.util.Direction;

@SuppressWarnings("NullableProblems")
public class ArmourerBlock extends AbstractHorizontalBlock {

    public ArmourerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
}
