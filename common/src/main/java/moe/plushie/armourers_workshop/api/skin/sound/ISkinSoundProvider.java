package moe.plushie.armourers_workshop.api.skin.sound;

import io.netty.buffer.ByteBuf;

public interface ISkinSoundProvider {

    String name();

    ByteBuf buffer();

    ISkinSoundProperties properties();
}
