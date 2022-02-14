package moe.plushie.armourers_workshop.core.bake;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ColorDescriptor {

    private final HashMap<ISkinPaintType, Channel> channels = new HashMap<>();

    public void add(PaintColor color) {
        ISkinPaintType paintType = color.getPaintType();
        if (paintType.getDyeType() == null && paintType != SkinPaintTypes.RAINBOW) {
            return;
        }
        Channel channel = channels.computeIfAbsent(paintType, k -> new Channel());
        channel.r += color.getRed();
        channel.g += color.getGreen();
        channel.b += color.getBlue();
        channel.total += 1;
    }

    public void add(ColorDescriptor colorInfo) {
        for (Map.Entry<ISkinPaintType, Channel> entry : colorInfo.channels.entrySet()) {
            Channel otherChannel = entry.getValue();
            Channel channel = channels.computeIfAbsent(entry.getKey(), k -> new Channel());
            channel.r += otherChannel.r;
            channel.g += otherChannel.g;
            channel.b += otherChannel.b;
            channel.total += otherChannel.total;
        }
    }

    public boolean isEmpty() {
        return channels.isEmpty();
    }


    public Integer getAverageColor(ISkinPaintType paintType) {
        if (paintType.getDyeType() == null) {
            return null;
        }
        Channel channel = channels.get(paintType);
        if (channel == null) {
            return null;
        }
        if (channel.total > 1) {
            channel.r /= channel.total;
            channel.g /= channel.total;
            channel.b /= channel.total;
            channel.total = 1;
        }
        return 0xFF000000 | channel.r << 16 | channel.g << 8 | channel.b;
    }

    public Set<ISkinPaintType> getPaintTypes() {
        return channels.keySet();
    }

    private static class Channel {
        int total = 0;
        int r = 0;
        int g = 0;
        int b = 0;
    }
}
