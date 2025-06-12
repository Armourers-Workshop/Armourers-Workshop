package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockBenchKeyframe extends BlockBenchObject {

    private final float time;

    private final String interpolation;
    private final List<Float> parameters;
    private final List<Map<String, OpenPrimitive>> points;

    public BlockBenchKeyframe(String uuid, String name, float time, String interpolation, List<Float> parameters, List<Map<String, OpenPrimitive>> points) {
        super(uuid, name);
        this.time = time;
        this.interpolation = interpolation;
        this.parameters = parameters;
        this.points = points;
    }

    public float time() {
        return time;
    }

    public String interpolation() {
        return interpolation;
    }

    public List<Float> parameters() {
        return parameters;
    }

    public List<Map<String, OpenPrimitive>> points() {
        return points;
    }

    protected static class Builder extends BlockBenchObject.Builder {

        private float time = 0;

        private String interpolation = "liner"; // liner,smooth,bezier,step

        private List<Float> parameters;
        private final List<Map<String, OpenPrimitive>> points = new ArrayList<>();

        public void time(float time) {
            this.time = time;
        }

        public void interpolation(String interpolation) {
            this.interpolation = interpolation;
        }

        public void parameters(List<Float> parameters) {
            this.parameters = parameters;
        }

        public void point(Map<String, OpenPrimitive> point) {
            this.points.add(point);
        }

        public BlockBenchKeyframe build() {
            return new BlockBenchKeyframe(uuid, name, time, interpolation, parameters, points);
        }
    }
}
