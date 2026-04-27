package moe.plushie.armourers_workshop.compat.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IGameProfile;
import moe.plushie.armourers_workshop.api.permission.IPermissionContext;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.core.permission.BlockPermissionContext;
import moe.plushie.armourers_workshop.core.permission.PlayerPermissionContext;
import moe.plushie.armourers_workshop.core.permission.TargetPermissionContext;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.platform.forge.builder.PermissionNodeBuilderImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;

@Available("[21, )")
public abstract class AbstractForgePermissionManager {

    private static final ArrayList<PermissionNode<?>> PENDING = makeRegisterQueue();

    private static final PermissionDynamicContextKey<Entity> TARGET = new PermissionDynamicContextKey<>(Entity.class, "target", Object::toString);
    private static final PermissionDynamicContextKey<Player> PLAYER = new PermissionDynamicContextKey<>(Player.class, "player", Object::toString);

    private static final PermissionDynamicContextKey<BlockPos> BLOCK_POS = new PermissionDynamicContextKey<>(BlockPos.class, "block_pos", BlockPos::toShortString);
    private static final PermissionDynamicContextKey<BlockState> BLOCK_STATE = new PermissionDynamicContextKey<>(BlockState.class, "block_state", Object::toString);
    private static final PermissionDynamicContextKey<Direction> FACING = new PermissionDynamicContextKey<>(Direction.class, "facing", Direction::getSerializedName);

    public static IPermissionNode makeNode(OpenResourceKey registryName, int level) {
        var node = new PermissionNode<>(registryName.get(), PermissionTypes.BOOLEAN, (player, uuid, contexts) -> true, TARGET, PLAYER, BLOCK_POS, BLOCK_STATE, FACING);
        var nodeImpl = new PermissionNodeBuilderImpl.NodeImpl(registryName) {

            @Override
            public boolean resolve(Player player, IPermissionContext context) {
                if (!hasPermissionAPI()) {
                    return true;
                }
                if (player instanceof ServerPlayer) {
                    return PermissionAPI.getPermission((ServerPlayer) player, node, makeContexts(context));
                }
                return false;
            }

            @Override
            public boolean resolve(IGameProfile profile, IPermissionContext context) {
                if (!hasPermissionAPI()) {
                    return true;
                }
                return PermissionAPI.getOfflinePermission(profile.id(), node, makeContexts(context));
            }
        };
        node.setInformation(nodeImpl.name(), nodeImpl.description());
        PENDING.add(node);
        return nodeImpl;
    }

    private static PermissionDynamicContext<?>[] makeContexts(IPermissionContext context) {
        var contexts = new ArrayList<PermissionDynamicContext<?>>();
        if (context instanceof PlayerPermissionContext player && player.player != null) {
            contexts.add(PLAYER.createContext(player.player));
        }
        if (context instanceof TargetPermissionContext target && target.target != null) {
            contexts.add(TARGET.createContext(target.target));
        }
        if (context instanceof BlockPermissionContext block) {
            if (block.blockPos != null) {
                contexts.add(BLOCK_POS.createContext(block.blockPos));
            }
            if (block.blockState != null) {
                contexts.add(BLOCK_STATE.createContext(block.blockState));
            }
            if (block.facing != null) {
                contexts.add(FACING.createContext(block.facing));
            }
        }
        return contexts.toArray(new PermissionDynamicContext<?>[0]);
    }

    private static boolean hasPermissionAPI() {
        // in version 1.18, the permission api only available on the service side.
        return ServerLifecycleHooks.getCurrentServer() != null;
    }

    private static ArrayList<PermissionNode<?>> makeRegisterQueue() {
        AbstractForgeEventBus.observer(PermissionGatherEvent.Nodes.class, event -> event.addNodes(PENDING));
        return new ArrayList<>();
    }
}
