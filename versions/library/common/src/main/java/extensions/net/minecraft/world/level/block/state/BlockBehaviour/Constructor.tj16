package extensions.net.minecraft.world.level.block.state.BlockBehaviour;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterialColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.16, 1.20)")
public class Constructor {

    private static final ImmutableMap<AbstractBlockMaterial, Material> MATERIALS = ImmutableMap.<AbstractBlockMaterial, Material>builder()
            .put(AbstractBlockMaterial.STONE, Material.STONE)
            .put(AbstractBlockMaterial.GLASS, Material.GLASS)
            .build();

    private static final ImmutableMap<AbstractBlockMaterialColor, MaterialColor> MATERIAL_COLORS = ImmutableMap.<AbstractBlockMaterialColor, MaterialColor>builder()
            .put(AbstractBlockMaterialColor.NONE, MaterialColor.NONE)
            .build();

    @Extension
    public static class Properties {

        public static BlockBehaviour.Properties of(@ThisClass Class<?> clazz, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor) {
            return BlockBehaviour.Properties.of(MATERIALS.get(material), MATERIAL_COLORS.get(materialColor));
        }
    }
}
