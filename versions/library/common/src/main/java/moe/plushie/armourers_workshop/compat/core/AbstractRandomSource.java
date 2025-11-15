package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IRandomSource;
import net.minecraft.util.RandomSource;

@Available("[1.19, )")
public class AbstractRandomSource implements IRandomSource {

    private final RandomSource random;

    public AbstractRandomSource(RandomSource random) {
        this.random = random;
    }

    public static IRandomSource wrap(RandomSource source) {
        return new AbstractRandomSource(source);
    }

    public static RandomSource unwrap(IRandomSource source) {
        return ((AbstractRandomSource) source).random;
    }

    @Override
    public int nextInt() {
        return random.nextInt();
    }

    @Override
    public int nextInt(int i) {
        return random.nextInt(i);
    }

}
