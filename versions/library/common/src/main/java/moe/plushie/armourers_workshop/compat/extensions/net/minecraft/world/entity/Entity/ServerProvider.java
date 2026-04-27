package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[16, )")
@Extension
public class ServerProvider {

    public static MinecraftServer server(@This Entity entity) {
        var level = entity.level();
        return level.getServer();
    }
}
