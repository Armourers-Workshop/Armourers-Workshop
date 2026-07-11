package moe.plushie.armourers_workshop.core.skin.particle;


import moe.plushie.armourers_workshop.api.skin.particle.ISkinParticleData;
import moe.plushie.armourers_workshop.core.skin.particle.math.ParticleMaterial;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;

import java.util.List;
import java.util.Map;

public class SkinParticleData implements ISkinParticleData {

    private final String name;
    private final ParticleMaterial material;
    private final SkinTextureData textureData;

    private final Map<String, SkinParticleCurve> curves;
    private final Map<String, SkinParticleEvent> events;

    private final List<? extends SkinParticleComponent> components;

    public SkinParticleData(String name, ParticleMaterial material, SkinTextureData textureData, Map<String, SkinParticleCurve> curves, Map<String, SkinParticleEvent> events, List<? extends SkinParticleComponent> components) {
        this.name = name;
        this.material = material;
        this.textureData = textureData;
        this.curves = curves;
        this.events = events;
        this.components = components;
    }

    public String name() {
        return name;
    }

    public ParticleMaterial material() {
        return material;
    }

    public SkinTextureData texture() {
        return textureData;
    }

    public Map<String, SkinParticleCurve> curves() {
        return curves;
    }

    public Map<String, SkinParticleEvent> events() {
        return events;
    }

    public List<? extends SkinParticleComponent> components() {
        return components;
    }
}
