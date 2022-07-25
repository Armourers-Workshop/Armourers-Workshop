package moe.plushie.armourers_workshop.core.permission;

import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.platform.PermissionManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class BlockPermission extends Permission {

    @SafeVarargs
    public BlockPermission(String name, IRegistryObject<Block>... blocks) {
        super(name);
        Arrays.stream(blocks).forEach(this::add);
    }

    public boolean accept(Player player) {
        if (player == null) {
            return true;
        }
        PermissionManager.PlayerContext context = new PermissionManager.PlayerContext(player);
        return getNodes().stream().allMatch(node -> eval(node, player, context));
    }

    public boolean accept(BlockEntity tileEntity, Player player) {
        BlockState state = tileEntity.getBlockState();
        String node = get(Registry.BLOCK.getKey(state.getBlock()));
        return eval(node, player, new PermissionManager.BlockContext(player, tileEntity.getBlockPos(), state, null));
    }
}
