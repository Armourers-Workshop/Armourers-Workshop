package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;

import moe.plushie.armourers_workshop.core.utils.OpenExpression;

public class BedrockEvent {

    private final OpenExpression expression;
    private final String soundId;
    private final String particleId;
    private final String particleType;
    private final OpenExpression particlePreExpression;

    public BedrockEvent(OpenExpression expression, String soundId, String particleId, String particleType, OpenExpression particlePreExpression) {
        this.expression = expression;
        this.soundId = soundId;
        this.particleId = particleId;
        this.particleType = particleType;
        this.particlePreExpression = particlePreExpression;
    }

    public OpenExpression expression() {
        return expression;
    }

    public String soundId() {
        return soundId;
    }

    public String particleId() {
        return particleId;
    }

    public String particleType() {
        return particleType;
    }

    public OpenExpression particlePreExpression() {
        return particlePreExpression;
    }

    protected static class Builder {

        private OpenExpression expression;
        private String soundId;
        private String particleId;
        private String particleType;
        private OpenExpression particlePreExpression;

        public void expression(OpenExpression expression) {
            this.expression = expression;
        }

        public void sound(String soundId) {
            this.soundId = soundId;
        }

        public void particle(String particleId, String type, OpenExpression pre) {
            this.particleId = particleId;
            this.particleType = type;
            this.particlePreExpression = pre;
            // emitter/emitter_bound/particle/particle_with_velocity
        }

        public BedrockEvent build() {
            return new BedrockEvent(expression, soundId, particleId, particleType, particlePreExpression);
        }
    }
}
