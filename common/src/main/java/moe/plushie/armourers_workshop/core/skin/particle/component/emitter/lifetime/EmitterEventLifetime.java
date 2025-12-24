package moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows for lifetime events on the emitter to trigger certain events.
 */
public class EmitterEventLifetime implements SkinParticleComponent {

    /// all events use the event names in the event section
    /// all events can be an array or a string

    /// Fires when the emitter is created
    private final List<String> creation;
    /// Fires when the emitter expires (does not wait for particles to expire too)
    private final List<String> expiration;

    /// a series of times, e.g. 0.0 or 1.0, that trigger the event
    /// these get fired on every loop the emitter goes through
    /// "time" is the time, e.g. one line might be: {"0.4": ["event"]}
    private final Map<Float, List<String>> timelineEvents;

    /// a series of distances, e.g. 0.0 or 1.0, that trigger the event
    /// these get fired when the emitter has moved by the specified input
    /// distance, e.g. one line might be: {"0.4": ["event"]}
    private final Map<Float, List<String>> travelDistanceEvents;

    /// a series of events that occur at set intervals
    /// these get fired every time the emitter has moved the specified input
    /// distance from the last time it was fired.
    /// An example for how to format these events would be: {"distance":1.0,"effects":["effect_one"]}
    private final Map<Float, List<String>> travelDistanceLoopEvents;

    public EmitterEventLifetime(List<String> creation, List<String> expiration, Map<Float, List<String>> timelineEvents, Map<Float, List<String>> travelDistanceEvents, Map<Float, List<String>> travelDistanceLoopEvents) {
        this.creation = creation;
        this.expiration = expiration;
        this.timelineEvents = timelineEvents;
        this.travelDistanceEvents = travelDistanceEvents;
        this.travelDistanceLoopEvents = travelDistanceLoopEvents;
    }

    public EmitterEventLifetime(IInputStream stream) throws IOException {
        this.creation = readEventsFromStream(stream);
        this.expiration = readEventsFromStream(stream);
        this.timelineEvents = readKeyedEventsFromStream(stream);
        this.travelDistanceEvents = readKeyedEventsFromStream(stream);
        this.travelDistanceLoopEvents = readKeyedEventsFromStream(stream);
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        writeEventsToStream(creation, stream);
        writeEventsToStream(expiration, stream);
        writeKeyedEventsToStream(timelineEvents, stream);
        writeKeyedEventsToStream(travelDistanceEvents, stream);
        writeKeyedEventsToStream(travelDistanceLoopEvents, stream);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        // TODO: NO IMPL - @SAGESSE
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
