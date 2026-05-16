package moe.plushie.armourers_workshop.compat.client.platform;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.platform.opnegl.AbstractGLDeviceImpl;

@OnlyIn(Dist.CLIENT)
public interface AbstractRenderDeviceImpl {

    AbstractGLDeviceImpl CURRENT = new AbstractGLDeviceImpl();
}
