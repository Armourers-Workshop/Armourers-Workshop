package moe.plushie.armourers_workshop.utils.ext;

import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

public class OpenModelPartBuilder {

    private static final Set<Direction> ALL_VISIBLE = EnumSet.allOf(Direction.class);

    protected int texU = 0;
    protected int texV = 0;
    protected final int texWidth;
    protected final int texHeight;

    protected boolean mirror = false;

    protected OpenModelPart.Pose offset = OpenModelPart.Pose.ZERO;
    protected final ArrayList<OpenModelPart.Cube> cubes = new ArrayList<>();

    protected OpenModelPartBuilder(int width, int texHeight) {
        this.texWidth = width;
        this.texHeight = texHeight;
    }

    public static OpenModelPartBuilder of(int width, int height) {
        return new OpenModelPartBuilder(width, height);
    }

    public static OpenModelPartBuilder player() {
        return of(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT);
    }

    public OpenModelPartBuilder uv(int u, int v) {
        this.texU = u;
        this.texV = v;
        return this;
    }

    public OpenModelPartBuilder offset(float x, float y, float z) {
        this.offset = OpenModelPart.Pose.offset(x, y, z);
        return this;
    }

    public OpenModelPartBuilder cube(float x, float y, float z, float width, float height, float depth) {
        return cube(x, y, z, width, height, depth, 0);
    }

    public OpenModelPartBuilder cube(float x, float y, float z, float width, float height, float depth, float scale) {
        cubes.add(new OpenModelPart.Cube(texU, texV, x, y, z, width, height, depth, scale, scale, scale, mirror, texWidth, texHeight, ALL_VISIBLE));
        return this;
    }

    public OpenModelPartBuilder mirror() {
        this.mirror = true;
        return this;
    }

    public OpenModelPart build() {
        var modelPart = new OpenModelPart(cubes, new HashMap<>());
        modelPart.setInitialPose(offset);
        modelPart.loadPose(offset);
        return modelPart;
    }
}
