package moe.plushie.armourers_workshop.core.render.bake;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.face.SkinCuller;
import moe.plushie.armourers_workshop.utils.SkyBox;
import moe.plushie.armourers_workshop.utils.color.ColorDescriptor;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import moe.plushie.armourers_workshop.core.model.PlayerTextureModel;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.extened.AWVoxelShape;
import moe.plushie.armourers_workshop.utils.Rectangle3f;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class PackedQuad {

    private final HashMap<SkinRenderType, ArrayList<SkinCubeFace>> allFaces = new HashMap<>();

    private final Rectangle3i bounds;
    private final AWVoxelShape renderShape;
    private final ColorDescriptor colorInfo = new ColorDescriptor();

    private int faceTotal = 0;

    public PackedQuad(Rectangle3i bounds, AWVoxelShape renderShape, ArrayList<SkinCubeFace> faces) {
        this.bounds = bounds;
        this.renderShape = renderShape;
        this.loadFaces(faces);
    }

    public static PackedQuad from(SkinCubeData data) {
        AWVoxelShape renderShape = data.getRenderShape();
        Rectangle3i bounds = new Rectangle3i(renderShape.bounds());
        return new PackedQuad(bounds, renderShape, SkinCuller.cullFaces(data, bounds));
    }

    public static HashMap<ISkinPartType, PackedQuad> from(int width, int height, int[] paintData) {
        HashMap<ISkinPartType, PackedQuad> allQuads = new HashMap<>();
        if (paintData == null || paintData.length == 0) {
            return allQuads;
        }
        for (Map.Entry<ISkinPartType, SkyBox> entry : PlayerTextureModel.of(width, height, false).entrySet()) {
            SkyBox box = entry.getValue();
            ArrayList<SkinCubeFace> quads = new ArrayList<>();
            box.forEach((texture, x, y, z, dir) -> {
                PaintColor paintColor = PaintColor.of(paintData[texture.y * width + texture.x]);
                if (paintColor.getPaintType() == SkinPaintTypes.NONE) {
                    return;
                }
                quads.add(new SkinCubeFace(x, y, z, paintColor, 255, dir, SkinCubes.SOLID));
            });
            if (quads.size() != 0) {
                Rectangle3i bounds = box.getBounds();
                AWVoxelShape renderShape = AWVoxelShape.box(new Rectangle3f(bounds));
                allQuads.put(entry.getKey(), new PackedQuad(bounds, renderShape, quads));
            }
        }
        return allQuads;
    }

    public void forEach(BiConsumer<SkinRenderType, ArrayList<SkinCubeFace>> action) {
        allFaces.forEach(action);
    }

    private void loadFaces(ArrayList<SkinCubeFace> faces) {
        for (SkinCubeFace face : faces) {
            if (face.getPaintType() == SkinPaintTypes.NONE) {
                continue;
            }
            SkinRenderType renderType = SkinRenderType.by(face.getCube());
            allFaces.computeIfAbsent(renderType, k -> new ArrayList<>()).add(face);
            colorInfo.add(face.getColor());
            faceTotal += 1;
        }
        for (ArrayList<SkinCubeFace> filteredFaces : allFaces.values()) {
            filteredFaces.sort(Comparator.comparingInt(f -> f.getDirection().get3DDataValue()));
        }
    }

    public ColorDescriptor getColorInfo() {
        return colorInfo;
    }

    public Rectangle3i getBounds() {
        return bounds;
    }

    public AWVoxelShape getRenderShape() {
        return renderShape;
    }

    public int getFaceTotal() {
        return faceTotal;
    }
}
