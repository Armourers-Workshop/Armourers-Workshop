package moe.plushie.armourers_workshop.core.permission;

import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.init.platform.PermissionManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Permission {

    protected final String name;
    protected final ArrayList<String> nodes = new ArrayList<>();

    public Permission(String name) {
        this.name = name;
    }

    protected void add(IRegistryObject<?> object) {
        nodes.add(get(object.getRegistryName()));
    }

    protected String get(ResourceLocation registryName) {
        return String.format("%s.%s.%s", registryName.getNamespace(), registryName.getPath(), name);
    }

    protected boolean eval(String node, Player player, @Nullable PermissionManager.PlayerContext context) {
        return PermissionManager.hasPermission(player.getGameProfile(), node, context);
    }

    public String getName() {
        return name;
    }

    public List<String> getNodes() {
        return nodes;
    }
}
