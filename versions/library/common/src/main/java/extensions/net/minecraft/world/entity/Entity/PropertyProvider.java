package extensions.net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@Extension
@Available("[1.20, )")
public class PropertyProvider {

    public static Level getLevel(@This Entity entity) {
        return entity.level();
    }
}

