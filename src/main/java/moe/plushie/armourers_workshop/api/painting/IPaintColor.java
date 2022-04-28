package moe.plushie.armourers_workshop.api.painting;

import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;

public interface IPaintColor {

    int getRGB();

    ISkinPaintType getPaintType();

    int getRawValue();
}
