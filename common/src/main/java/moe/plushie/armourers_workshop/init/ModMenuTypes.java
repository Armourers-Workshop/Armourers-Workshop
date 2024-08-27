package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IBlockMenuProvider;
import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.builder.client.gui.ColorMixerWindow;
import moe.plushie.armourers_workshop.builder.client.gui.OutfitMakerWindow;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.AdvancedBuilderWindow;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.ArmourerWindow;
import moe.plushie.armourers_workshop.builder.menu.AdvancedBuilderMenu;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.menu.ColorMixerMenu;
import moe.plushie.armourers_workshop.builder.menu.OutfitMakerMenu;
import moe.plushie.armourers_workshop.core.client.gui.DyeTableWindow;
import moe.plushie.armourers_workshop.core.client.gui.SkinnableWindow;
import moe.plushie.armourers_workshop.core.client.gui.hologramprojector.HologramProjectorWindow;
import moe.plushie.armourers_workshop.core.client.gui.skinningtable.SkinningTableWindow;
import moe.plushie.armourers_workshop.core.client.gui.wardrobe.SkinWardrobeWindow;
import moe.plushie.armourers_workshop.core.menu.DyeTableMenu;
import moe.plushie.armourers_workshop.core.menu.HologramProjectorMenu;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeMenu;
import moe.plushie.armourers_workshop.core.menu.SkinWardrobeOpMenu;
import moe.plushie.armourers_workshop.core.menu.SkinnableMenu;
import moe.plushie.armourers_workshop.core.menu.SkinningTableMenu;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.client.gui.SkinLibraryWindow;
import moe.plushie.armourers_workshop.library.menu.CreativeSkinLibraryMenu;
import moe.plushie.armourers_workshop.library.menu.GlobalSkinLibraryMenu;
import moe.plushie.armourers_workshop.library.menu.SkinLibraryMenu;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public class ModMenuTypes {

    public static final IRegistryHolder<IMenuType<SkinWardrobeMenu>> WARDROBE = normal(SkinWardrobeMenu::new, DataSerializers.ENTITY_WARDROBE).bind(() -> SkinWardrobeWindow::new).build("wardrobe");
    public static final IRegistryHolder<IMenuType<SkinWardrobeOpMenu>> WARDROBE_OP = normal(SkinWardrobeOpMenu::new, DataSerializers.ENTITY_WARDROBE).bind(() -> SkinWardrobeWindow::new).build("wardrobe-op");

    public static final IRegistryHolder<IMenuType<SkinnableMenu>> SKINNABLE = block(SkinnableMenu::new, ModBlocks.SKINNABLE).bind(() -> SkinnableWindow::new).build("skinnable");

    public static final IRegistryHolder<IMenuType<DyeTableMenu>> DYE_TABLE = block(DyeTableMenu::new, ModBlocks.DYE_TABLE).bind(() -> DyeTableWindow::new).build("dye-table");
    public static final IRegistryHolder<IMenuType<SkinningTableMenu>> SKINNING_TABLE = block(SkinningTableMenu::new, ModBlocks.SKINNING_TABLE).bind(() -> SkinningTableWindow::new).build("skinning-table");

    public static final IRegistryHolder<IMenuType<CreativeSkinLibraryMenu>> SKIN_LIBRARY_CREATIVE = block(CreativeSkinLibraryMenu::new, ModBlocks.SKIN_LIBRARY_CREATIVE).bind(() -> SkinLibraryWindow::new).build("skin-library-creative");
    public static final IRegistryHolder<IMenuType<SkinLibraryMenu>> SKIN_LIBRARY = block(SkinLibraryMenu::new, ModBlocks.SKIN_LIBRARY).bind(() -> SkinLibraryWindow::new).build("skin-library");
    public static final IRegistryHolder<IMenuType<GlobalSkinLibraryMenu>> SKIN_LIBRARY_GLOBAL = block(GlobalSkinLibraryMenu::new, ModBlocks.SKIN_LIBRARY_GLOBAL).bind(() -> GlobalSkinLibraryWindow::new).build("skin-library-global");

    public static final IRegistryHolder<IMenuType<HologramProjectorMenu>> HOLOGRAM_PROJECTOR = block(HologramProjectorMenu::new, ModBlocks.HOLOGRAM_PROJECTOR).bind(() -> HologramProjectorWindow::new).build("hologram-projector");
    public static final IRegistryHolder<IMenuType<ColorMixerMenu>> COLOR_MIXER = block(ColorMixerMenu::new, ModBlocks.COLOR_MIXER).bind(() -> ColorMixerWindow::new).build("colour-mixer");
    public static final IRegistryHolder<IMenuType<ArmourerMenu>> ARMOURER = block(ArmourerMenu::new, ModBlocks.ARMOURER).bind(() -> ArmourerWindow::new).build("armourer");
    public static final IRegistryHolder<IMenuType<OutfitMakerMenu>> OUTFIT_MAKER = block(OutfitMakerMenu::new, ModBlocks.OUTFIT_MAKER).bind(() -> OutfitMakerWindow::new).build("outfit-maker");
    public static final IRegistryHolder<IMenuType<AdvancedBuilderMenu>> ADVANCED_SKIN_BUILDER = block(AdvancedBuilderMenu::new, ModBlocks.ADVANCED_SKIN_BUILDER).bind(() -> AdvancedBuilderWindow::new).build("advanced-skin-builder");

    private static <T extends AbstractContainerMenu, V> IMenuTypeBuilder<T> normal(IMenuProvider<T, V> factory, IMenuSerializer<V> serializer) {
        return BuilderManager.getInstance().createMenuTypeBuilder(factory, serializer);
    }

    private static <T extends AbstractContainerMenu> IMenuTypeBuilder<T> block(IBlockMenuProvider<T, ? super IGlobalPos> factory, IRegistryHolder<Block> block) {
        IMenuProvider<T, IGlobalPos> factory1 = (menuType, containerId, inventory, object) -> factory.createMenu(menuType, block.get(), containerId, inventory, object);
        return BuilderManager.getInstance().createMenuTypeBuilder(factory1, DataSerializers.GLOBAL_POS);
    }

    public static void init() {
    }
}
