package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.common.IGameProfile;
import moe.plushie.armourers_workshop.api.permission.IPermissionContext;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.api.registry.IPermissionNodeBuilder;
import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricPermissionManager;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class PermissionNodeBuilderImpl<T extends IPermissionNode> implements IPermissionNodeBuilder<T> {

    private int level = 0;

    @Override
    public IPermissionNodeBuilder<T> level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public T build(String name) {
        var registryName = ModConstants.key(name);
        ModLog.debug("Registering Permission '{}'", registryName);
        return Objects.unsafeCast(makeNode(registryName, level));
    }

    private static IPermissionNode makeNode(OpenResourceKey registryName, int level) {
        // requires install fabric permission api, like luckperm mod.
        if (FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0")) {
            var node = (Supplier<Supplier<IPermissionNode>>) () -> () -> AbstractFabricPermissionManager.makeNode(registryName, level);
            return node.get().get();
        }
        return new NodeImpl(registryName);
    }


    public static class NodeImpl implements IPermissionNode {

        private final String key;
        private final OpenResourceKey registryName;

        public NodeImpl(OpenResourceKey registryName) {
            this.registryName = registryName;
            this.key = registryName.toLanguageKey();
        }

        @Override
        public boolean resolve(Player player, IPermissionContext context) {
            return true;
        }

        @Override
        public boolean resolve(IGameProfile profile, IPermissionContext context) {
            return true;
        }

        public String key() {
            return key;
        }

        @Override
        public Component name() {
            return Component.translatable("permission." + key);
        }

        @Override
        public Component description() {
            return Component.translatable("permission." + key + ".desc");
        }

        @Override
        public OpenResourceKey registryName() {
            return registryName;
        }
    }
}
