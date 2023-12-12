package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.registry.IBlockEntityTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.BoundingBoxBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.ColorMixerBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.OutfitMakerBlockEntity;
import moe.plushie.armourers_workshop.builder.blockentity.SkinCubeBlockEntity;
import moe.plushie.armourers_workshop.builder.client.render.AdvancedBuilderBlockRenderer;
import moe.plushie.armourers_workshop.builder.client.render.ArmourerBlockRenderer;
import moe.plushie.armourers_workshop.builder.client.render.SkinCubeBlockRenderer;
import moe.plushie.armourers_workshop.core.blockentity.DyeTableBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.render.HologramProjectorBlockRenderer;
import moe.plushie.armourers_workshop.core.client.render.SkinnableBlockRenderer;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.library.blockentity.GlobalSkinLibraryBlockEntity;
import moe.plushie.armourers_workshop.library.blockentity.SkinLibraryBlockEntity;
import moe.plushie.armourers_workshop.library.client.render.GlobalSkinLibraryBlockRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings("unused")
public final class ModBlockEntityTypes {

    public static final IRegistryKey<IBlockEntityType<HologramProjectorBlockEntity>> HOLOGRAM_PROJECTOR = create(HologramProjectorBlockEntity::new).of(ModBlocks.HOLOGRAM_PROJECTOR).bind(() -> HologramProjectorBlockRenderer::new).build(ModConstants.BLOCK_HOLOGRAM_PROJECTOR);
    public static final IRegistryKey<IBlockEntityType<OutfitMakerBlockEntity>> OUTFIT_MAKER = create(OutfitMakerBlockEntity::new).of(ModBlocks.OUTFIT_MAKER).build(ModConstants.BLOCK_OUTFIT_MAKER);
    public static final IRegistryKey<IBlockEntityType<DyeTableBlockEntity>> DYE_TABLE = create(DyeTableBlockEntity::new).of(ModBlocks.DYE_TABLE).build(ModConstants.BLOCK_DYE_TABLE);

    public static final IRegistryKey<IBlockEntityType<ColorMixerBlockEntity>> COLOR_MIXER = create(ColorMixerBlockEntity::new).of(ModBlocks.COLOR_MIXER).build(ModConstants.BLOCK_COLOR_MIXER);
    public static final IRegistryKey<IBlockEntityType<ArmourerBlockEntity>> ARMOURER = create(ArmourerBlockEntity::new).of(ModBlocks.ARMOURER).bind(() -> ArmourerBlockRenderer::new).build(ModConstants.BLOCK_ARMOURER);
    public static final IRegistryKey<IBlockEntityType<AdvancedBuilderBlockEntity>> ADVANCED_SKIN_BUILDER = create(AdvancedBuilderBlockEntity::new).of(ModBlocks.ADVANCED_SKIN_BUILDER).bind(() -> AdvancedBuilderBlockRenderer::new).build(ModConstants.BLOCK_ADVANCED_SKIN_BUILDER);

    public static final IRegistryKey<IBlockEntityType<SkinLibraryBlockEntity>> SKIN_LIBRARY = create(SkinLibraryBlockEntity::new).of(ModBlocks.SKIN_LIBRARY).build(ModConstants.BLOCK_SKIN_LIBRARY);
    public static final IRegistryKey<IBlockEntityType<GlobalSkinLibraryBlockEntity>> SKIN_LIBRARY_GLOBAL = create(GlobalSkinLibraryBlockEntity::new).of(ModBlocks.SKIN_LIBRARY_GLOBAL).bind(() -> GlobalSkinLibraryBlockRenderer::new).build(ModConstants.BLOCK_SKIN_LIBRARY_GLOBAL);

    public static final IRegistryKey<IBlockEntityType<SkinnableBlockEntity>> SKINNABLE = create(SkinnableBlockEntity::new).of(ModBlocks.SKINNABLE).bind(() -> SkinnableBlockRenderer::new).build(ModConstants.BLOCK_SKINNABLE);
    public static final IRegistryKey<IBlockEntityType<BoundingBoxBlockEntity>> BOUNDING_BOX = create(BoundingBoxBlockEntity::new).of(ModBlocks.BOUNDING_BOX).bind(() -> SkinCubeBlockRenderer::new).build(ModConstants.BLOCK_BOUNDING_BOX);
    public static final IRegistryKey<IBlockEntityType<SkinCubeBlockEntity>> SKIN_CUBE = create(SkinCubeBlockEntity::new).of(ModBlocks.SKIN_CUBE).of(ModBlocks.SKIN_CUBE_GLASS).of(ModBlocks.SKIN_CUBE_GLASS_GLOWING).of(ModBlocks.SKIN_CUBE_GLOWING).bind(() -> SkinCubeBlockRenderer::new).build(ModConstants.BLOCK_SKIN_CUBE);

    // legacy
    private static final IRegistryKey<IBlockEntityType<SkinnableBlockEntity>> SKINNABLE_CUBE_SR = createLegacy(SKINNABLE).of(ModBlocks.SKINNABLE).build("skinnable-sr");
    private static final IRegistryKey<IBlockEntityType<BoundingBoxBlockEntity>> BOUNDING_BOX_SR = createLegacy(BOUNDING_BOX).of(ModBlocks.BOUNDING_BOX).build("bounding-box-sr");
    private static final IRegistryKey<IBlockEntityType<SkinCubeBlockEntity>> SKIN_CUBE_SR = createLegacy(SKIN_CUBE).of(ModBlocks.SKIN_CUBE).of(ModBlocks.SKIN_CUBE_GLASS).of(ModBlocks.SKIN_CUBE_GLASS_GLOWING).of(ModBlocks.SKIN_CUBE_GLOWING).build("skin-cube-sr");

    private static <T extends BlockEntity> IBlockEntityTypeBuilder<T> create(IBlockEntityType.Serializer<T> supplier) {
        return BuilderManager.getInstance().createBlockEntityTypeBuilder(supplier);
    }

    private static <T extends BlockEntity> IBlockEntityTypeBuilder<T> createLegacy(IRegistryKey<IBlockEntityType<T>> entityType) {
        return create((entityType1, blockPos, blockState) -> entityType.get().create(null, blockPos, blockState));
    }

    public static void init() {
    }
}
