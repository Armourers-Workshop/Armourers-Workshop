package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.core.data.EntityActionSet;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class EntityActionProvider {

    @Nullable
    public static EntityActionSet getActionSet(@This Entity entity) {
        var actionSet = EntityActionSet.of(entity);
        if (actionSet != null) {
            actionSet.tick(entity);
            return actionSet;
        }
        return null;
    }
}
