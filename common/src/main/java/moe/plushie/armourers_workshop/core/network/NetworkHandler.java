package moe.plushie.armourers_workshop.core.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.function.Function;

public class NetworkHandler {

    private static final HashMap<Class<? extends CustomPacket>, PacketTypes> REVERSE_LOOKUP = new HashMap<>();

    public static void init() {

    }

    public enum PacketTypes {
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

        private final Function<FriendlyByteBuf, CustomPacket> factory;

        PacketTypes(Class<? extends CustomPacket> packetClass, Function<FriendlyByteBuf, CustomPacket> factory) {
            this.factory = factory;
            REVERSE_LOOKUP.put(packetClass, this);
        }

        public static PacketTypes getPacket(final int id) {
            return values()[id];
        }

        public static PacketTypes getID(final Class<? extends CustomPacket> c) {
            return REVERSE_LOOKUP.get(c);
        }

        public CustomPacket parsePacket(final FriendlyByteBuf in) throws IllegalArgumentException {
            return this.factory.apply(in);
        }
    }
}
