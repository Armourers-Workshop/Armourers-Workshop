package moe.plushie.armourers_workshop.core.bake;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PackedColorInfo {

    private final HashMap<ISkinPaintType, Channel> channels = new HashMap<>();

    public void add(ISkinPaintType paintType, int rgb) {
        if (paintType.getDyeType() == null && paintType != SkinPaintTypes.TEXTURE) {
            return;
        }
        Channel channel = channels.computeIfAbsent(paintType, k -> new Channel());
        channel.r += rgb >> 16 & 0xff;
        channel.g += rgb >> 8 & 0xff;
        channel.b += rgb & 0xff;
        channel.total += 1;
    }

    public void add(PackedColorInfo colorInfo) {
        for (Map.Entry<ISkinPaintType, Channel> entry : colorInfo.channels.entrySet()) {
            Channel otherChannel = entry.getValue();
            Channel channel = channels.computeIfAbsent(entry.getKey(), k -> new Channel());
            channel.r += otherChannel.r;
            channel.g += otherChannel.g;
            channel.b += otherChannel.b;
            channel.total += otherChannel.total;
        }
    }


    public Integer getAverageDyeColor(ISkinPaintType paintType) {
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
