package moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This component allows for triggering events based on various lifetime events.
 */
public class ParticleEventLifetime implements SkinParticleComponent {

    /// fires when the particle is created
    private final List<String> creation;

    /// fires when the particle expires (does not wait for particles to expire too)
    private final List<String> expiration;

    /// a series of times, e.g. 0.0 or 1.0, that trigger the event
    /// "time" is the time, e.g. one line might be: {"0.4":"event"}
    private final Map<Float, List<String>> timelineEvents;

    public ParticleEventLifetime(List<String> creation, List<String> expiration, Map<Float, List<String>> timelineEvents) {
        this.creation = creation;
        this.expiration = expiration;
        this.timelineEvents = timelineEvents;
    }

    public ParticleEventLifetime(IInputStream stream) throws IOException {
        this.creation = readEventsFromStream(stream);
        this.expiration = readEventsFromStream(stream);
        this.timelineEvents = readKeyedEventsFromStream(stream);
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        writeEventsToStream(creation, stream);
        writeEventsToStream(expiration, stream);
        writeKeyedEventsToStream(timelineEvents, stream);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        // TODO: NO IMPL @SAGESSE
    }

    private List<String> readEventsFromStream(IInputStream stream) throws IOException {
        var events = new ArrayList<String>();
        int size = stream.readVarInt();
        for (int i = 0; i < size; i++) {
            events.add(stream.readString());
        }
        return events;
    }

    private void writeEventsToStream(List<String> events, IOutputStream stream) throws IOException {
        stream.writeVarInt(events.size());
        for (var eventId : events) {
            stream.writeString(eventId);
        }
    }

    private Map<Float, List<String>> readKeyedEventsFromStream(IInputStream stream) throws IOException {
        var events = new LinkedHashMap<Float, List<String>>();
        int timelineEventSize = stream.readVarInt();
        while (timelineEventSize != 0) {
            var key = stream.readFloat();
            var value = new ArrayList<String>();
            for (int i = 0; i < timelineEventSize; i++) {
                value.add(stream.readString());
            }
            events.put(key, value);
            timelineEventSize = stream.readVarInt();
        }
        return events;
    }

    private void writeKeyedEventsToStream(Map<Float, List<String>> events, IOutputStream stream) throws IOException {
        for (var entry : events.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue; // ignore when empty.
            }
            stream.writeVarInt(entry.getValue().size());
            stream.writeFloat(entry.getKey());
            for (var eventId : entry.getValue()) {
                stream.writeString(eventId);
            }
        }
        stream.writeVarInt(0);
    }
}
