package moe.plushie.armourers_workshop.builder.client.render.guide;

import moe.plushie.armourers_workshop.api.client.guide.IGuideRenderer;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;

@Environment(value = EnvType.CLIENT)
public class GuideRendererManager {

    private final HashMap<ISkinPartType, IGuideRenderer> renderers = new HashMap<>();

    public GuideRendererManager() {
        register(new HeadGuideRenderer());
        register(new ChestGuideRenderer());
        register(new FeetGuideRenderer());
        register(new HeldItemGuideRenderer());
        register(new WingsGuideRenderer());
    }

    private void register(AbstractGuideRenderer renderer) {
        renderer.init(this);
    }

    public void register(ISkinPartType partType, IGuideRenderer renderer) {
        renderers.put(partType, renderer);
    }

    public IGuideRenderer getRenderer(ISkinPartType partType) {
        return renderers.get(partType);
    }
}

