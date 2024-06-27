package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.api.data.IDataPackObject;

import java.util.ArrayList;
import java.util.List;

public class BlockBenchKeyFrame extends BlockBenchObject {

    private final float time;

    private final String interpolation;
    private final List<Float> parameters;
    private final List<Object> points;

    public BlockBenchKeyFrame(String uuid, String name, float time, String interpolation, List<Float> parameters, List<Object> points) {
        super(uuid, name);
        this.time = time;
        this.interpolation = interpolation;
        this.parameters = parameters;
        this.points = points;
    }

    public float getTime() {
        return time;
    }

    public String getInterpolation() {
        return interpolation;
    }

    public List<Float> getParameters() {
        return parameters;
    }

    public List<Object> getPoints() {
        return points;
    }

    public static class Builder extends BlockBenchObject.Builder {

        private float time = 0;

        private String interpolation = "liner"; // liner,smooth,bezier,step

        private List<Float> parameters;
        private final ArrayList<Object> points = new ArrayList<>();

        public void time(float time) {
            this.time = time;
        }

        public void interpolation(String interpolation) {
            this.interpolation = interpolation;
        }

        public void parameters(List<Float> parameters) {
            this.parameters = parameters;
        }

        public void add(IDataPackObject value) {
            switch (value.type()) {
                case NUMBER -> points.add(value.floatValue());
                case STRING -> points.add(value.stringValue());
                default -> points.add("");
            }
        }

        public BlockBenchKeyFrame build() {
            return new BlockBenchKeyFrame(uuid, name, time, interpolation, parameters, points);
        }
    }
}
