package moe.plushie.armourers_workshop.api.common.skin.type;

import java.awt.Point;

import moe.plushie.armourers_workshop.api.common.IPoint3D;

public interface ISkinPartTypeTextured extends ISkinPartType {

    public Point getTextureLocation();
    
    public boolean isTextureMirrored();
    
    public IPoint3D getTextureModelSize();
}
