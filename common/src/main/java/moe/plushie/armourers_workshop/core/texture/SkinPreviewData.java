package moe.plushie.armourers_workshop.core.texture;

import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinPartTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SkinPreviewData {

    private final Collection<Pair<ISkinTransform, SkinCubes>> allCubes;

    public SkinPreviewData(Collection<Pair<ISkinTransform, SkinCubes>> allCubes) {
        this.allCubes = allCubes;
    }

    public static SkinPreviewData of(Skin skin) {
        // we can't re-generate the preview data for a preview skin.
        if (skin.getPreviewData() != null) {
            return skin.getPreviewData();
        }
        var allCubes = new ArrayList<Pair<ISkinTransform, SkinCubes>>();
        eachPart(skin.getParts(), part -> {
            // apply the origin offset.
            var pos = part.getType().getRenderOffset();
            var offset = SkinTransform.createTranslateTransform(pos.getX(), pos.getY(), pos.getZ());
            // apply the marker rotation and offset.
            var transform = new SkinPartTransform(part, offset);
            allCubes.add(Pair.of(transform, part.getCubeData()));
        });
        return new SkinPreviewData(allCubes);
    }

    private static void eachPart(Collection<SkinPart> parts, Consumer<SkinPart> consumer) {
        for (var part : parts) {
            consumer.accept(part);
            eachPart(part.getParts(), consumer);
        }
    }

    public void forEach(BiConsumer<ISkinTransform, SkinCubes> consumer) {
        allCubes.forEach(pair -> consumer.accept(pair.getKey(), pair.getValue()));
    }
}
