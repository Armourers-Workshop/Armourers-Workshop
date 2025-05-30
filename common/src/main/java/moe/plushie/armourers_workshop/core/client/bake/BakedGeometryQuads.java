package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.core.math.ITransform;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.SkinPreviewData;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryFace;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryOptions;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFaceCuller;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureModel;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintData;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class BakedGeometryQuads {

    //private final HashMap<Direction, ArrayList<BakedCubeFace>> dirFaces = new HashMap<>();
    private final HashMap<IRenderType, CompressedList<BakedGeometryFace>> splitFaces = new HashMap<>();

    private final OpenVoxelShape shape;
    private final ColorDescriptor colorInfo;
    private final SkinUsedCounter usedCounter;

    public BakedGeometryQuads(OpenVoxelShape shape, ColorDescriptor colorInfo, SkinUsedCounter usedCounter) {
        this.shape = shape;
        this.colorInfo = colorInfo;
        this.usedCounter = usedCounter;
    }

    public static QuadsList<SkinPartType> from(SkinPart part) {
        var quads = new QuadsList<SkinPartType>();
        var geometries = part.getGeometries();
        var shape = geometries.getShape();
        var bounds = new OpenRectangle3i(shape.bounds());
        SkinCubeFaceCuller.cullFaces2(geometries, bounds, part.getType()).forEach(result -> {
            // when has a different part type, it means the skin part was split.
            var newTransform = OpenTransform3f.createTranslateTransform(new OpenVector3f(result.getOrigin()));
            var newShape = shape;
            if (result.getPartType() != part.getType()) {
                var fixedBounds = result.getBounds().offset(bounds.origin());
                newShape = OpenVoxelShape.box(fixedBounds);
            }
            var newQuads = new BakedGeometryQuads(newShape, new ColorDescriptor(), result.getUsedCounter());
            newQuads.loadFaces(result.getFaces());
            quads.add(result.getPartType(), newTransform, newQuads);
        });
        return quads;
    }

    public static QuadsList<SkinPartType> from(SkinPreviewData previewData) {
        var allQuads = new QuadsList<SkinPartType>();
        if (previewData == null) {
            return allQuads;
        }
        previewData.forEach((transform, data) -> {
            var shape = data.getShape();
            var bounds = new OpenRectangle3i(shape.bounds());
            SkinCubeFaceCuller.cullFaces2(data, bounds, SkinPartTypes.BLOCK).forEach(result -> {
                var quads = new BakedGeometryQuads(shape, new ColorDescriptor(), result.getUsedCounter());
                quads.loadFaces(result.getFaces());
                allQuads.add(result.getPartType(), transform, quads);
            });
        });
        return allQuads;
    }

    public static QuadsList<SkinPartType> from(SkinPaintData paintData) {
        var allQuads = new QuadsList<SkinPartType>();
        if (paintData == null) {
            return allQuads;
        }
        for (var entry : EntityTextureModel.of(paintData.getWidth(), paintData.getHeight(), false).entrySet()) {
            var box = entry.getValue();
            var faces = new ArrayList<SkinGeometryFace>();
            box.forEach((texture, x, y, z, dir) -> {
                var paintColor = SkinPaintColor.of(paintData.getColor(texture));
                if (paintColor.getPaintType() == SkinPaintTypes.NONE) {
                    return;
                }
                // in the vanilla's player textures are rendering without diffuse lighting.
                var id = dir.get3DDataValue();
                var shape = new OpenRectangle3f(x, y, z, 1, 1, 1);
                var transform = OpenTransform3f.IDENTITY;
                faces.add(new SkinCubeFace(id, SkinGeometryTypes.BLOCK_SOLID, SkinGeometryOptions.EMPTY, transform, null, shape, dir, paintColor, 255));
            });
            if (!faces.isEmpty()) {
                var quads = new BakedGeometryQuads(OpenVoxelShape.box(box.getBounds()), new ColorDescriptor(), new SkinUsedCounter());
                quads.loadFaces(faces);
                allQuads.add(entry.getKey(), OpenTransform3f.IDENTITY, quads);
            }
        }
        return allQuads;
    }

    public static BakedGeometryQuads merge(BakedGeometryQuads parent, List<Pair<ITransform, BakedGeometryQuads>> children) {
        // when children is empty, we no needs merge action.
        if (children.isEmpty()) {
            return parent;
        }
        // we need to recalculate the render bounds and shape.
        var mergedShape = parent.getShape().copy();
        children.forEach(pair -> {
            var transform = pair.getKey();
            var child = pair.getValue();
            if (child.getShape().isEmpty()) {
                return;
            }
            var shape = child.getShape().copy();
            var poseStack = new OpenPoseStack();
            transform.apply(poseStack);
            shape.mul(poseStack.last().pose());
            mergedShape.add(shape);
        });
        if (!mergedShape.isEmpty()) {
            mergedShape.optimize();
        }
        var mergedQuads = new BakedGeometryQuads(mergedShape, parent.getColorInfo().copy(), parent.getUsedCounter().copy());
        parent.splitFaces.forEach((key, value) -> mergedQuads.splitFaces.put(key, value.copy()));
        children.forEach(pair -> {
            var transform = pair.getKey();
            var child = pair.getValue();
            child.splitFaces.forEach((key, value) -> mergedQuads.splitFaces.computeIfAbsent(key, CompressedList::new).addAll(transform, value));
            mergedQuads.getColorInfo().add(child.getColorInfo());
            mergedQuads.getUsedCounter().add(child.getUsedCounter());
        });
        return mergedQuads;
    }

    public void forEach(BiConsumer<IRenderType, CompressedList<BakedGeometryFace>> action) {
        splitFaces.forEach(action);
    }

//    public void forEach(OpenRay ray, Consumer<BakedCubeFace> recorder) {
//        if (dirFaces.isEmpty()) {
//            loadDirFaces();
//        }
//        dirFaces.forEach((dir, faces) -> {
//            for (var face : faces) {
//                if (face.intersects(ray)) {
//                    recorder.accept(face);
//                }
//            }
//        });
//    }

    private void loadFaces(Collection<? extends SkinGeometryFace> geometryFaces) {
        for (var geometryFace : geometryFaces) {
            if (!geometryFace.isVisible()) {
                continue;
            }
            var bakedFace = new BakedGeometryFace(geometryFace);
            addSplitFace(bakedFace.getRenderType(), bakedFace);
            if (bakedFace.getRenderTypeVariants() != null) {
                bakedFace.getRenderTypeVariants().forEach(renderType -> addSplitFace(renderType, bakedFace));
            }
            if (bakedFace.getDefaultColor() != null) {
                colorInfo.add(bakedFace.getDefaultColor());
            }
        }
        for (var filteredFaces : splitFaces.values()) {
            filteredFaces.sort(Comparator.comparingDouble(BakedGeometryFace::getPriority));
            usedCounter.addFaceTotal(filteredFaces.size());
        }
    }

//    private void loadDirFaces() {
//        splitFaces.values().forEach(faces -> faces.forEach(face -> {
//            dirFaces.computeIfAbsent(face.getDirection(), k -> new ArrayList<>()).add(face);
//        }));
//    }

    private void addSplitFace(IRenderType renderType, BakedGeometryFace bakedFace) {
        splitFaces.computeIfAbsent(renderType, CompressedList::new).add(bakedFace);
    }

    public SkinUsedCounter getUsedCounter() {
        return usedCounter;
    }

    public ColorDescriptor getColorInfo() {
        return colorInfo;
    }

    public OpenVoxelShape getShape() {
        return shape;
    }

    public static class CompressedList<T> {

        private final IRenderType renderType;
        private final ArrayList<T> values = new ArrayList<>();
        private final ArrayList<Pair<ITransform, List<T>>> transformedValues = new ArrayList<>();

        public CompressedList(IRenderType renderType) {
            this.renderType = renderType;
        }

        public void add(T face) {
            values.add(face);
        }

        public void addAll(ITransform transform, CompressedList<T> compressedList) {
            transformedValues.add(Pair.of(transform, compressedList.values));
            for (var transformedValue : compressedList.transformedValues) {
                var combinedTransform = new SkinPartTransform();
                combinedTransform.addChild(transform);
                combinedTransform.addChild(transformedValue.getKey());
                transformedValues.add(Pair.of(combinedTransform, transformedValue.getValue()));
            }
        }

        public void sort(Comparator<? super T> comparator) {
            values.sort(comparator);
        }

        public void forEach(BiConsumer<ITransform, List<T>> consumer) {
            consumer.accept(OpenTransform3f.IDENTITY, values);
            for (var transformedValue : transformedValues) {
                consumer.accept(transformedValue.getLeft(), transformedValue.getValue());
            }
        }

        public int size() {
            int total = values.size();
            for (var transformedValue : transformedValues) {
                total += transformedValue.getValue().size();
            }
            return total;
        }

        public IRenderType renderType() {
            return renderType;
        }

        public CompressedList<T> copy() {
            var list = new CompressedList<T>(renderType);
            list.values.addAll(values);
            list.transformedValues.addAll(transformedValues);
            return list;
        }
    }

    public static class QuadsList<T> {

        private final ArrayList<Triple<T, ITransform, BakedGeometryQuads>> quads = new ArrayList<>();

        public void add(T partType, ITransform partTransform, BakedGeometryQuads quad) {
            quads.add(Triple.of(partType, partTransform, quad));
        }

        public void forEach(QuadsConsumer<T> consumer) {
            quads.forEach(pair -> consumer.accept(pair.getLeft(), pair.getMiddle(), pair.getRight()));
        }
    }

    public interface QuadsConsumer<T> {
        void accept(T key, ITransform partTransform, BakedGeometryQuads quad);
    }
}
