package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;

import moe.plushie.armourers_workshop.core.utils.OpenExpression;

import java.util.List;

public class BedrockCurve {

    private final String type;

    private final OpenExpression input;
    private final OpenExpression range;

    private final List<Float> parameters;

    public BedrockCurve(String type, OpenExpression input, OpenExpression range, List<Float> parameters) {
        this.type = type;
        this.input = input;
        this.range = range;
        this.parameters = parameters;
    }

    public String type() {
        return type;
    }

    public OpenExpression input() {
        return input;
    }


    public OpenExpression range() {
        return range;
    }

    public List<Float> parameters() {
        return parameters;
    }

    protected static class Builder {

        private String type = "linear"; // catmull_rom, linear, bezier, bezier_chain

        private OpenExpression input;
        private OpenExpression range;
        private List<Float> parameters = null;

        public void type(String type) {
            this.type = type;
        }

        public void input(OpenExpression input) {
            this.input = input;
        }

        public void range(OpenExpression range) {
            this.range = range;
        }

        public void parameters(List<Float> parameters) {
            this.parameters = parameters;
        }

        public BedrockCurve build() {
            return new BedrockCurve(type, input, range, parameters);
        }

    }
}
