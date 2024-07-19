package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.api.data.IDataSerializerProvider;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;

public class TickTracker implements IDataSerializerProvider {

    private static final DataSerializerKey<Float> TIME_KEY = DataSerializerKey.create("Time", DataTypeCodecs.FLOAT, 0f);

    private static final TickTracker CLIENT = new TickTracker();
    private static final TickTracker SERVER = new TickTracker();

    private long lastTime = System.nanoTime();
    private float animationTicks = 0.0f;

    public static TickTracker client() {
        return CLIENT;
    }

    public static TickTracker server() {
        return SERVER;
    }

    public void update(boolean isPaused) {
        // when tick is pause, ignore any time advance.
        long time = System.nanoTime();
        if (!isPaused) {
            float delta = (time - lastTime) / 1e9f;
            animationTicks += delta * ModDebugger.animationSpeed;
        }
        lastTime = time;
    }

    public float animationTicks() {
        return animationTicks;
    }

    public void setAnimationTicks(float animationTicks) {
        this.animationTicks = animationTicks;
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(TIME_KEY, animationTicks);
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        animationTicks = serializer.read(TIME_KEY);
    }
}
