package moe.plushie.armourers_workshop.api.painting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public interface IPaintingPredicate {

    boolean shouldApplyColor(World worldIn, IPaintable target, Direction direction, Item item, ItemUseContext context);
}
