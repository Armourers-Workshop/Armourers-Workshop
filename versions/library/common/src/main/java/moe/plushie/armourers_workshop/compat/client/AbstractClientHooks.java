package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.render.element.SkinPartElement;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;

@OnlyIn(Dist.CLIENT)
public class AbstractClientHooks {

    public static void reloadResources() {
        SkinRendererManager.reload();
    }

    public static void createShaders() {
        //AbstractShaderUniformState.VERSION += 1;
    }

    public static void reloadShaders() {
        //AbstractShaderUniformState.VERSION += 1;
        SkinPartElement.clearCache();
    }

    public static void drawElements() {
        var callback = RenderSystem.getDrawElementsCallback();
        if (callback != null) {
            callback.run();
        }
    }
}
