package moe.plushie.armourers_workshop.core.item.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;

public interface IPaintProvider {

    IPaintColor getColor();

    void setColor(IPaintColor color);
}
