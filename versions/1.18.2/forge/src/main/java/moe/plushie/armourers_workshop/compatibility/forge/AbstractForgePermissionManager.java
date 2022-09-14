package moe.plushie.armourers_workshop.compatibility.forge;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.permission.IPermissionContext;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.core.permission.BlockPermissionContext;
import moe.plushie.armourers_workshop.core.permission.PlayerPermissionContext;
import moe.plushie.armourers_workshop.core.permission.TargetPermissionContext;
import moe.plushie.armourers_workshop.init.platform.forge.builder.PermissionNodeBuilderImpl;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;

public abstract class AbstractForgePermissionManager {

    private static final ArrayList<PermissionNode<?>> PENDING = makeRegisterQueue();

    private static final PermissionDynamicContextKey<Entity> TARGET = new PermissionDynamicContextKey<>(Entity.class, "target", Object::toString);
    private static final PermissionDynamicContextKey<Player> PLAYER = new PermissionDynamicContextKey<>(Player.class, "player", Object::toString);

    private static final PermissionDynamicContextKey<BlockPos> BLOCK_POS = new PermissionDynamicContextKey<>(BlockPos.class, "block_pos", BlockPos::toShortString);
    private static final PermissionDynamicContextKey<BlockState> BLOCK_STATE = new PermissionDynamicContextKey<>(BlockState.class, "block_state", Object::toString);
    private static final PermissionDynamicContextKey<Direction> FACING = new PermissionDynamicContextKey<>(Direction.class, "facing", Direction::getSerializedName);

    public static IPermissionNode makeNode(ResourceLocation registryName, int level) {
        PermissionNode<Boolean> node = new PermissionNode<>(registryName, PermissionTypes.BOOLEAN, (player, uuid, contexts) -> true, TARGET, PLAYER, BLOCK_POS, BLOCK_STATE, FACING);
        IPermissionNode nodeImpl = new PermissionNodeBuilderImpl.NodeImpl(registryName) {

            @Override
            public boolean resolve(Player player, IPermissionContext context) {
                if (player instanceof ServerPlayer) {
                    return PermissionAPI.getPermission((ServerPlayer) player, node, makeContexts(context));
                }
                return super.resolve(player, context);
            }

            @Override
            public boolean resolve(GameProfile profile, IPermissionContext context) {
                return PermissionAPI.getOfflinePermission(profile.getId(), node, makeContexts(context));
            }
        };
        node.setInformation(nodeImpl.getName(), nodeImpl.getDescription());
        PENDING.add(node);
        return nodeImpl;
    }

    private static PermissionDynamicContext<?>[] makeContexts(IPermissionContext context) {
        ArrayList<PermissionDynamicContext<?>> contexts = new ArrayList<>();
        PlayerPermissionContext player = ObjectUtils.safeCast(context, PlayerPermissionContext.class);
        if (player != null && player.player != null) {
            contexts.add(PLAYER.createContext(player.player));
        }
        TargetPermissionContext target = ObjectUtils.safeCast(context, TargetPermissionContext.class);
        if (target != null && target.target != null) {
            contexts.add(TARGET.createContext(target.target));
        }
        BlockPermissionContext block = ObjectUtils.safeCast(context, BlockPermissionContext.class);
        if (block != null) {
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

    private static ArrayList<PermissionNode<?>> makeRegisterQueue() {
        MinecraftForge.EVENT_BUS.addListener(AbstractForgePermissionManager::registerNodes);
        return new ArrayList<>();
    }

    private static void registerNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(PENDING);
    }
}
