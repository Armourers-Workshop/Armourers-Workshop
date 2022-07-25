package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IMenuExtendFactory;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
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
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

    public static final IRegistryObject<MenuType<SkinWardrobeMenu>> WARDROBE = normal(SkinWardrobeMenu::new, DataSerializers.ENTITY_WARDROBE).bind(() -> SkinWardrobeScreen::new).build("wardrobe");
    public static final IRegistryObject<MenuType<SkinnableMenu>> SKINNABLE = normal(SkinnableMenu::new, DataSerializers.WORLD_POS).bind(() -> SkinnableScreen::new).build("skinnable");

    public static final IRegistryObject<MenuType<DyeTableMenu>> DYE_TABLE = normal(DyeTableMenu::new, DataSerializers.WORLD_POS).bind(() -> DyeTableScreen::new).build("dye-table");
    public static final IRegistryObject<MenuType<SkinningTableMenu>> SKINNING_TABLE = normal(SkinningTableMenu::new, DataSerializers.WORLD_POS).bind(() -> SkinningTableScreen::new).build("skinning-table");

    public static final IRegistryObject<MenuType<SkinLibraryMenu>> SKIN_LIBRARY_CREATIVE = library(CreativeSkinLibraryMenu::new, DataSerializers.WORLD_POS).bind(() -> SkinLibraryScreen::new).build("skin-library-creative");
    public static final IRegistryObject<MenuType<SkinLibraryMenu>> SKIN_LIBRARY = library(SkinLibraryMenu::new, DataSerializers.WORLD_POS).bind(() -> SkinLibraryScreen::new).build("skin-library");
    public static final IRegistryObject<MenuType<GlobalSkinLibraryMenu>> SKIN_LIBRARY_GLOBAL = normal(GlobalSkinLibraryMenu::new, DataSerializers.WORLD_POS).bind(() -> GlobalSkinLibraryScreen::new).build("skin-library-global");

    public static final IRegistryObject<MenuType<HologramProjectorMenu>> HOLOGRAM_PROJECTOR = normal(HologramProjectorMenu::new, DataSerializers.WORLD_POS).bind(() -> HologramProjectorScreen::new).build("hologram-projector");
    public static final IRegistryObject<MenuType<ColorMixerMenu>> COLOR_MIXER = normal(ColorMixerMenu::new, DataSerializers.WORLD_POS).bind(() -> ColorMixerScreen::new).build("colour-mixer");
    public static final IRegistryObject<MenuType<ArmourerMenu>> ARMOURER = normal(ArmourerMenu::new, DataSerializers.WORLD_POS).bind(() -> ArmourerScreen::new).build("armourer");
    public static final IRegistryObject<MenuType<OutfitMakerMenu>> OUTFIT_MAKER = normal(OutfitMakerMenu::new, DataSerializers.WORLD_POS).bind(() -> OutfitMakerScreen::new).build("outfit-maker");

    private static <T extends AbstractContainerMenu, V> IMenuTypeBuilder<T> normal(IMenuExtendFactory<T, V> factory, IPlayerDataSerializer<V> serializer) {
        return BuilderManager.getInstance().createMenuTypeBuilder(factory, serializer);
    }

    private static <T extends AbstractContainerMenu, V> IMenuTypeBuilder<SkinLibraryMenu> library(IMenuExtendFactory<T, V> factory, IPlayerDataSerializer<V> serializer) {
        return ObjectUtils.unsafeCast(normal(factory, serializer));
    }

    public static void init() {
    }
}
