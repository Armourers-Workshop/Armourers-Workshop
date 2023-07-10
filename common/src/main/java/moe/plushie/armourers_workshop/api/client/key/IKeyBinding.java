package moe.plushie.armourers_workshop.api.client.key;

import net.minecraft.network.chat.Component;

public interface IKeyBinding {

    boolean matches(int key1, int key2);

    Component getKeyName();

    IKeyModifier getKeyModifier();
}
