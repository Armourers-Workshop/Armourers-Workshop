package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, )")
@Extension
public class OutlineColor {

    public static int getOutlineColor(@This Entity entity) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.levelRenderer.shouldShowEntityOutlines() && minecraft.shouldEntityAppearGlowing(entity)) {
            return entity.getTeamColor() | 0xff000000;
        }
        return 0;
    }
}
