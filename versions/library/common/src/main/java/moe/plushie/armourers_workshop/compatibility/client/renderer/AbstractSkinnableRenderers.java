package moe.plushie.armourers_workshop.compatibility.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;

@SuppressWarnings("rawtypes")
@Environment(EnvType.CLIENT)
public class AbstractSkinnableRenderers {

    public static final Class<ArrowRenderer> ARROW = ArrowRenderer.class;
    public static final Class<ThrownTridentRenderer> THROWN_TRIDENT = ThrownTridentRenderer.class;
}
