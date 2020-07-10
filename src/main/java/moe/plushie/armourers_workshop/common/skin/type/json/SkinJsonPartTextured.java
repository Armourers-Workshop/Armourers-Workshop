package moe.plushie.armourers_workshop.common.skin.type.json;

import java.awt.Point;

import moe.plushie.armourers_workshop.api.common.IPoint3D;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

public class SkinJsonPartTextured extends SkinJsonPart implements ISkinPartTypeTextured {

    public SkinJsonPartTextured(ISkinType baseType) {
        super(baseType);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Point getTextureSkinPos() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isTextureMirrored() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Point getTextureBasePos() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Point getTextureOverlayPos() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IPoint3D getTextureModelSize() {
        // TODO Auto-generated method stub
        return null;
    }

}
