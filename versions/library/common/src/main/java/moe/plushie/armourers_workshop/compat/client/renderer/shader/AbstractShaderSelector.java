package moe.plushie.armourers_workshop.compat.client.renderer.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@Available("[1.18, 1.26)")
public class AbstractShaderSelector {

    public static final List<ResourceLocation> DEFAULT = Collections.immutableList(builder -> {
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_entity_solid.vsh"));
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_entity_shadow.vsh"));
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_entity_cutout.vsh"));
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_energy_swirl.vsh"));
        builder.add(ResourceLocation.withDefaultNamespace("shaders/core/rendertype_outline.vsh"));
    });
}
