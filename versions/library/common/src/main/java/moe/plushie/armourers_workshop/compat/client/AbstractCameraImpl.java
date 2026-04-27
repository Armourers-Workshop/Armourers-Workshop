package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;

@Available("[16, 26)")
public abstract class AbstractCameraImpl {

    protected float getXRot() {
        return getCamera().getXRot();
    }

    protected float getYRot() {
        return getCamera().getYRot();
    }

    protected Vec3 getPosition() {
        return getCamera().getPosition();
    }

    protected abstract Camera getCamera();
}
