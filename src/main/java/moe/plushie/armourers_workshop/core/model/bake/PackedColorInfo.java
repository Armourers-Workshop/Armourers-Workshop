package moe.plushie.armourers_workshop.core.model.bake;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;

public class PackedColorInfo {

    private final int[] channel = new int[SkinPaintTypes.getTotalExtraChannels()];
    private final int[] channelR = new int[SkinPaintTypes.getTotalExtraChannels()];
    private final int[] channelG = new int[SkinPaintTypes.getTotalExtraChannels()];
    private final int[] channelB = new int[SkinPaintTypes.getTotalExtraChannels()];

    public void add(ISkinPaintType paintType, int rgb) {
        if (!paintType.hasAverageColourChannel()) {
            return;
        }
        int idx = paintType.getChannelIndex();
        channelR[idx] += rgb >> 16 & 0xff;
        channelG[idx] += rgb >> 8 & 0xff;
        channelB[idx] += rgb & 0xff;
        channel[idx] += 1;
    }

    public void add(PackedColorInfo colorInfo) {
        for (int idx = 0; idx < colorInfo.channel.length; ++idx) {
            channelR[idx] += colorInfo.channelR[idx];
            channelG[idx] += colorInfo.channelG[idx];
            channelB[idx] += colorInfo.channelB[idx];
            channel[idx] += colorInfo.channel[idx];
        }
    }

    public PackedColorInfo optimize() {
        for (int idx = 0; idx < channel.length; ++idx) {
            int count = channel[idx];
            if (count == 0) {
                continue;
            }
            channelR[idx] /= count;
            channelG[idx] /= count;
            channelB[idx] /= count;
            channel[idx] = 1;
        }
        return this;
    }

    public int getColor(ISkinPaintType paintType) {
        if (!paintType.hasAverageColourChannel()) {
            return 0;
        }
        int idx = paintType.getChannelIndex();
        return channelR[idx] << 16 | channelG[idx] << 8 | channelB[idx];
    }
}
