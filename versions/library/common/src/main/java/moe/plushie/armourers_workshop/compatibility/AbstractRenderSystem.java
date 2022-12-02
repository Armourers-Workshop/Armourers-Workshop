package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Available("[1.18, )")
@Environment(value = EnvType.CLIENT)
public class AbstractRenderSystem extends RenderSystem {


    public static void disableAlphaTest() {
    }

    public static void enableAlphaTest() {
    }

    public static void init() {
    }
}
