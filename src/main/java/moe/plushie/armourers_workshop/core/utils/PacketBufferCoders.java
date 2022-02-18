package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;

public class PacketBufferCoders {

    public static PacketBufferCoder<Skin> SKIN = new PacketBufferCoder<>(Skin.class, SkinSerializer::readSkinByBuffer, SkinSerializer::writeSkinToBuffer);

    public static PacketBufferCoder<SkinDescriptor> SKIN_DESCRIPTOR = new PacketBufferCoder<>(SkinDescriptor.class, SkinSerializer::readSkinDescriptorByBuffer, SkinSerializer::writeSkinDescriptorToBuffer);

}
