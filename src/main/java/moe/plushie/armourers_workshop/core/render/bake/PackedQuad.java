package moe.plushie.armourers_workshop.core.render.bake;

import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.face.SkinCuller;
import moe.plushie.armourers_workshop.utils.color.ColorDescriptor;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import moe.plushie.armourers_workshop.core.model.PlayerTextureModel;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.utils.Rectangle3f;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class PackedQuad {

    private final HashMap<SkinRenderType, ArrayList<SkinCubeFace>> allFaces = new HashMap<>();

    private final Rectangle3i bounds;
    private final CustomVoxelShape renderShape;
    private final ColorDescriptor colorInfo = new ColorDescriptor();

    public PackedQuad(Rectangle3i bounds, CustomVoxelShape renderShape, ArrayList<SkinCubeFace> faces) {
        this.bounds = bounds;
        this.renderShape = renderShape;
        this.loadFaces(faces);
    }

    public static PackedQuad from(SkinCubeData data) {
        CustomVoxelShape renderShape = data.getRenderShape();
        Rectangle3i bounds = new Rectangle3i(renderShape.bounds());
        return new PackedQuad(bounds, renderShape, SkinCuller.cullFaces(data, bounds));
    }

    public static HashMap<PlayerTextureModel, PackedQuad> from(int width, int height, int[] paintData) {
        HashMap<PlayerTextureModel, PackedQuad> allQuads = new HashMap<>();
        if (paintData == null || paintData.length == 0) {
            return allQuads;
        }
        for (PlayerTextureModel texturedModel : PlayerTextureModel.getPlayerModels(width, height, false)) {
            ArrayList<SkinCubeFace> quads = new ArrayList<>();
            texturedModel.forEach((u, v, x, y, z, dir) -> {
                PaintColor paintColor = PaintColor.of(paintData[v * width + u]);
                if (paintColor.getPaintType() == SkinPaintTypes.NONE) {
                    return;
                }
                quads.add(new SkinCubeFace(x, y, z, paintColor, 255, dir, SkinCubes.SOLID));
            });
            if (quads.size() != 0) {
                Rectangle3i bounds = texturedModel.getBounds();
                CustomVoxelShape renderShape = CustomVoxelShape.box(new Rectangle3f(bounds));
                allQuads.put(texturedModel, new PackedQuad(bounds, renderShape, quads));
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

    public CustomVoxelShape getRenderShape() {
        return renderShape;
    }

    static class CubeLoader {

    }
}
