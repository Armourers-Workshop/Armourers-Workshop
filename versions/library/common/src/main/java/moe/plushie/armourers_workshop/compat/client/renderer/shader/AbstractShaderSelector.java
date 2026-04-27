package moe.plushie.armourers_workshop.compat.client.renderer.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

import java.util.List;

@Available("[18, 26)")
public class AbstractShaderSelector {

    public static final List<OpenResourceKey> DEFAULT = Collections.immutableList(builder -> {
        builder.add(OpenResourceKey.withDefaultNamespace("shaders/core/rendertype_entity_solid.vsh"));
        builder.add(OpenResourceKey.withDefaultNamespace("shaders/core/rendertype_entity_shadow.vsh"));
        builder.add(OpenResourceKey.withDefaultNamespace("shaders/core/rendertype_entity_cutout.vsh"));
        builder.add(OpenResourceKey.withDefaultNamespace("shaders/core/rendertype_energy_swirl.vsh"));
        builder.add(OpenResourceKey.withDefaultNamespace("shaders/core/rendertype_outline.vsh"));
    });
}
