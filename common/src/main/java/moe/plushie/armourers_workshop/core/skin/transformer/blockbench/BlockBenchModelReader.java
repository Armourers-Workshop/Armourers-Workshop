package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.core.skin.transformer.SkinPack;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModel;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelBone;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelGeometry;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelTexture;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelUV;
import moe.plushie.armourers_workshop.core.skin.transformer.SkinPackModelReader;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelCube;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockTransform;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Size3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BlockBenchModelReader implements SkinPackModelReader {

    private final BlockBenchPack pack;
    private final BlockBenchModelTexture texture;

    private final Vector3f baseOrigin;

    private final HashSet<Integer> usedTextureIds = new HashSet<>();

    public BlockBenchModelReader(String name, BlockBenchPack pack) {
        this.pack = pack;
        this.texture = new BlockBenchModelTexture(pack.getResolution(), pack.getTextures());
        this.baseOrigin = convertToLocal(pack.getOrigin());
    }

    @Override
    public BedrockModel readModel() throws IOException {
        BedrockModelGeometry.Builder builder = new BedrockModelGeometry.Builder();

        builder.identifier("geometry.steve");

        builder.textureWidth(pack.getResolution().getWidth());
        builder.textureHeight(pack.getResolution().getHeight());

        builder.visibleWidth(pack.getVisibleBox().getWidth());
        builder.visibleHeight(pack.getVisibleBox().getHeight());
        builder.visibleOffset(new Vector3f(0, pack.getVisibleBox().getDepth(), 0));

        builder.addBones(convertToBone(pack.getRootOutliner(), null));

        BedrockModel.Builder rootBuilder = new BedrockModel.Builder();
        rootBuilder.formatVersion("1.12.0");
        rootBuilder.addGeometry(builder.build());
        return rootBuilder.build();
    }

    @Override
    public BedrockModelTexture readTexture(BedrockModelGeometry geometry) throws IOException {
        texture.load(usedTextureIds);
        return texture;
    }

    @Override
    public Map<String, BedrockTransform> getTransforms() {
        // ignore when item transforms is not provided.
        if (pack.getTransforms() == null) {
            return null;
        }
        Map<String, BedrockTransform> results = new HashMap<>();
        pack.getTransforms().forEach((name, transform) -> {
            BedrockTransform.Builder builder = new BedrockTransform.Builder();
            builder.translation(convertToLocal(transform.getTranslation()));
            builder.rotation(convertToLocal(transform.getRotation()));
            builder.scale(transform.getScale());
            results.put(name, builder.build());
        });
        return results;
    }

    @Override
    public SkinPack getPack() {
        return pack;
    }

    // https://github.com/JannisX11/blockbench/blob/master/js/io/formats/bedrock.js#L781
    private ArrayList<BedrockModelBone> convertToBone(BlockBenchOutliner outliner, @Nullable BlockBenchOutliner parentOutliner) {
        ArrayList<BedrockModelBone> bones = new ArrayList<>();
        if (!outliner.allowExport()) {
            return bones;
        }

        BedrockModelBone.Builder builder = new BedrockModelBone.Builder();

        builder.name(outliner.getName());
        if (parentOutliner != null) {
            builder.parent(parentOutliner.getName());
        }

        if (!outliner.getOrigin().equals(Vector3f.ZERO)) {
            builder.pivot(convertToLocal(outliner.getOrigin()).subtracting(baseOrigin));
        }
        if (!outliner.getRotation().equals(Vector3f.ZERO)) {
            builder.rotation(convertToLocal(outliner.getRotation()));
        }

        builder.mirror(false);

        for (Object child : outliner.getChilds()) {
            BlockBenchOutliner childOutliner = ObjectUtils.safeCast(child, BlockBenchOutliner.class);
            if (childOutliner != null) {
                bones.addAll(convertToBone(childOutliner, outliner));
                continue;
            }
            String ref = ObjectUtils.safeCast(child, String.class);
            if (ref == null) {
                continue;
            }
            BlockBenchElement element = ObjectUtils.safeCast(pack.getObject(ref), BlockBenchElement.class);
            if (element == null || !element.allowExport()) {
                continue;
            }
            if (element.getType().equals("cube")) {
                builder.addCube(convertToCube(element));
            }
            // ignore locator type
            // ignore null_object type
            // ignore texture_mesh type
        }
        bones.add(0, builder.build());
        return bones;
    }

    // https://github.com/JannisX11/blockbench/blob/master/js/io/formats/bedrock.js#L726
    private BedrockModelCube convertToCube(BlockBenchElement element) {
        BedrockModelCube.Builder builder = new BedrockModelCube.Builder();

        builder.origin(convertToCubeOrigin(element).subtracting(baseOrigin));
        builder.size(convertToCubeSize(element));
        builder.inflate(element.getInflate());

        if (!element.getRotation().equals(Vector3f.ZERO)) {
            builder.pivot(convertToLocal(element.getOrigin()).subtracting(baseOrigin));
            builder.rotation(convertToLocal(element.getRotation()));
        }

        builder.uv(convertToCubeUV(element));
        return builder.build();
    }

    private Vector3f convertToCubeOrigin(BlockBenchElement element) {
        Vector3f from = element.getFrom();
        Vector3f to = element.getTo();
        // tx = -(from.getX() + (to.getX() - from.getX()))
        // ty = -(from.getY() + (to.getY() - from.getY()))
        return new Vector3f(-to.getX(), -to.getY(), from.getZ());
    }

    private Size3f convertToCubeSize(BlockBenchElement element) {
        Vector3f from = element.getFrom();
        Vector3f to = element.getTo();
        return new Size3f(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
    }

    private BedrockModelUV convertToCubeUV(BlockBenchElement element) {
        // box texture
        if (element.isBoxUV()) {
            BlockBenchModelUV uv = new BlockBenchModelUV(element.getUVOffset());
            element.getFaces().forEach((dir, face) -> {
                uv.setTextureId(face.getTextureId());
                usedTextureIds.add(face.getTextureId());
            });
            return uv;
        }
        // per-face texture
        BlockBenchModelUV uv = new BlockBenchModelUV(null);
        element.getFaces().forEach((dir, face) -> {
            uv.setTextureId(face.getTextureId());
            usedTextureIds.add(face.getTextureId());
            if (face.getTextureId() < 0) {
                return;
            }
            Rectangle2f rect = face.getRect();
            if (dir == Direction.UP || dir == Direction.DOWN) {
                Rectangle2f fixedRect = rect.copy();
                fixedRect.setX(rect.getMaxX());
                fixedRect.setY(rect.getMaxY());
                fixedRect.setWidth(-rect.getWidth());
                fixedRect.setHeight(-rect.getHeight());
                rect = fixedRect;
            }
            uv.put(dir, rect);
        });
        return uv;
    }

    private Vector3f convertToLocal(Vector3f pos) {
        if (!pos.equals(Vector3f.ZERO)) {
            return pos.scaling(-1, -1, 1);
        }
        return pos;
    }
}
