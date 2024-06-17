package moe.plushie.armourers_workshop.core.permission;

import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class BlockPermission extends Permission {

    @SafeVarargs
    public BlockPermission(String name, IRegistryHolder<Block>... blocks) {
        super(name);
        Arrays.stream(blocks).forEach(this::add);
    }

    public boolean accept(Player player) {
        if (player == null) {
            return true;
        }
        return getNodes().stream().allMatch(node -> eval(node, player, new PlayerPermissionContext(player)));
    }

    public boolean accept(BlockEntity blockEntity, Player player) {
        var state = blockEntity.getBlockState();
        var node = get(TypedRegistry.findKey(state.getBlock()));
        return eval(node, player, new BlockPermissionContext(player, blockEntity.getBlockPos(), state, null));
    }
}
