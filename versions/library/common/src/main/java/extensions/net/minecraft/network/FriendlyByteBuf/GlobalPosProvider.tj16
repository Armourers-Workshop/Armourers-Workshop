package extensions.net.minecraft.network.FriendlyByteBuf;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.16, 1.19)")
public class GlobalPosProvider {

    public static void writeGlobalPos(@This FriendlyByteBuf buf, GlobalPos globalPos) {
        try {
            buf.writeWithCodec(GlobalPos.CODEC, globalPos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GlobalPos readGlobalPos(@This FriendlyByteBuf buf) {
        try {
            return buf.readWithCodec(GlobalPos.CODEC);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
