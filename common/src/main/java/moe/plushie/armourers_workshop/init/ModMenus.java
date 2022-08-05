package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IBlockMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.builder.client.gui.ColorMixerScreen;
import moe.plushie.armourers_workshop.builder.client.gui.OutfitMakerScreen;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.ArmourerScreen;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.menu.ColorMixerMenu;
import moe.plushie.armourers_workshop.builder.menu.OutfitMakerMenu;
import moe.plushie.armourers_workshop.core.client.gui.DyeTableScreen;
import moe.plushie.armourers_workshop.core.client.gui.SkinnableScreen;
import moe.plushie.armourers_workshop.core.client.gui.SkinningTableScreen;
import moe.plushie.armourers_workshop.core.client.gui.hologramprojector.HologramProjectorScreen;
import moe.plushie.armourers_workshop.core.client.gui.wardrobe.SkinWardrobeScreen;
import moe.plushie.armourers_workshop.core.menu.*;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.client.gui.SkinLibraryScreen;
import moe.plushie.armourers_workshop.library.menu.CreativeSkinLibraryMenu;
import moe.plushie.armourers_workshop.library.menu.GlobalSkinLibraryMenu;
import moe.plushie.armourers_workshop.library.menu.SkinLibraryMenu;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

public class ModMenus {

    public static final IRegistryKey<MenuType<SkinWardrobeMenu>> WARDROBE = normal(SkinWardrobeMenu::new, DataSerializers.ENTITY_WARDROBE).bind(() -> SkinWardrobeScreen::new).build("wardrobe");
    public static final IRegistryKey<MenuType<SkinWardrobeOpMenu>> WARDROBE_OP = normal(SkinWardrobeOpMenu::new, DataSerializers.ENTITY_WARDROBE).bind(() -> SkinWardrobeScreen::new).build("wardrobe-op");

    public static final IRegistryKey<MenuType<SkinnableMenu>> SKINNABLE = block(SkinnableMenu::new, ModBlocks.SKINNABLE).bind(() -> SkinnableScreen::new).build("skinnable");

    public static final IRegistryKey<MenuType<DyeTableMenu>> DYE_TABLE = block(DyeTableMenu::new, ModBlocks.DYE_TABLE).bind(() -> DyeTableScreen::new).build("dye-table");
    public static final IRegistryKey<MenuType<SkinningTableMenu>> SKINNING_TABLE = block(SkinningTableMenu::new, ModBlocks.SKINNING_TABLE).bind(() -> SkinningTableScreen::new).build("skinning-table");

    public static final IRegistryKey<MenuType<SkinLibraryMenu>> SKIN_LIBRARY_CREATIVE = library(CreativeSkinLibraryMenu::new, ModBlocks.SKIN_LIBRARY_CREATIVE).bind(() -> SkinLibraryScreen::new).build("skin-library-creative");
    public static final IRegistryKey<MenuType<SkinLibraryMenu>> SKIN_LIBRARY = library(SkinLibraryMenu::new, ModBlocks.SKIN_LIBRARY).bind(() -> SkinLibraryScreen::new).build("skin-library");
    public static final IRegistryKey<MenuType<GlobalSkinLibraryMenu>> SKIN_LIBRARY_GLOBAL = block(GlobalSkinLibraryMenu::new, ModBlocks.SKIN_LIBRARY_GLOBAL).bind(() -> GlobalSkinLibraryScreen::new).build("skin-library-global");

    public static final IRegistryKey<MenuType<HologramProjectorMenu>> HOLOGRAM_PROJECTOR = block(HologramProjectorMenu::new, ModBlocks.HOLOGRAM_PROJECTOR).bind(() -> HologramProjectorScreen::new).build("hologram-projector");
    public static final IRegistryKey<MenuType<ColorMixerMenu>> COLOR_MIXER = block(ColorMixerMenu::new, ModBlocks.COLOR_MIXER).bind(() -> ColorMixerScreen::new).build("colour-mixer");
    public static final IRegistryKey<MenuType<ArmourerMenu>> ARMOURER = block(ArmourerMenu::new, ModBlocks.ARMOURER).bind(() -> ArmourerScreen::new).build("armourer");
    public static final IRegistryKey<MenuType<OutfitMakerMenu>> OUTFIT_MAKER = block(OutfitMakerMenu::new, ModBlocks.OUTFIT_MAKER).bind(() -> OutfitMakerScreen::new).build("outfit-maker");

    private static <T extends AbstractContainerMenu, V> IMenuTypeBuilder<T> normal(IMenuProvider<T, V> factory, IPlayerDataSerializer<V> serializer) {
        return BuilderManager.getInstance().createMenuTypeBuilder(factory, serializer);
    }

    private static <T extends AbstractContainerMenu> IMenuTypeBuilder<T> block(IBlockMenuProvider<T, ? super ContainerLevelAccess> factory, IRegistryKey<Block> block) {
        IMenuProvider<T, ContainerLevelAccess> factory1 = (menuType, containerId, inventory, object) -> factory.createMenu(menuType, block.get(), containerId, inventory, object);
        return BuilderManager.getInstance().createMenuTypeBuilder(factory1, DataSerializers.WORLD_POS);
    }

    private static <T extends AbstractContainerMenu> IMenuTypeBuilder<SkinLibraryMenu> library(IBlockMenuProvider<T, ? super ContainerLevelAccess> factory, IRegistryKey<Block> block) {
        return ObjectUtils.unsafeCast(block(factory, block));
    }

    public static void init() {
    }
}
