package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
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
import moe.plushie.armourers_workshop.core.texture.PlayerTextureModel;
import moe.plushie.armourers_workshop.core.texture.SkinPaintData;
import moe.plushie.armourers_workshop.core.texture.SkinPreviewData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
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
        var quads = new QuadsList<ISkinPartType>();
        var data = part.getCubeData();
        var shape = data.getShape();
        var bounds = new Rectangle3i(shape.bounds());
        SkinCuller.cullFaces2(data, bounds, part.getType()).forEach(result -> {
            // when has a different part type, it means the skin part was split.
            var newBounds = bounds;
            var newTransform = SkinTransform.createTranslateTransform(new Vector3f(result.getOrigin()));
            var newRenderShape = shape;
            if (result.getPartType() != part.getType()) {
                newBounds = result.getBounds().offset(bounds.getOrigin());
                newRenderShape = OpenVoxelShape.box(new Rectangle3f(newBounds));
            }
            quads.add(result.getPartType(), new BakedCubeQuads(newBounds, newTransform, newRenderShape, result.getFaces()));
        });
        return quads;
    }

    public static QuadsList<ISkinPartType> from(SkinPreviewData previewData) {
        var allQuads = new QuadsList<ISkinPartType>();
        if (previewData == null) {
            return allQuads;
        }
        previewData.forEach((transform, data) -> {
            var shape = data.getShape();
            var bounds = new Rectangle3i(shape.bounds());
            SkinCuller.cullFaces2(data, bounds, SkinPartTypes.BLOCK).forEach(result -> {
                var quad = new BakedCubeQuads(bounds, transform, shape, result.getFaces());
                allQuads.add(result.getPartType(), quad);
            });
        });
        return allQuads;
    }

    public static QuadsList<ISkinPartType> from(SkinPaintData paintData) {
        var allQuads = new QuadsList<ISkinPartType>();
        if (paintData == null) {
            return allQuads;
        }
        for (var entry : PlayerTextureModel.of(paintData.getWidth(), paintData.getHeight(), false).entrySet()) {
            var box = entry.getValue();
            var quads = new ArrayList<SkinCubeFace>();
            box.forEach((texture, x, y, z, dir) -> {
                var paintColor = PaintColor.of(paintData.getColor(texture));
                if (paintColor.getPaintType() == SkinPaintTypes.NONE) {
                    return;
                }
                // in the vanilla's player textures are rendering without diffuse lighting.
                var shape = new Rectangle3f(x, y, z, 1, 1, 1);
                var transform = SkinTransform.IDENTITY;
                quads.add(new SkinCubeFace(shape, transform, paintColor, 255, dir, null, SkinCubeTypes.SOLID));
            });
            if (!quads.isEmpty()) {
                var bounds = box.getBounds();
                var renderShape = OpenVoxelShape.box(new Rectangle3f(bounds));
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
            for (var face : faces) {
                if (face.intersects(ray)) {
                    recorder.accept(face);
                }
            }
        });
    }

    private void loadFaces(Collection<SkinCubeFace> faces) {
        for (var face : faces) {
            if (face.getPaintType() == SkinPaintTypes.NONE) {
                continue;
            }
            var bakedFace = new BakedCubeFace(face);
            addSplitFace(bakedFace.getRenderType(), bakedFace);
            if (bakedFace.getRenderTypeVariants() != null) {
                bakedFace.getRenderTypeVariants().forEach(renderType -> addSplitFace(renderType, bakedFace));
            }
            colorInfo.add(face.getColor());
            faceTotal += 1;
        }
        for (var filteredFaces : splitFaces.values()) {
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
