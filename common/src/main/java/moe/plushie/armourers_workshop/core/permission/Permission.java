package moe.plushie.armourers_workshop.core.permission;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public abstract class Permission {

    protected final String name;
    protected final HashMap<IResourceLocation, IPermissionNode> nodes = new HashMap<>();

    public Permission(String name) {
        this.name = name;
    }

    protected void add(IRegistryHolder<?> object) {
        var registryName = object.registryName();
        var node = makeNode(registryName.path() + "." + name);
        nodes.put(registryName, node);
    }

    protected IPermissionNode get(IResourceLocation registryName) {
        return nodes.get(registryName);
    }

    protected boolean eval(IPermissionNode node, Player player, @Nullable PlayerPermissionContext context) {
        // we allow server owner turn off permission checks.
        if (ModConfig.Common.enablePermissionCheck) {
            return node.resolve(player, context);
        }
        return true;
    }

    private IPermissionNode makeNode(String path) {
        return BuilderManager.getInstance().createPermissionBuilder().level(0).build(path);
    }

    public String name() {
        return name;
    }

    public Collection<IPermissionNode> nodes() {
        return nodes.values();
    }
}

