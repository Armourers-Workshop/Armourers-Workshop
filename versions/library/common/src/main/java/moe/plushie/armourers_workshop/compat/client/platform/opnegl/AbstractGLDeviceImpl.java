package moe.plushie.armourers_workshop.compat.client.platform.opnegl;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.utils.RenderSystem;

@Available("[18, 26)")
@OnlyIn(Dist.CLIENT)
public class AbstractGLDeviceImpl extends AbstractGLDevice {

    @Override
    public void beginTransaction() {
        RenderSystem.resetTextureMatrix();
        super.beginTransaction();
    }

    @Override
    public void endTransaction() {
        super.endTransaction();
    }
}
