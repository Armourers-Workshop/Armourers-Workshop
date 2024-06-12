package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.face.SkinCuller;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.math.OpenRay;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.texture.PlayerTextureModel;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import moe.plushie.armourers_workshop.utils.texture.SkinPreviewData;
import moe.plushie.armourers_workshop.utils.texture.SkyBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class BakedCubeQuads {

    private final HashMap<Direction, ArrayList<BakedCubeFace>> dirFaces = new HashMap<>();
    private final HashMap<RenderType, ArrayList<BakedCubeFace>> splitFaces = new HashMap<>();

    private final Rectangle3i bounds;
    private final ISkinTransform transform;
    private final OpenVoxelShape renderShape;
    private final ColorDescriptor colorInfo = new ColorDescriptor();

    private int faceTotal = 0;

    public BakedCubeQuads(Rectangle3i bounds, ISkinTransform transform, OpenVoxelShape renderShape, Collection<SkinCubeFace> faces) {
        this.bounds = bounds;
        this.transform = transform;
        this.renderShape = renderShape;
        this.loadFaces(faces);
    }

    public static QuadsList<ISkinPartType> from(SkinPart part) {
        QuadsList<ISkinPartType> quads = new QuadsList<>();
        SkinCubes data = part.getCubeData();
        OpenVoxelShape shape = data.getShape();
        Rectangle3i bounds = new Rectangle3i(shape.bounds());
        SkinCuller.cullFaces2(data, bounds, part.getType()).forEach(result -> {
            // when has a different part type, it means the skin part was split.
            Rectangle3i newBounds = bounds;
            SkinTransform newTransform = SkinTransform.createTranslateTransform(new Vector3f(result.getOrigin()));
            OpenVoxelShape newRenderShape = shape;
            if (result.getPartType() != part.getType()) {
                newBounds = result.getBounds().offset(bounds.getOrigin());
                newRenderShape = OpenVoxelShape.box(new Rectangle3f(newBounds));
            }
            quads.add(result.getPartType(), new BakedCubeQuads(newBounds, newTransform, newRenderShape, result.getFaces()));
        });
        return quads;
    }

    public static QuadsList<ISkinPartType> from(SkinPreviewData previewData) {
        QuadsList<ISkinPartType> allQuads = new QuadsList<>();
        if (previewData == null) {
            return allQuads;
        }
        previewData.forEach((transform, data) -> {
            OpenVoxelShape shape = data.getShape();
            Rectangle3i bounds = new Rectangle3i(shape.bounds());
            SkinCuller.cullFaces2(data, bounds, SkinPartTypes.BLOCK).forEach(result -> {
                BakedCubeQuads quad = new BakedCubeQuads(bounds, transform, shape, result.getFaces());
                allQuads.add(result.getPartType(), quad);
            });
        });
        return allQuads;
    }

    public static QuadsList<ISkinPartType> from(SkinPaintData paintData) {
        QuadsList<ISkinPartType> allQuads = new QuadsList<>();
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
                Rectangle3f shape = new Rectangle3f(x, y, z, 1, 1, 1);
                SkinTransform transform = SkinTransform.IDENTITY;
                quads.add(new SkinCubeFace(shape, transform, paintColor, 255, dir, null, SkinCubeTypes.SOLID));
            });
            if (!quads.isEmpty()) {
                Rectangle3i bounds = box.getBounds();
                OpenVoxelShape renderShape = OpenVoxelShape.box(new Rectangle3f(bounds));
                allQuads.add(entry.getKey(), new BakedCubeQuads(bounds, SkinTransform.IDENTITY, renderShape, quads));
            }
        }
        return allQuads;
    }

    public void forEach(BiConsumer<RenderType, ArrayList<BakedCubeFace>> action) {
        splitFaces.forEach(action);
    }

    public void forEach(OpenRay ray, Consumer<BakedCubeFace> recorder) {
        if (dirFaces.isEmpty()) {
            loadDirFaces();
        }
        dirFaces.forEach((dir, faces) -> {
            for (BakedCubeFace face : faces) {
                if (face.intersects(ray)) {
                    recorder.accept(face);
                }
            }
        });
    }

    private void loadFaces(Collection<SkinCubeFace> faces) {
        for (SkinCubeFace face : faces) {
            if (face.getPaintType() == SkinPaintTypes.NONE) {
                continue;
            }
            BakedCubeFace bakedFace = new BakedCubeFace(face);
            addSplitFace(bakedFace.getRenderType(), bakedFace);
            if (bakedFace.getRenderTypeVariants() != null) {
                bakedFace.getRenderTypeVariants().forEach(renderType -> addSplitFace(renderType, bakedFace));
            }
            colorInfo.add(face.getColor());
            faceTotal += 1;
        }
        for (ArrayList<BakedCubeFace> filteredFaces : splitFaces.values()) {
            filteredFaces.sort(Comparator.comparingInt(f -> f.getDirection().get3DDataValue()));
        }
    }

    private void loadDirFaces() {
        splitFaces.values().forEach(faces -> faces.forEach(face -> {
            dirFaces.computeIfAbsent(face.getDirection(), k -> new ArrayList<>()).add(face);
        }));
    }

    private void addSplitFace(RenderType renderType, BakedCubeFace bakedFace) {
        splitFaces.computeIfAbsent(renderType, k -> new ArrayList<>()).add(bakedFace);
    }

    public ColorDescriptor getColorInfo() {
        return colorInfo;
    }

    public Rectangle3i getBounds() {
        return bounds;
    }

    public ISkinTransform getTransform() {
        return transform;
    }

    public OpenVoxelShape getRenderShape() {
        return renderShape;
    }

    public int getFaceTotal() {
        return faceTotal;
    }

    public static class QuadsList<T> {

        private final ArrayList<Pair<T, BakedCubeQuads>> quads = new ArrayList<>();

        public void add(T partType, BakedCubeQuads quad) {
            quads.add(Pair.of(partType, quad));
        }

        public void forEach(BiConsumer<T, BakedCubeQuads> consumer) {
            quads.forEach(pair -> consumer.accept(pair.getKey(), pair.getValue()));
        }
    }
}
