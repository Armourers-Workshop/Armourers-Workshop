package moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This component sets the color tinting of the particle.
 */
public class ParticleTintingAppearance implements SkinParticleComponent {

    /// interpolation based color
    private final ColorComponent color;

    public ParticleTintingAppearance(OpenPrimitive interpolation, Map<Float, Integer> gradientValues) {
        this.color = new GradientColor(interpolation, gradientValues);
    }

    public ParticleTintingAppearance(OpenPrimitive red, OpenPrimitive green, OpenPrimitive blue, OpenPrimitive alpha) {
        this.color = new SolidColor(red, green, blue, alpha);
    }

    public ParticleTintingAppearance(IInputStream stream) throws IOException {
        var isGradient = stream.readBoolean();
        if (isGradient) {
            this.color = new GradientColor(stream);
        } else {
            this.color = new SolidColor(stream);
        }
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeBoolean(color instanceof GradientColor);
        color.writeToStream(stream);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        color.compile(generator);
    }

    @Override
    public int priority() {
        return -10;
    }

//    // interpolation based color
//    "color": {
//        // an array of colors
//        // there are two ways to specify the array of colors
//        // the first method is to just have an array of colors
//        // these will be interpreted to be equally spaced, with the entire
//        // range going from 0 to 1
//        //
//        // the second option is to specify a value/color pair list
//        // this will cause the colors to appear when their specified value
//        // occurs in the interpolant, and interpolated in between.  Note
//        // that this will be sorted
//        "gradient": [ <color>, <color>, ...],
//        "gradient": {
//        <float>: <color>,
//        <float>: <color>,
//        ...
//        }
//        "interpolant": <float/molang> // hint: use a curve here!
//    }
//
//    // directly set the color
//    "color": <color>
//    // examples of direct color field:
//    "color": "#ff0000"
//            "color": [1, 0, 0]
    private interface ColorComponent {

        void writeToStream(IOutputStream stream) throws IOException;

        void compile(SkinParticleGenerator generator);
    }

    /**
     * directly set the color
     */
    private static class SolidColor implements ColorComponent {

        private final OpenPrimitive red;
        private final OpenPrimitive green;
        private final OpenPrimitive blue;
        private final OpenPrimitive alpha;

        private SolidColor(OpenPrimitive red, OpenPrimitive green, OpenPrimitive blue, OpenPrimitive alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public SolidColor(IInputStream stream) throws IOException {
            this.red = stream.readPrimitiveObject();
            this.green = stream.readPrimitiveObject();
            this.blue = stream.readPrimitiveObject();
            this.alpha = stream.readPrimitiveObject();
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writePrimitiveObject(red);
            stream.writePrimitiveObject(green);
            stream.writePrimitiveObject(blue);
            stream.writePrimitiveObject(alpha);
        }

        @Override
        public void compile(SkinParticleGenerator generator) {
            var red = generator.compile(this.red, 1.0);
            var green = generator.compile(this.green, 1.0);
            var blue = generator.compile(this.blue, 1.0);
            var alpha = generator.compile(this.alpha, 1.0);
            generator.instance().render((emitter, particle, context) -> {
                var r = (float) red.compute(context);
                var g = (float) green.compute(context);
                var b = (float) blue.compute(context);
                var a = (float) alpha.compute(context);
                particle.setTintColor(r, g, b, a);
            });
        }
    }

    /**
     * interpolation based color
     */
    private static class GradientColor implements ColorComponent {

        /// hint: use a curve here!
        private final OpenPrimitive interpolation;

        /// an array of colors
        /// there are two ways to specify the array of colors
        /// the first method is to just have an array of colors
        /// these will be interpreted to be equally spaced, with the entire
        /// range going from 0 to 1
        ///
        /// the second option is to specify a value/color pair list
        /// this will cause the colors to appear when their specified value
        /// occurs in the interpolant, and interpolated in between.  Note
        /// that this will be sorted
        private final Map<Float, Integer> gradientValues;

        private GradientColor(OpenPrimitive interpolation, Map<Float, Integer> gradientValues) {
            this.interpolation = interpolation;
            this.gradientValues = gradientValues;
        }

        public GradientColor(IInputStream stream) throws IOException {
            this.interpolation = stream.readPrimitiveObject();
            this.gradientValues = new LinkedHashMap<>();
            var size = stream.readVarInt();
            for (int i = 0; i < size; i++) {
                var key = stream.readFloat();
                var value = stream.readInt();
                this.gradientValues.put(key, value);
            }
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writePrimitiveObject(interpolation);
            stream.writeVarInt(gradientValues.size());
            for (var entry : gradientValues.entrySet()) {
                stream.writeFloat(entry.getKey());
                stream.writeInt(entry.getValue());
            }
        }

        @Override
        public void compile(SkinParticleGenerator generator) {
            var interpolation = generator.compile(this.interpolation, 1.0);
            var stops = ColorStop.create(this.gradientValues);
            var length = stops.size();
            generator.instance().render((emitter, particle, context) -> {
                // we will use last item when factor not matched.
                var left = stops.get(0);
                var right = stops.get(length - 1);
                var progress = 1.0f;

                var factor = interpolation.compute(context);
                factor = OpenMath.clamp(factor, 0.0, 1.0);
                for (var i = 1; i < length; i++) {
                    var cur = stops.get(i);
                    if (cur.stop > factor) {
                        left = stops.get(i - 1);
                        right = cur;
                        progress = ((float) factor - left.stop) / (right.stop - left.stop);
                        break;
                    }
                }

                var r = OpenMath.lerp(progress, left.red, right.red);
                var g = OpenMath.lerp(progress, left.green, right.green);
                var b = OpenMath.lerp(progress, left.blue, right.blue);
                var a = OpenMath.lerp(progress, left.alpha, right.alpha);

                particle.setTintColor(r, g, b, a);
            });
        }
    }

    private static class ColorStop {

        private final float stop;

        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;

        ColorStop(float stop, float red, float green, float blue, float alpha) {
            this.stop = stop;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public static List<ColorStop> create(Map<Float, Integer> values) {
            var result = new ArrayList<ColorStop>();
            for (var entry : values.entrySet()) {
                var index = entry.getKey();
                var rawValue = entry.getValue();
                var red = Colors.getRed(rawValue) / 255.0f;
                var green = Colors.getGreen(rawValue) / 255.0f;
                var blue = Colors.getBlue(rawValue) / 255.0f;
                var alpha = Colors.getAlpha(rawValue) / 255.0f;
                result.add(new ColorStop(index, red, green, blue, alpha));
            }
            result.sort(Comparator.comparingDouble(it -> it.stop));
            // make sure it is never empty.
            if (result.isEmpty()) {
                result.add(new ColorStop(0.0f, 1.0f, 1.0f, 1.0f, 1.0f));
            }
            return result;
        }
    }
}
