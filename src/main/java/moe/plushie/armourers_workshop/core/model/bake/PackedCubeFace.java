package moe.plushie.armourers_workshop.core.model.bake;

import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.render.SkinRenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class PackedCubeFace {

    private int total = 0;
    private final Map<SkinRenderType, ArrayList<ColouredFace>> faces = new HashMap<>();

    public PackedColorInfo colorInfo;

    public void forEach(BiConsumer<SkinRenderType, ArrayList<ColouredFace>> action) {
        faces.forEach(action);
    }

    public void add(ColouredFace face) {
        if (face.paintType == SkinPaintTypes.NONE) {
            return;
        }
        SkinRenderType renderType = SkinRenderType.by(face.cube);
        ArrayList<ColouredFace> filteredFaces = faces.computeIfAbsent(renderType, (k) -> new ArrayList<>());
        filteredFaces.add(face);
        total += 1;
    }

    public void sort(Comparator<ColouredFace> c) {
        for (ArrayList<ColouredFace> filteredFaces : faces.values()) {
            filteredFaces.sort(c);
        }
    }

    public int getTotal() {
        return total;
    }
}
