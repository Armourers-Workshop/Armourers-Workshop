package moe.plushie.armourers_workshop.common.skin.advanced;

import java.util.ArrayList;

import net.minecraft.util.math.Vec3d;

public class AdvancedPart {

    private final ArrayList<AdvancedPart> children = new ArrayList<AdvancedPart>();
    public final int partIndex;
    public String name;
    public boolean isStatic = true;
    public boolean enabled = true;
    public float scale = 1F;
    public boolean mirror = false;

    public Vec3d pos = Vec3d.ZERO;
    public Vec3d posOffset = Vec3d.ZERO;

    public Vec3d rotationAngle = Vec3d.ZERO;
    public Vec3d rotationAngleOffset = Vec3d.ZERO;

    public Vec3d rotationPos = Vec3d.ZERO;
    public Vec3d rotationPosOffset = Vec3d.ZERO;

    public AdvancedPart(int partIndex, String name) {
        this.partIndex = partIndex;
        this.name = name;
    }

    public ArrayList<AdvancedPart> getChildren() {
        return children;
    }

    public void setRotationAngleOffset(double x, double y, double z) {
        this.rotationAngleOffset = new Vec3d(x, y, z);
    }
}
