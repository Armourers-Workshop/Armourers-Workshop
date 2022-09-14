package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.compatibility.AbstractModelPartBuilderImpl;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public abstract class ModelPartBuilder {

    protected int texU = 0;
    protected int texV = 0;
    protected final int texWidth;
    protected final int texHeight;

    protected Vector3f offset = Vector3f.ZERO;
    protected final ArrayList<Cube> cubes = new ArrayList<>();

    protected ModelPartBuilder(int width, int texHeight) {
        this.texWidth = width;
        this.texHeight = texHeight;
    }

    public static ModelPartBuilder of(int width, int height) {
        return new AbstractModelPartBuilderImpl(width, height);
    }

    public static ModelPartBuilder player() {
        return of(PlayerTexture.TEXTURE_WIDTH, PlayerTexture.TEXTURE_HEIGHT);
    }

    public ModelPartBuilder uv(int u, int v) {
        this.texU = u;
        this.texV = v;
        return this;
    }

    public ModelPartBuilder offset(float x, float y, float z) {
        this.offset = new Vector3f(x, y, z);
        return this;
    }

    public ModelPartBuilder cube(float x, float y, float z, float width, float height, float depth) {
        return this.cube(x, y, z, width, height, depth, 0);
    }

    public ModelPartBuilder cube(float x, float y, float z, float width, float height, float depth, float scale) {
        Rectangle3f rect = new Rectangle3f(x, y, z, width, height, depth);
        this.cubes.add(new Cube("cube" + cubes.size(), rect, scale, texU, texV));
        return this;
    }

    public ModelPartBuilder mirror() {
        return this;
    }

    public abstract ModelPart build();

    public static class Cube {

        public final String name;
        public final int texU;
        public final int texV;
        public final float scale;
        public final Rectangle3f rect;

        Cube(String name, Rectangle3f rect, float scale, int texU, int texV) {
            this.name = name;
            this.rect = rect;
            this.scale = scale;
            this.texU = texU;
            this.texV = texV;
        }
    }
}
