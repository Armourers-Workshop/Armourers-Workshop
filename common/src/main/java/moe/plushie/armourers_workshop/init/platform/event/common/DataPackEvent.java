package moe.plushie.armourers_workshop.init.platform.event.common;

import moe.plushie.armourers_workshop.core.data.DataPackType;

public interface DataPackEvent {

    DataPackType getType();

    interface Reloading extends DataPackEvent {
    }
}
