package moe.plushie.armourers_workshop.api.key;

import net.minecraft.network.chat.Component;

public interface IKeyBinding {

    boolean consumeClick();

    Component getKeyName();

    IKeyModifier getKeyModifier();
}
