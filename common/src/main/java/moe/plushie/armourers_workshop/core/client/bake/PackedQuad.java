package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.face.SkinCuller;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.texture.PlayerTextureModel;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import moe.plushie.armourers_workshop.utils.texture.SkyBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Environment(value = EnvType.CLIENT)
public class PackedQuad {

    private final HashMap<RenderType, ArrayList<SkinCubeFace>> allFaces = new HashMap<>();

    private final Rectangle3i bounds;
    private final OpenVoxelShape renderShape;
    private final ColorDescriptor colorInfo = new ColorDescriptor();

    private int faceTotal = 0;

    public PackedQuad(Rectangle3i bounds, OpenVoxelShape renderShape, ArrayList<SkinCubeFace> faces) {
        this.bounds = bounds;
        this.renderShape = renderShape;
        this.loadFaces(faces);
    }

    public static PackedQuad from(SkinCubeData data) {
        OpenVoxelShape renderShape = data.getRenderShape();
        Rectangle3i bounds = new Rectangle3i(renderShape.bounds());
        return new PackedQuad(bounds, renderShape, SkinCuller.cullFaces(data, bounds));
    }

    public static HashMap<ISkinPartType, PackedQuad> from(SkinPaintData paintData) {
        HashMap<ISkinPartType, PackedQuad> allQuads = new HashMap<>();
        if (paintData == null) {
            return allQuads;
        }
        for (Map.Entry<ISkinPartType, SkyBox> entry : PlayerTextureModel.of(paintData.getWidth(), paintData.getHeight(), false).entrySet()) {
            SkyBox box = entry.getValue();
            ArrayList<SkinCubeFace> quads = new ArrayList<>();
            box.forEach((texture, x, y, z, dir) -> {
                PaintColor paintColor = PaintColor.of(paintData.getColor(texture));
                if (paintColor.getPaintType() == SkinPaintTypes.NONE) {
                    return;
                }
                // in the vanilla's player textures are rendering without diffuse lighting.
                quads.add(new SkinCubeFace(x, y, z, paintColor, 255, dir, SkinCubes.SOLID));
            });
            if (quads.size() != 0) {
                Rectangle3i bounds = box.getBounds();
                OpenVoxelShape renderShape = OpenVoxelShape.box(new Rectangle3f(bounds));
                allQuads.put(entry.getKey(), new PackedQuad(bounds, renderShape, quads));
            }
        }
        return allQuads;
    }

    public void forEach(BiConsumer<RenderType, ArrayList<SkinCubeFace>> action) {
        allFaces.forEach(action);
    }

    private void loadFaces(ArrayList<SkinCubeFace> faces) {
        for (SkinCubeFace face : faces) {
            if (face.getPaintType() == SkinPaintTypes.NONE) {
                continue;
            }
            RenderType renderType = SkinRenderType.by(face.getCube());
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

    public OpenVoxelShape getRenderShape() {
        return renderShape;
    }

    public int getFaceTotal() {
        return faceTotal;
    }
}
