package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.render.element.SkinPartElement;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.event.client.ClientShaderEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;

@OnlyIn(Dist.CLIENT)
public class AbstractClientHooks {

    private static boolean RELOADING = false;

    public static void createShaders() {
        if (RELOADING) {
            return;
        }
        RELOADING = true;
        RenderSystem.recordRenderCall(() -> {
            RELOADING = false;
            reloadShaders();
        });
    }

    public static void reloadShaders() {
        ModLog.debug("Reloading shaders");
        EventManager.post(ClientShaderEvent.Reloading.class, new ReloadShader());
        SkinPartElement.clearCache();
    }

    public static void reloadResources() {
        ModLog.debug("Reloading resources");
        SkinRendererManager.reload();
    }

    public static void drawElements() {
        var callback = RenderSystem.getDrawElementsCallback();
        if (callback != null) {
            callback.run();
        }
    }

    private static class ReloadShader implements ClientShaderEvent.Reloading {

    }
}
