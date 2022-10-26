package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class AbstractRenderSystem extends RenderSystem {

    public static void init() {
        AbstractShaders.initShader();
    }
}
