package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.builder.network.*;
import moe.plushie.armourers_workshop.core.network.*;
import moe.plushie.armourers_workshop.library.network.SaveSkinPacket;
import moe.plushie.armourers_workshop.library.network.UpdateLibraryFilePacket;
import moe.plushie.armourers_workshop.library.network.UpdateLibraryFilesPacket;
import moe.plushie.armourers_workshop.library.network.UploadSkinPacket;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public enum ModPackets {

    PACKET_UPDATE_CONTEXT(UpdateContextPacket.class, UpdateContextPacket::new),

    PACKET_UNDO_ACTION(UndoActionPacket.class, UndoActionPacket::new),
    PACKET_UPDATE_COLOR_PICKER(UpdateColorPickerPacket.class, UpdateColorPickerPacket::new),

    PACKET_REQUEST_FILE(RequestSkinPacket.class, RequestSkinPacket::new),
    PACKET_RESPONSE_FILE(ResponseSkinPacket.class, ResponseSkinPacket::new),
    PACKET_UPLOAD_FILE(SaveSkinPacket.class, SaveSkinPacket::new),

    PACKET_UPLOAD_SKIN_TO_GLOBAL(UploadSkinPacket.class, UploadSkinPacket::new),
    PACKET_UPDATE_OUTFIT_MAKER(UpdateOutfitMakerPacket.class, UpdateOutfitMakerPacket::new),
    PACKET_UPDATE_ARMOURER(UpdateArmourerPacket.class, UpdateArmourerPacket::new),

    PACKET_UPDATE_HOLOGRAM_PROJECTOR(UpdateHologramProjectorPacket.class, UpdateHologramProjectorPacket::new),
    PACKET_UPDATE_COLOUR_MIXER(UpdateColorMixerPacket.class, UpdateColorMixerPacket::new),

    PACKET_UPDATE_PAINTING_TOOL(UpdatePaintingToolPacket.class, UpdatePaintingToolPacket::new),
    PACKET_UPDATE_BLOCK_COLOR(UpdateBlockColorPacket.class, UpdateBlockColorPacket::new),

    PACKET_UPDATE_LIBRARY_FILE(UpdateLibraryFilePacket.class, UpdateLibraryFilePacket::new),
    PACKET_UPDATE_LIBRARY_FILES(UpdateLibraryFilesPacket.class, UpdateLibraryFilesPacket::new),

    PACKET_EXECUTE_COMMAND(ExecuteCommandPacket.class, ExecuteCommandPacket::new),

    PACKET_OPEN_WARDROBE(OpenWardrobePacket.class, OpenWardrobePacket::new),
    PACKET_UPDATE_WARDROBE(UpdateWardrobePacket.class, UpdateWardrobePacket::new);

    ModPackets(Class<? extends CustomPacket> packetClass, Function<FriendlyByteBuf, CustomPacket> factory) {
        CustomPacket.register(ordinal(), packetClass, factory);
    }

    public static void init() {
    }
}
