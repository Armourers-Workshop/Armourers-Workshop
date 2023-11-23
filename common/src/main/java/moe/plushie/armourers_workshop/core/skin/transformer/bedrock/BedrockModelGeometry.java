package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.util.ArrayList;
import java.util.Collection;

public class BedrockModelGeometry {

    private final String identifier;
    private final float textureWidth;
    private final float textureHeight;
    private final float visibleWidth;
    private final float visibleHeight;
    private final Vector3f visibleOffset;

    private final Collection<BedrockModelBone> bones;

    public BedrockModelGeometry(String identifier, float textureWidth, float textureHeight, float visibleWidth, float visibleHeight, Vector3f visibleOffset, Collection<BedrockModelBone> bones) {
        this.identifier = identifier;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.visibleWidth = visibleWidth;
        this.visibleHeight = visibleHeight;
        this.visibleOffset = visibleOffset;
        this.bones = bones;
    }

    public String getIdentifier() {
        return identifier;
    }

    public float getTextureWidth() {
        return textureWidth;
    }

    public float getTextureHeight() {
        return textureHeight;
    }

    public Collection<BedrockModelBone> getBones() {
        return bones;
    }

    public static class Builder {

        private String identifier;
        private float textureWidth;
        private float textureHeight;
        private float visibleWidth;
        private float visibleHeight;
        private Vector3f visibleOffset = Vector3f.ZERO;
        private final ArrayList<BedrockModelBone> bones = new ArrayList<>();

        public void identifier(String identifier) {
            this.identifier = identifier;
        }

        public void textureWidth(float textureWidth) {
            this.textureWidth = textureWidth;
        }

        public void textureHeight(float textureHeight) {
            this.textureHeight = textureHeight;
        }

        public void visibleWidth(float visibleWidth) {
            this.visibleWidth = visibleWidth;
        }

        public void visibleHeight(float visibleHeight) {
            this.visibleHeight = visibleHeight;
        }

        public void visibleOffset(Vector3f visibleOffset) {
            this.visibleOffset = visibleOffset;
        }

        public void addBone(BedrockModelBone bone) {
            this.bones.add(bone);
        }

        public void addBones(Collection<BedrockModelBone> bones) {
            this.bones.addAll(bones);
        }

        public BedrockModelGeometry build() {
            return new BedrockModelGeometry(identifier, textureWidth, textureHeight, visibleWidth, visibleHeight, visibleOffset, bones);
        }
    }
}
