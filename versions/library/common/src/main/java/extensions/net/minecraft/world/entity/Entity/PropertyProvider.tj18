package extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.18, 1.20)")
public class PropertyProvider {

    public static void setLevel(@This Entity entity, Level level) {
        entity.level = level;
    }
}

