package moe.plushie.armourers_workshop.core.api.common.skin;

import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;

import java.util.Collection;

public interface ISkinPartType {

    /**
     * Gets the name this skin will be registered with. Armourer's Workshop uses the
     * format baseType.getRegistryName() + "." + getPartName(). Example
     * armourers:chest.leftArm is the registry name of Armourer's Workshop chest
     * left arm skin part.
     *
     * @return Registry name
     */
    String getRegistryName();


    /**
     * The last 3 values are used to define the size of this part, the first 3
     * values will change the origin. Example -5, -5, -5, 10, 10, 10, Will create a
     * 10x10x10 cube with it's origin in the centre.
     *
     * @return
     */
    Rectangle3D getBuildingSpace();

    /**
     * The last 3 values set the size of the invisible blocks that cubes can be
     * placed on, the first 3 set the offset. Use 0, 0, 0, 0, 0, 0, if you don't
     * want to use this. Setting showArmourerDebugRender to true in the config will
     * show this box.
     *
     * @return
     */
    Rectangle3D getGuideSpace();

    /**
     * This is used by the armourer to position this part
     */
    Point3D getOffset();

    Point3D getRenderOffset();


    /**
     * @param scale           Normally 0.0625F.
     * @param showSkinOverlay
     * @param showHelper
     */
    void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper);

    /**
     * Get the minimum number of markers needed for this skin part.
     *
     * @return
     */
    int getMinimumMarkersNeeded();

    /**
     * Gets the maximum number of markers allowed for this skin part.
     *
     * @return
     */
    int getMaximumMarkersNeeded();

    /**
     * If true this part must be present for the skin to be saved.
     *
     * @return
     */
    boolean isPartRequired();


    Collection<ISkinProperty<?>> getProperties();

    Rectangle3D getItemRenderTextureBounds();

    boolean isModelOverridden(ISkinProperties skinProps);

    boolean isOverlayOverridden(ISkinProperties skinProps);

    ISkinPart makeDummyPaintPart(int[] paintData);
}
