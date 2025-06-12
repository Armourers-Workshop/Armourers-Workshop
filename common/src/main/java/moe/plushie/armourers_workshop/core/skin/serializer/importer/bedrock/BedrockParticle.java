package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;

import java.util.LinkedHashMap;
import java.util.Map;

public class BedrockParticle {

    private final String name;
    private final String material;
    private final String texture;

    private final Map<String, BedrockCurve> curves;
    private final Map<String, BedrockComponent> components;
    private final Map<String, BedrockEvent> events;

    private final String format;

    public BedrockParticle(String name, String material, String texture, Map<String, BedrockCurve> curves, Map<String, BedrockComponent> components, Map<String, BedrockEvent> events, String format) {
        this.name = name;
        this.material = material;
        this.texture = texture;
        this.curves = curves;
        this.components = components;
        this.events = events;
        this.format = format;
    }

    public String name() {
        return name;
    }

    public String material() {
        return material;
    }

    public String texture() {
        return texture;
    }

    public Map<String, BedrockCurve> curves() {
        return curves;
    }

    public Map<String, BedrockComponent> components() {
        return components;
    }

    public Map<String, BedrockEvent> events() {
        return events;
    }

    public String format() {
        return format;
    }

    protected static class Builder {

        private String format = "1.10";

        private String name = "";
        private String material = ""; // particles_alpha/particles_blend/particles_add/particles_opaque/custom...
        private String texture = ""; // textures/particle/particles

        private final Map<String, BedrockCurve> curves = new LinkedHashMap<>();
        private final Map<String, BedrockComponent> components = new LinkedHashMap<>();
        private final Map<String, BedrockEvent> events = new LinkedHashMap<>();

        public void format(String format) {
            this.format = format;
        }

        public void name(String name) {
            this.name = name;
        }

        public void material(String material) {
            this.material = material;
        }

        public void texture(String texture) {
            this.texture = texture;
        }

        public void addCurve(String name, BedrockCurve curve) {
            this.curves.put(name, curve);
        }

        public void addComponent(String name, BedrockComponent component) {
            this.components.put(name, component);
        }

        public void addEvent(String name, BedrockEvent event) {
            this.events.put(name, event);
        }

        public BedrockParticle build() {
            return new BedrockParticle(name, material, texture, curves, components, events, format);
        }
    }
}

