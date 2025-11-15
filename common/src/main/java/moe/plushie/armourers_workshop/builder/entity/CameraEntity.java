package moe.plushie.armourers_workshop.builder.entity;

import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.core.math.OpenNearPlane;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

public class CameraEntity extends SeatEntity {

    private float zoom = 0;

    private CameraType cameraType;
    private final Minecraft minecraft = Minecraft.getInstance();

    public CameraEntity() {
        super(ModEntityTypes.SEAT.get().get(), Minecraft.getInstance().level);
    }

    public void connect() {
        cameraType = minecraft.options.getCameraType();
        minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        minecraft.setCameraEntity(this);
    }

    public void disconnect() {
        minecraft.setCameraEntity(null);
        minecraft.options.setCameraType(cameraType);
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public float getZoom() {
        return zoom;
    }

    public float getMaxZoom(float targetZoom) {
        return targetZoom * zoom;
    }

    public OpenNearPlane getNearPlane() {
        float rx = abi$getXRot();
        float ry = abi$getYRot();
        float width = minecraft.getWindow().getWidth();
        float height = minecraft.getWindow().getHeight();
        float fov = minecraft.options.getCameraFOV();
        return new OpenNearPlane(rx, ry, width, height, fov);
    }

    @Override
    protected float abi$getViewXRot(float f) {
        return abi$getXRot();
    }

    @Override
    protected float abi$getViewYRot(float f) {
        return abi$getYRot();
    }
}
