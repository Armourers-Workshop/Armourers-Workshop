package moe.plushie.armourers_workshop.core.permission;

import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class ContainerPermission extends Permission {

    public ContainerPermission(String name, Consumer<Consumer<IRegistryKey<?>>> each) {
        super(name);
        each.accept(this::add);
    }

    public <T extends AbstractContainerMenu> boolean accept(IRegistryKey<MenuType<T>> type, Entity target, Player player) {
        IPermissionNode node = get(type.getRegistryName());
        return eval(node, player, new TargetPermissionContext(player, target));
    }

    public <T extends AbstractContainerMenu> boolean accept(IRegistryKey<MenuType<T>> type, Level level, BlockPos pos, Player player) {
        IPermissionNode node = get(type.getRegistryName());
        return eval(node, player, new BlockPermissionContext(player, pos, level.getBlockState(pos), null));
    }
}
