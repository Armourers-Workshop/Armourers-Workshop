package moe.plushie.armourers_workshop.core.permission;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Permission {

    protected final String name;
    protected final ArrayList<String> nodes = new ArrayList<>();

    public Permission(String name) {
        this.name = name;
    }

    protected void add(IForgeRegistryEntry<?> entry) {
        nodes.add(get(entry));
    }

    protected String get(IForgeRegistryEntry<?> entry) {
        ResourceLocation registryName = Objects.requireNonNull(entry.getRegistryName());
        return String.format("%s.%s.%s", registryName.getNamespace(), registryName.getPath(), name);
    }

    protected boolean eval(String node, PlayerEntity player, @Nullable IContext context) {
        return PermissionAPI.hasPermission(player.getGameProfile(), node, context);
    }

    public String getName() {
        return name;
    }

    public List<String> getNodes() {
        return nodes;
    }
}
