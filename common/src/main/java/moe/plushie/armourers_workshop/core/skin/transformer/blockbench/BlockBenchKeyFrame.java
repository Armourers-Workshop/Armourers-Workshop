package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

public class BlockBenchKeyFrame extends BlockBenchObject {

    private final float time;

    private final String channel;
    private final String interpolation;

    public BlockBenchKeyFrame(String uuid, String name, float time, String channel, String interpolation) {
        super(uuid, name);
        this.time = time;
        this.channel = channel;
        this.interpolation = interpolation;
    }

    public float getTime() {
        return time;
    }

    public String getChannel() {
        return channel;
    }

    public String getInterpolation() {
        return interpolation;
    }

    public static class Builder extends BlockBenchObject.Builder {

        private float time = 0;

        private String channel = "position"; // position,rotation,scale
        private String interpolation = "liner"; // liner,smooth,bezier,step

        public void time(float time) {
            this.time = time;
        }

        public void channel(String channel) {
            this.channel = channel;
        }

        public void interpolation(String interpolation) {
            this.interpolation = interpolation;
        }

        public BlockBenchKeyFrame build() {
            return new BlockBenchKeyFrame(uuid, name, time, channel, interpolation);
        }
    }
}
