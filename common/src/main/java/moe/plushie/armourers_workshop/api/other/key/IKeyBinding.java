package moe.plushie.armourers_workshop.api.other.key;

import net.minecraft.network.chat.Component;

public interface IKeyBinding {

    boolean consumeClick();

    Component getKeyName();

    IKeyModifier getKeyModifier();
}
