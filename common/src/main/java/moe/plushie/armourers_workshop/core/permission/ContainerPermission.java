package moe.plushie.armourers_workshop.core.permission;

import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.platform.PermissionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

public class ContainerPermission extends Permission {

    public ContainerPermission(String name) {
        super(name);
        Registry.MENU_TYPE.getEntries().forEach(this::add);
    }

    public <T extends AbstractContainerMenu> boolean accept(IRegistryObject<MenuType<T>> type, Entity target, Player player) {
        String node = get(type.getRegistryName());
        return eval(node, player, new PermissionManager.TargetContext(player, target));
    }

    public <T extends AbstractContainerMenu> boolean accept(IRegistryObject<MenuType<T>> type, Level world, BlockPos pos, Player player) {
        String node = get(type.getRegistryName());
        return eval(node, player, new PermissionManager.BlockContext(player, pos, world.getBlockState(pos), null));
    }
}
