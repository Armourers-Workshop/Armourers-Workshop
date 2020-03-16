package moe.plushie.armourers_workshop.common.skin.advanced;

import java.util.ArrayList;

import net.minecraft.util.math.Vec3d;

public class AdvancedPart {

    private final ArrayList<AdvancedPart> children = new ArrayList<AdvancedPart>();
    private final int partIndex;
    private String name;
    private boolean isStatic = true;
    private boolean enabled = true;
    private float scale = 1F;
    private boolean mirror = false;

    private Vec3d pos = Vec3d.ZERO;
    private Vec3d posOffset = Vec3d.ZERO;

    private Vec3d rotationAngle = Vec3d.ZERO;
    private Vec3d rotationAngleOffset = Vec3d.ZERO;

    private Vec3d rotationPos = Vec3d.ZERO;
    private Vec3d rotationPosOffset = Vec3d.ZERO;

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
