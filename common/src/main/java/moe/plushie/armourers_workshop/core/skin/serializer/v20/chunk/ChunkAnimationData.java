package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationFunction;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationKeyframe;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationPoint;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundData;
import moe.plushie.armourers_workshop.core.skin.sound.SkinSoundProperties;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChunkAnimationData {

    private final List<SkinAnimation> animations;

    public ChunkAnimationData(List<SkinAnimation> animations) {
        this.animations = animations;
    }

    public List<SkinAnimation> animations() {
        return animations;
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
        // TODO: remove in the future (22-refactor-file).
        if (stream.fileVersion() < 22) {
            animations.addAll(LegacyHelperV20.readFromStream(stream));
            return;
        }
        var animationCount = stream.readVarInt();
        for (int i = 0; i < animationCount; ++i) {
            var name = stream.readString();
            var duration = stream.readFloat();
            var loop = stream.readEnum(SkinAnimationLoop.class);
            var keyframes = readKeyframesFromStream(stream);
            animations.add(new SkinAnimation(name, duration, loop, keyframes));
        }
    }

    public void writeToStream(ChunkOutputStream stream) throws IOException {
        stream.writeVarInt(animations.size());
        for (var animation : animations) {
            stream.writeString(animation.name());
            stream.writeFloat(animation.duration());
            stream.writeEnum(animation.loop());
            writeKeyframesToStream(animation.keyframes(), stream);
        }
    }

    private Map<String, List<SkinAnimationKeyframe>> readKeyframesFromStream(ChunkInputStream stream) throws IOException {
        var keyframes = new LinkedHashMap<String, List<SkinAnimationKeyframe>>();
        while (true) {
            var channel = stream.readString();
            if (channel.isEmpty()) {
                break;
            }
            while (true) {
                var bone = stream.readString();
                if (bone.isEmpty()) {
                    break;
                }
                while (true) {
                    var keyframe = readKeyframeFromStream(channel, stream);
                    if (keyframe == null) {
                        break;
                    }
                    keyframes.computeIfAbsent(bone, it -> new ArrayList<>()).add(keyframe);
                }
            }
        }
        return keyframes;
    }

    private void writeKeyframesToStream(Map<String, List<SkinAnimationKeyframe>> keyframes, ChunkOutputStream stream) throws IOException {
        // ..
        var sortedKeyframes = new LinkedHashMap<String, Map<String, List<SkinAnimationKeyframe>>>();
        for (var entry1 : keyframes.entrySet()) {
            var bone = entry1.getKey();
            for (var entry2 : entry1.getValue()) {
                var channel = entry2.key();
                sortedKeyframes.computeIfAbsent(channel, it -> new LinkedHashMap<>()).computeIfAbsent(bone, it -> new ArrayList<>()).add(entry2);
            }
        }
        // ..
        for (var entry1 : sortedKeyframes.entrySet()) {
            var channel = entry1.getKey();
            stream.writeString(channel);
            for (var entry2 : entry1.getValue().entrySet()) {
                var bone = entry2.getKey();
                stream.writeString(bone);
                for (var keyframe : entry2.getValue()) {
                    writeKeyframeToStream(keyframe, channel, stream);
                }
                writeKeyframeToStream(null, null, stream); // empty keyframe
            }
            stream.writeString(""); // empty bone.
        }
        stream.writeString(""); // empty channel.
    }

    private SkinAnimationKeyframe readKeyframeFromStream(String channel, ChunkInputStream stream) throws IOException {
        // 0 is null keyframe.
        var pointCount = stream.readVarInt();
        if (pointCount == 0) {
            return null;
        }
        var time = stream.readFloat();
        var function = SkinAnimationFunction.readFromStream(stream);
        var points = new ArrayList<SkinAnimationPoint>();
        // 1 is empty keyframe.
        for (int i = 1; i < pointCount; ++i) {
            var type = stream.readVarInt();
            var serializer = PointSerializer.byType(type);
            if (serializer == null) {
                throw new IOException("can't read animation point of type: " + type);
            }
            points.add(serializer.readFromStream(stream));
        }
        return new SkinAnimationKeyframe(time, channel, function, points);
    }

    private void writeKeyframeToStream(SkinAnimationKeyframe keyframe, String channel, ChunkOutputStream stream) throws IOException {
        // 0 is null keyframe.
        if (keyframe == null || channel == null) {
            stream.writeVarInt(0);
            return;
        }
        // 1 is empty keyframe.
        stream.writeVarInt(keyframe.points().size() + 1);
        stream.writeFloat(keyframe.time());
        keyframe.function().writeToStream(stream);
        // write all points into stream.
        for (var point : keyframe.points()) {
            var serializer = PointSerializer.byValue(point);
            if (serializer == null) {
                throw new IOException("can't write animation point: " + point);
            }
            stream.writeVarInt(serializer.type);
            serializer.writeToStream(Objects.unsafeCast(point), stream);
        }
    }

    @SuppressWarnings("unused")
    private static abstract class PointSerializer<T extends SkinAnimationPoint> {

        private static final List<PointSerializer<?>> SERIALIZERS = new ArrayList<>();

        private static final PointSerializer<?> BONE = new PointSerializer<>(8, SkinAnimationPoint.Bone.class) {

            @Override
            public SkinAnimationPoint.Bone readFromStream(ChunkInputStream stream) throws IOException {
                var x = readField(stream);
                var y = readField(stream);
                var z = readField(stream);
                return new SkinAnimationPoint.Bone(x, y, z);
            }

            @Override
            public void writeToStream(SkinAnimationPoint.Bone value, ChunkOutputStream stream) throws IOException {
                writeField(value.x(), stream);
                writeField(value.y(), stream);
                writeField(value.z(), stream);
            }

            private static OpenPrimitive readField(ChunkInputStream stream) throws IOException {
                var len = stream.readVarInt();
                if (len == 0) {
                    return OpenPrimitive.of(stream.readFloat());
                }
                return OpenPrimitive.of(stream.readString(len));
            }

            private static void writeField(OpenPrimitive value, ChunkOutputStream stream) throws IOException {
                if (value.isString()) {
                    var script = value.stringValue();
                    stream.writeVarInt(script.length());
                    stream.writeString(script, script.length());
                } else if (value.isNumber()) {
                    stream.writeVarInt(0);
                    stream.writeFloat(value.floatValue());
                } else {
                    throw new IOException("can't write point value: " + value);
                }
            }
        };

        private static final PointSerializer<?> INSTRUCT = new PointSerializer<>(9, SkinAnimationPoint.Instruct.class) {

            @Override
            public SkinAnimationPoint.Instruct readFromStream(ChunkInputStream stream) throws IOException {
                var script = stream.readString();
                return new SkinAnimationPoint.Instruct(script);
            }

            @Override
            public void writeToStream(SkinAnimationPoint.Instruct value, ChunkOutputStream stream) throws IOException {
                stream.writeString(value.script());
            }
        };

        private static final PointSerializer<?> SOUND = new PointSerializer<>(10, SkinAnimationPoint.Sound.class) {

            @Override
            public SkinAnimationPoint.Sound readFromStream(ChunkInputStream stream) throws IOException {
                // TODO: remove in the future (23-builtin-sound).
                if (stream.fileVersion() < 23) {
                    var effect = stream.readString();
                    var file = stream.readFile();
                    var sound = new SkinSoundData(file.name(), file.bytes(), SkinSoundProperties.EMPTY);
                    return new SkinAnimationPoint.Sound(effect, sound);
                }
                var effect = stream.readString();
                var properties = new SkinSoundProperties();
                properties.readFromStream(stream);
                var file = stream.readFile();
                var sound = new SkinSoundData(file.name(), file.bytes(), properties);
                return new SkinAnimationPoint.Sound(effect, sound);
            }

            @Override
            public void writeToStream(SkinAnimationPoint.Sound value, ChunkOutputStream stream) throws IOException {
                var sound = value.provider();
                var properties = sound.properties();
                stream.writeString(value.effect());
                properties.writeToStream(stream);
                stream.writeFile(ChunkFile.audio(sound.name(), sound.buffer()));
            }
        };

        private static final PointSerializer<?> PARTICLE = new PointSerializer<>(11, SkinAnimationPoint.Particle.class) {

            @Override
            public SkinAnimationPoint.Particle readFromStream(ChunkInputStream stream) throws IOException {
                var effect = stream.readString();
                var locator = stream.readOptionalString();
                var script = stream.readOptionalString();
                var particleData = new ChunkParticleData();
                particleData.readFromStream(stream);
                return new SkinAnimationPoint.Particle(effect, locator.orElse(null), script.orElse(null), particleData.particle());
            }

            @Override
            public void writeToStream(SkinAnimationPoint.Particle value, ChunkOutputStream stream) throws IOException {
                var particle = value.provider();
                stream.writeString(value.effect());
                stream.writeOptionalString(value.locator());
                stream.writeOptionalString(value.script());
                var particleData = new ChunkParticleData(particle);
                particleData.writeToStream(stream);
            }
        };

        private final int type;
        private final Class<?> valueClass;

        public PointSerializer(int type, Class<T> valueClass) {
            this.type = type;
            this.valueClass = valueClass;
            SERIALIZERS.add(this);
        }

        public static PointSerializer<?> byType(int type) {
            for (var serializer : SERIALIZERS) {
                if (serializer.type == type) {
                    return serializer;
                }
            }
            return null;
        }

        public static PointSerializer<?> byValue(SkinAnimationPoint value) {
            for (var serializer : SERIALIZERS) {
                if (serializer.valueClass.isInstance(value)) {
                    return serializer;
                }
            }
            return null;
        }

        public abstract T readFromStream(ChunkInputStream stream) throws IOException;

        public abstract void writeToStream(T value, ChunkOutputStream stream) throws IOException;
    }


    private static class LegacyHelperV20 {

        private static List<SkinAnimation> readFromStream(ChunkInputStream stream) throws IOException {
            var results = new ArrayList<SkinAnimation>();
            var count = stream.readVarInt();
            for (int i = 0; i < count; i++) {
                var animation = readAnimationFromStream(stream);
                results.add(animation);
            }
            return results;
        }

        private static SkinAnimation readAnimationFromStream(ChunkInputStream stream) throws IOException {
            var id = stream.readString();
            var duration = stream.readFloat();
            var loop = stream.readEnum(SkinAnimationLoop.class);
            var keyframes = new LinkedHashMap<String, List<SkinAnimationKeyframe>>();
            while (true) {
                int count = stream.readVarInt();
                if (count == 0) {
                    break;
                }
                var bone = stream.readString();
                var channel = stream.readString();
                for (int i = 0; i < count; i++) {
                    var keyframe = readKeyframeFromStream(channel, stream);
                    keyframes.computeIfAbsent(bone, k -> new ArrayList<>()).add(keyframe);
                }
            }
            return new SkinAnimation(id, duration, loop, keyframes);
        }

        private static SkinAnimationKeyframe readKeyframeFromStream(String key, ChunkInputStream stream) throws IOException {
            var time = stream.readFloat();
            var function = SkinAnimationFunction.readFromStream(stream);
            var points = new ArrayList<SkinAnimationPoint>();
            int type = stream.readVarInt();
            // old version is: 0,3,6
            if (type == 3 || type == 6) {
                points.addAll(readKeyframePointsFromStream(type, stream));
                type = 0;
            }
            while (type != 0) {
                var serializer = PointSerializer.byType(type);
                if (serializer == null) {
                    throw new IOException("can't read animation point of type: " + type);
                }
                points.add(serializer.readFromStream(stream));
                type = stream.readVarInt();
            }
            return new SkinAnimationKeyframe(time, key, function, points);
        }

        private static List<SkinAnimationPoint> readKeyframePointsFromStream(int length, ChunkInputStream stream) throws IOException {
            var points = new ArrayList<SkinAnimationPoint>();
            var objects = new ArrayList<OpenPrimitive>();
            for (int i = 0; i < length; i++) {
                var flags = stream.readVarInt();
                if ((flags & 0x40) != 0) {
                    objects.add(OpenPrimitive.of(stream.readString()));
                } else {
                    objects.add(OpenPrimitive.of(stream.readFloat()));
                }
            }
            for (int i = 0; i < objects.size(); i += 3) {
                var x = objects.get(i);
                var y = objects.get(i + 1);
                var z = objects.get(i + 2);
                points.add(new SkinAnimationPoint.Bone(x, y, z));
            }
            return points;
        }
    }
}
