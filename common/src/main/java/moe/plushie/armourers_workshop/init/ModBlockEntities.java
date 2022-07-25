package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.builder.blockentity.*;
import moe.plushie.armourers_workshop.builder.client.render.ArmourerBlockRenderer;
import moe.plushie.armourers_workshop.builder.client.render.SkinCubeBlockRenderer;
import moe.plushie.armourers_workshop.core.blockentity.DyeTableBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.render.HologramProjectorBlockRenderer;
import moe.plushie.armourers_workshop.core.client.render.SkinnableBlockRenderer;
import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityBuilder;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.library.blockentity.GlobalSkinLibraryBlockEntity;
import moe.plushie.armourers_workshop.library.blockentity.SkinLibraryBlockEntity;
import moe.plushie.armourers_workshop.library.client.render.GlobalSkinLibraryTitleEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class ModBlockEntities {

    public static final IRegistryObject<BlockEntityType<HologramProjectorBlockEntity>> HOLOGRAM_PROJECTOR = create(HologramProjectorBlockEntity::new).of(ModBlocks.HOLOGRAM_PROJECTOR).bind(() -> HologramProjectorBlockRenderer::new).build("hologram-projector");
    public static final IRegistryObject<BlockEntityType<OutfitMakerBlockEntity>> OUTFIT_MAKER = create(OutfitMakerBlockEntity::new).of(ModBlocks.OUTFIT_MAKER).build("outfit-maker");
    public static final IRegistryObject<BlockEntityType<DyeTableBlockEntity>> DYE_TABLE = create(DyeTableBlockEntity::new).of(ModBlocks.DYE_TABLE).build("dye-table");

    public static final IRegistryObject<BlockEntityType<ColorMixerBlockEntity>> COLOR_MIXER = create(ColorMixerBlockEntity::new).of(ModBlocks.COLOR_MIXER).build("colour-mixer");
    public static final IRegistryObject<BlockEntityType<ArmourerBlockEntity>> ARMOURER = create(ArmourerBlockEntity::new).of(ModBlocks.ARMOURER).bind(() -> ArmourerBlockRenderer::new).build("armourer");

    public static final IRegistryObject<BlockEntityType<SkinLibraryBlockEntity>> SKIN_LIBRARY = create(SkinLibraryBlockEntity::new).of(ModBlocks.SKIN_LIBRARY).build("skin-library");
    public static final IRegistryObject<BlockEntityType<GlobalSkinLibraryBlockEntity>> SKIN_LIBRARY_GLOBAL = create(GlobalSkinLibraryBlockEntity::new).of(ModBlocks.SKIN_LIBRARY_GLOBAL).bind(() -> GlobalSkinLibraryTitleEntityRenderer::new).build("skin-library-global");

    public static final IRegistryObject<BlockEntityType<SkinnableBlockEntity>> SKINNABLE_CUBE = create(SkinnableBlockEntity::new).of(ModBlocks.SKINNABLE).bind(() -> SkinnableBlockRenderer::new).build("skinnable");
    public static final IRegistryObject<BlockEntityType<BoundingBoxBlockEntity>> BOUNDING_BOX = create(BoundingBoxBlockEntity::new).of(ModBlocks.BOUNDING_BOX).bind(() -> SkinCubeBlockRenderer::new).build("bounding-box");
    public static final IRegistryObject<BlockEntityType<SkinCubeBlockEntity>> SKIN_CUBE = create(SkinCubeBlockEntity::new).of(ModBlocks.SKIN_CUBE).of(ModBlocks.SKIN_CUBE_GLASS).of(ModBlocks.SKIN_CUBE_GLASS_GLOWING).of(ModBlocks.SKIN_CUBE_GLOWING).bind(() -> SkinCubeBlockRenderer::new).build("skin-cube");

    // legacy
    private static final IRegistryObject<BlockEntityType<SkinnableBlockEntity>> SKINNABLE_CUBE_SR = create(SkinnableBlockEntity::new).of(ModBlocks.SKINNABLE).build("skinnable-sr");
    private static final IRegistryObject<BlockEntityType<BoundingBoxBlockEntity>> BOUNDING_BOX_SR = create(BoundingBoxBlockEntity::new).of(ModBlocks.BOUNDING_BOX).build("bounding-box-sr");
    private static final IRegistryObject<BlockEntityType<SkinCubeBlockEntity>> SKIN_CUBE_SR = create(SkinCubeBlockEntity::new).of(ModBlocks.SKIN_CUBE).of(ModBlocks.SKIN_CUBE_GLASS).of(ModBlocks.SKIN_CUBE_GLASS_GLOWING).of(ModBlocks.SKIN_CUBE_GLOWING).build("skin-cube-sr");

    private static <T extends BlockEntity> IBlockEntityBuilder<T> create(Supplier<T> supplier) {
        return BuilderManager.getInstance().createBlockEntityBuilder(supplier);
    }

    public static void init() {
    }
}
