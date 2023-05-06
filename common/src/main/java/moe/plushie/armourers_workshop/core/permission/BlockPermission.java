package moe.plushie.armourers_workshop.core.permission;

import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.init.platform.RegistryManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class BlockPermission extends Permission {

    @SafeVarargs
    public BlockPermission(String name, IRegistryKey<Block>... blocks) {
        super(name);
        Arrays.stream(blocks).forEach(this::add);
    }

    public boolean accept(Player player) {
        if (player == null) {
            return true;
        }
        return getNodes().stream().allMatch(node -> eval(node, player, new PlayerPermissionContext(player)));
    }

    public boolean accept(BlockEntity tileEntity, Player player) {
        BlockState state = tileEntity.getBlockState();
        IPermissionNode node = get(RegistryManager.getKey(state.getBlock()));
        return eval(node, player, new BlockPermissionContext(player, tileEntity.getBlockPos(), state, null));
    }
}
