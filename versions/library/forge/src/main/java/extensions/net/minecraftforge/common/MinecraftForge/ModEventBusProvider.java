package extensions.net.minecraftforge.common.MinecraftForge;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraftforge.fml.event.IModBusEvent;

@Available("[1.18, )")
@Extension
public class ModEventBusProvider {

    public static Class<?> getModEventBusClass(@ThisClass Class<?> clazz) {
        return IModBusEvent.class;
    }
}
