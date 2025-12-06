package moe.plushie.armourers_workshop.builder.client.gui.armourer.guide;

import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public class GuideRendererManager {

    private final HashMap<SkinPartType, GuideRenderer> renderers = new HashMap<>();
    private final HashMap<Pair<PlayerSkinModel, SkinPartType>, GuideRenderer> variantRenderers = new HashMap<>();

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

    public void register(SkinPartType partType, GuideRenderer renderer) {
        renderers.put(partType, renderer);
    }

    public void register(PlayerSkinModel model, SkinPartType partType, GuideRenderer renderer) {
        variantRenderers.put(Pair.of(model, partType), renderer);
    }

    public GuideRenderer getRenderer(PlayerSkinModel model, SkinPartType partType) {
        return variantRenderers.computeIfAbsent(Pair.of(model, partType), key -> {
            return renderers.get(key.getValue());
        });
    }
}

