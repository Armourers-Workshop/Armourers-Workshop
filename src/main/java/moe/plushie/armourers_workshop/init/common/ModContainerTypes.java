package moe.plushie.armourers_workshop.init.common;

import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.builder.container.ArmourerContainer;
import moe.plushie.armourers_workshop.builder.container.ColorMixerContainer;
import moe.plushie.armourers_workshop.builder.container.OutfitMakerContainer;
import moe.plushie.armourers_workshop.core.container.*;
import moe.plushie.armourers_workshop.core.permission.PermissionManager;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.utils.ContainerTypeBuilder;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.library.container.CreativeSkinLibraryContainer;
import moe.plushie.armourers_workshop.library.container.GlobalSkinLibraryContainer;
import moe.plushie.armourers_workshop.library.container.SkinLibraryContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ModContainerTypes {

    private static final Map<ContainerType<? extends Container>, Opener<?>> CONTAINER_TYPES = new HashMap<>();

    public static final ContainerType<SkinWardrobeContainer> WARDROBE = create("wardrobe", SkinWardrobeContainer::new, AWDataSerializers.ENTITY_WARDROBE);
    public static final ContainerType<SkinnableContainer> SKINNABLE = create("skinnable", SkinnableContainer::new, AWDataSerializers.WORLD_POS);

    public static final ContainerType<DyeTableContainer> DYE_TABLE = create("dye-table", DyeTableContainer::new, AWDataSerializers.WORLD_POS);
    public static final ContainerType<SkinningTableContainer> SKINNING_TABLE = create("skinning-table", SkinningTableContainer::new, AWDataSerializers.WORLD_POS);

    public static final ContainerType<SkinLibraryContainer> SKIN_LIBRARY_CREATIVE = create("skin-library-creative", CreativeSkinLibraryContainer::new, AWDataSerializers.WORLD_POS);
    public static final ContainerType<SkinLibraryContainer> SKIN_LIBRARY = create("skin-library", SkinLibraryContainer::new, AWDataSerializers.WORLD_POS);
    public static final ContainerType<GlobalSkinLibraryContainer> SKIN_LIBRARY_GLOBAL = create("skin-library-global", GlobalSkinLibraryContainer::new, AWDataSerializers.WORLD_POS);

    public static final ContainerType<HologramProjectorContainer> HOLOGRAM_PROJECTOR = create("hologram-projector", HologramProjectorContainer::new, AWDataSerializers.WORLD_POS);
    public static final ContainerType<ColorMixerContainer> COLOR_MIXER = create("colour-mixer", ColorMixerContainer::new, AWDataSerializers.WORLD_POS);
    public static final ContainerType<ArmourerContainer> ARMOURER = create("armourer", ArmourerContainer::new, AWDataSerializers.WORLD_POS);
    public static final ContainerType<OutfitMakerContainer> OUTFIT_MAKER = create("outfit-maker", OutfitMakerContainer::new, AWDataSerializers.WORLD_POS);

    public static void forEach(Consumer<ContainerType<?>> consumer) {
        CONTAINER_TYPES.keySet().forEach(consumer);
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean open(ContainerType<?> type, PlayerEntity player, T value) {
        Opener<T> opener = (Opener<T>)CONTAINER_TYPES.get(type);
        if (opener == null) {
            ModLog.warn("Trying to open container for unknown container type {}", type);
            return false;
        }
        return opener.open(player, value);
    }

    public static boolean open(ContainerType<?> type, PlayerEntity player, World world, BlockPos pos) {
        if (!PermissionManager.shouldOpenGui(type, player, pos)) {
            return false;
        }
        return open(type, player, IWorldPosCallable.create(world, pos));
    }

    private static <C extends Container, T> ContainerType<C> create(String registryName, ContainerTypeBuilder.ContainerFactory<C, T> factory, IPlayerDataSerializer<T> serializer) {
        String modId = AWCore.getModId();
        ContainerType<C> containerType = IForgeContainerType.create((id, inv, buf) -> factory.create(id, inv, serializer.read(buf, inv.player)));
        containerType.setRegistryName(modId, registryName);
        register(containerType, serializer, (player, object) -> {
            if (!(player instanceof ServerPlayerEntity)) {
                // Cannot open containers on the client or for non-players
                return false;
            }
            ITextComponent title = TranslateUtils.title("inventory." + modId + "." + registryName);
            INamedContainerProvider container = new SimpleNamedContainerProvider((wnd, p, pl) -> factory.create(wnd, p, object), title);
            NetworkHooks.openGui((ServerPlayerEntity) player, container, buffer -> serializer.write(buffer, player, object));
            return true;
        });
        return containerType;
    }

    private static <C extends Container, T> void register(ContainerType<C> type, IDataSerializer<T> serializer, Opener<T> opener) {
        CONTAINER_TYPES.put(type, opener);
    }

    @FunctionalInterface
    public interface Opener<T> {
        boolean open(PlayerEntity player, T host);
    }
}
