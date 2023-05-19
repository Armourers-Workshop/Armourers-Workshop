package extensions.net.minecraft.core.Registry;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraftforge.fml.event.IModBusEvent;

@Available("[1.18, )")
@Extension
public class ModEventBusProvider {

    public static boolean isModEvent(@ThisClass Class<?> clazz, Class<?> event) {
        return IModBusEvent.class.isAssignableFrom(event);
    }
}
