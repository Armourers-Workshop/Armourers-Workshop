package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.utils.ModelPartBuilder;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class AbstractModelPartBuilderImpl extends ModelPartBuilder {

    public AbstractModelPartBuilderImpl(int width, int texHeight) {
        super(width, texHeight);
    }

    @Override
    public ModelPart build() {
        MeshDefinition mesh = new MeshDefinition();
        CubeDeformation deformation = CubeDeformation.NONE;
        PartDefinition part = mesh.getRoot();
        PartPose pose = PartPose.offset(offset.getX(), offset.getY(), offset.getZ());
        for (Cube cube : cubes) {
            Rectangle3f rect = cube.rect;
            float x = rect.getX();
            float y = rect.getY();
            float z = rect.getZ();
            float w = rect.getWidth();
            float h = rect.getHeight();
            float d = rect.getDepth();
            CubeDeformation def = deformation;
            if (cube.scale != 0) {
                def = def.extend(cube.scale);
            }
            part.addOrReplaceChild(cube.name, CubeListBuilder.create().texOffs(texU, texV).addBox(x, y, z, w, h, d, def), pose);
        }
        return part.bake(texWidth, texHeight);
    }
}
