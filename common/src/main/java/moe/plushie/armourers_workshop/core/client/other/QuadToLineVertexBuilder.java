package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.compatibility.AbstractVertexBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class QuadToLineVertexBuilder extends AbstractVertexBuilder {

    private final ArrayList<ArrayList<Runnable>> vertexes = new ArrayList<>();

    public QuadToLineVertexBuilder(VertexConsumer consumer) {
        super(consumer);
    }

    @Override
    public void endVertex() {
        super.endVertex();
        vertexes.add(new ArrayList<>(pending));
        pending.clear();
        if (vertexes.size() == 4) {
            for (int i = 0; i < 4; ++i) {
                vertexes.get(i).forEach(Runnable::run);
                vertexes.get((i + 1) % 4).forEach(Runnable::run);
            }
            vertexes.clear();
        }
    }
}
