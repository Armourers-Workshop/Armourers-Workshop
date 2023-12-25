package moe.plushie.armourers_workshop.core.skin.cube.impl;

import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.utils.math.OpenPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.texture.TextureBox;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SkinCubesV2 extends SkinCubes {

    private final ArrayList<Box> entities = new ArrayList<>();
    private final OpenPoseStack poseStack = new OpenPoseStack();

    public void addBox(Box box) {
        entities.add(box);
    }

    @Override
    public OpenVoxelShape getShape() {
        OpenVoxelShape shape = OpenVoxelShape.empty();
        for (Box box : entities) {
            if (box.transform.isIdentity()) {
                shape.add(box.shape);
                continue;
            }
            poseStack.pushPose();
            OpenVoxelShape shape1 = OpenVoxelShape.box(box.shape);
            box.transform.apply(poseStack);
            shape1.mul(poseStack.lastPose());
            shape.add(shape1);
            poseStack.popPose();
        }
        shape.optimize();
        return shape;
    }


    @Override
    public int getCubeTotal() {
        return entities.size();
    }

    @Override
    public SkinCube getCube(int index) {
        return entities.get(index);
    }

    @Override
    public Collection<ISkinCubeType> getCubeTypes() {
        return Collections.singleton(SkinCubeTypes.TEXTURE);
    }

    public static class Box extends SkinCube {

        private final Rectangle3f shape;
        private final SkinTransform transform;
        private final TextureBox skyBox;

        public Box(Rectangle3f shape, SkinTransform transform, TextureBox skyBox) {
            this.shape = shape;
            this.transform = transform;
            this.skyBox = skyBox;
        }

        @Override
        public Rectangle3f getShape() {
            return shape;
        }

        @Override
        public SkinTransform getTransform() {
            return transform;
        }

        @Override
        public ISkinCubeType getType() {
            return SkinCubeTypes.TEXTURE;
        }

        @Override
        public IPaintColor getPaintColor(Direction dir) {
            return PaintColor.WHITE;
        }

        @Override
        public ITextureKey getTexture(Direction dir) {
            return skyBox.getTexture(dir);
        }

        @Override
        public SkinCubeFace getFace(Direction dir) {
            if (getTexture(dir) != null) {
                return super.getFace(dir);
            }
            return null;
        }
    }
}
