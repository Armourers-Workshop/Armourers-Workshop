package moe.plushie.armourers_workshop.api.client.key;

import net.minecraft.network.chat.Component;

public interface IKeyBinding {

    Component getKeyName();

    IKeyModifier getKeyModifier();
}
