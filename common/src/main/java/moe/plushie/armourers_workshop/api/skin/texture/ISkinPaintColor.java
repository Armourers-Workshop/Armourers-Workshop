package moe.plushie.armourers_workshop.api.skin.texture;

public interface ISkinPaintColor {

    int argb();

    int rawValue();

    ISkinPaintType paintType();

    default int red() {
        return (argb() >> 16) & 0xff;
    }

    default int green() {
        return (argb() >> 8) & 0xff;
    }

    default int blue() {
        return argb() & 0xff;
    }

    ISkinPaintColor withPaintType(ISkinPaintType paintType);

    ISkinPaintColor withColor(int argb);

    default ISkinPaintColor withColor(int red, int green, int blue) {
        return withColor(red << 16 | green << 8 | blue);
    }
}
