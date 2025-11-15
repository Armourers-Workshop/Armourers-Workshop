package moe.plushie.armourers_workshop.core.permission;

import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.function.Supplier;

public class ContainerPermission extends Permission {

    public ContainerPermission(String name, Supplier<Collection<? extends IRegistryHolder<? extends IMenuType<?>>>> entities) {
        super(name);
        entities.get().forEach(this::add);
    }

    public boolean accept(IRegistryHolder<? extends IMenuType<?>> type, Entity target, Player player) {
        var node = get(type.registryName());
        return eval(node, player, new TargetPermissionContext(player, target));
    }

    public boolean accept(IRegistryHolder<? extends IMenuType<?>> type, Level level, BlockPos pos, Player player) {
        var node = get(type.registryName());
        return eval(node, player, new BlockPermissionContext(player, pos, level.getBlockState(pos), null));
    }
}
