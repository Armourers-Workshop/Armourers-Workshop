package moe.plushie.armourers_workshop.common.init.items.block;

import net.minecraft.block.Block;

public class ModItemBlockNoStack extends ModItemBlock {

    public ModItemBlockNoStack(Block block) {
        super(block);
        setMaxStackSize(1);
    }
}
