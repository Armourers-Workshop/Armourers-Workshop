package moe.plushie.armourers_workshop.init.platform;

import com.apple.library.coregraphics.CGGraphicsRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractModelPartRegistries;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class RendererManager {

    public static void init() {
        CGGraphicsRenderer.init();
        AbstractModelPartRegistries.init();
        SkinRendererRegistries.init();
        SkinRendererManager.getInstance().init();
    }

}
