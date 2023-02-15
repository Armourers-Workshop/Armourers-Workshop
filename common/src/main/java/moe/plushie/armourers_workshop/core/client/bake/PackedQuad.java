package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.face.SkinCuller;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.transform.SkinTransform;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.texture.PlayerTextureModel;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import moe.plushie.armourers_workshop.utils.texture.SkyBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Environment(value = EnvType.CLIENT)
public class PackedQuad {

    private final HashMap<RenderType, ArrayList<SkinCubeFace>> allFaces = new HashMap<>();

    private final Rectangle3i bounds;
    private final SkinTransform transform;
    private final OpenVoxelShape renderShape;
    private final ColorDescriptor colorInfo = new ColorDescriptor();

    private int faceTotal = 0;

    public PackedQuad(Rectangle3i bounds, SkinTransform transform, OpenVoxelShape renderShape, ArrayList<SkinCubeFace> faces) {
        this.bounds = bounds;
        this.transform = transform;
        this.renderShape = renderShape;
        this.loadFaces(faces);
    }

    public static PackedQuads from(SkinPart part) {
        PackedQuads quads = new PackedQuads();
        SkinCubes data = part.getCubeData();
        OpenVoxelShape renderShape = data.getRenderShape();
        Rectangle3i bounds = new Rectangle3i(renderShape.bounds());
        SkinCuller.cullFaces2(data, bounds, part.getType()).forEach(result -> {
            // when has a different part type, it means the skin part was split.
            Rectangle3i newBounds = bounds;
            SkinTransform newTransform = SkinTransform.createTranslateTransform(new Vector3f(result.getOrigin()));
            OpenVoxelShape newRenderShape = renderShape;
            if (result.getPartType() != part.getType()) {
                newBounds = result.getBounds().offset(bounds.getOrigin());
                newRenderShape = OpenVoxelShape.box(new Rectangle3f(newBounds));
            }
            quads.add(result.getPartType(), new PackedQuad(newBounds, newTransform, newRenderShape, result.getFaces()));
        });
        return quads;
    }

    public static PackedQuads from(SkinPaintData paintData) {
        PackedQuads allQuads = new PackedQuads();
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
                Vector3i pos = new Vector3i(x, y, z);
                quads.add(new SkinCubeFace(pos, paintColor, 255, dir, SkinCubeTypes.SOLID));
            });
            if (quads.size() != 0) {
                Rectangle3i bounds = box.getBounds();
                OpenVoxelShape renderShape = OpenVoxelShape.box(new Rectangle3f(bounds));
                allQuads.add(entry.getKey(), new PackedQuad(bounds, SkinTransform.IDENTIFIER, renderShape, quads));
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
            RenderType renderType = SkinRenderType.by(face.getType());
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

    public SkinTransform getTransform() {
        return transform;
    }

    public OpenVoxelShape getRenderShape() {
        return renderShape;
    }

    public int getFaceTotal() {
        return faceTotal;
    }

    public static class PackedQuads {

        ArrayList<Pair<ISkinPartType, PackedQuad>> quads = new ArrayList<>();

        public void add(ISkinPartType partType, PackedQuad quad) {
            quads.add(Pair.of(partType, quad));
        }

        public void forEach(BiConsumer<ISkinPartType, PackedQuad> consumer) {
            quads.forEach(pair -> consumer.accept(pair.getKey(), pair.getValue()));
        }
    }
}
