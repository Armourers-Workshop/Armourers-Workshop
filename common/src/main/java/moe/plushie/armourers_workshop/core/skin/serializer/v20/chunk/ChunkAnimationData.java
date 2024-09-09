package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationValue;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ChunkAnimationData {

    public static List<SkinAnimation> readFromStream(IInputStream stream) throws IOException {
        var results = new ArrayList<SkinAnimation>();
        var count = stream.readVarInt();
        for (int i = 0; i < count; i++) {
            var section = readSectionFromStream(stream);
            results.add(section.animation);
        }
        return results;
    }

    public static void writeToStream(List<SkinAnimation> animations, IOutputStream stream) throws IOException {
        stream.writeVarInt(animations.size());
        for (var animation : animations) {
            var section = new Section(animation);
            writeSectionToStream(section, stream);
        }
    }

    private static Section readSectionFromStream(IInputStream stream) throws IOException {
        var id = stream.readString();
        var duration = stream.readFloat();
        var loop = stream.readEnum(SkinAnimationLoop.class);
        var section = new Section(id, duration, loop);
        section.readFromStream(stream);
        return section;
    }

    private static void writeSectionToStream(Section section, IOutputStream stream) throws IOException {
        stream.writeString(section.id);
        stream.writeFloat(section.duration);
        stream.writeEnum(section.loop);
        section.writeToStream(stream);
    }

    public static class Section {

        private SkinAnimation animation;

        private final String id;
        private final float duration;
        private final SkinAnimationLoop loop;

        public Section(String id, float duration, SkinAnimationLoop loop) {
            this.id = id;
            this.loop = loop;
            this.duration = duration;
        }

        public Section(SkinAnimation animation) {
            this.id = animation.getName();
            this.loop = animation.getLoop();
            this.duration = animation.getDuration();
            this.animation = animation;
        }

        void readFromStream(IInputStream stream) throws IOException {
            var values = new LinkedHashMap<String, List<SkinAnimationValue>>();
            while (true) {
                int count = stream.readVarInt();
                if (count == 0) {
                    break;
                }
                var bone = stream.readString();
                var channel = stream.readString();
                for (int i = 0; i < count; i++) {
                    var value = SkinAnimationValue.readFromStream(channel, stream);
                    values.computeIfAbsent(bone, k -> new ArrayList<>()).add(value);
                }
            }
            animation = new SkinAnimation(id, duration, loop, values);
        }

        void writeToStream(IOutputStream stream) throws IOException {
            // merge similar channels.
            var channels = new LinkedHashMap<Pair<String, String>, ArrayList<SkinAnimationValue>>();
            animation.getValues().forEach((bone, values) -> values.forEach(value -> {
                var key = Pair.of(bone, value.getKey());
                channels.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }));
            // write all value of channel.
            for (var entry : channels.entrySet()) {
                var bone = entry.getKey().getKey();
                var channel = entry.getKey().getValue();
                var values = entry.getValue();
                stream.writeVarInt(values.size());
                stream.writeString(bone);
                stream.writeString(channel);
                for (var value : values) {
                    value.writeToStream(channel, stream);
                }
            }
            stream.writeVarInt(0);
        }

        public boolean isEmpty() {
            return animation != null && animation.getValues().isEmpty();
        }
    }
}
