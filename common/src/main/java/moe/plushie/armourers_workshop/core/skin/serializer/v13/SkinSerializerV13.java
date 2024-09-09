package moe.plushie.armourers_workshop.core.skin.serializer.v13;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.ISkinSerializer;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureModel;
import moe.plushie.armourers_workshop.core.texture.SkinPaintData;
import moe.plushie.armourers_workshop.init.ModLog;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class SkinSerializerV13 implements ISkinSerializer {

    public static final int FILE_VERSION = 13;

    private static final String TAG_SKIN_HEADER = "AW-SKIN-START";

    private static final String TAG_SKIN_PROPS_HEADER = "PROPS-START";
    private static final String TAG_SKIN_PROPS_FOOTER = "PROPS-END";

    private static final String TAG_SKIN_TYPE_HEADER = "TYPE-START";
    private static final String TAG_SKIN_TYPE_FOOTER = "TYPE-END";

    private static final String TAG_SKIN_PAINT_HEADER = "PAINT-START";
    private static final String TAG_SKIN_PAINT_FOOTER = "PAINT-END";

    private static final String TAG_SKIN_PART_HEADER = "PART-START";
    private static final String TAG_SKIN_PART_FOOTER = "PART-END";

    private static final String TAG_SKIN_FOOTER = "AW-SKIN-END";

    private static final String KEY_TAGS = "tags";

    private SkinPartSerializerV13 partSerializer = new SkinPartSerializerV13();

    public SkinSerializerV13() {
    }

    @Override
    public void writeToStream(Skin skin, IOutputStream stream, SkinFileOptions options) throws IOException {
        // Write skin header.
        stream.writeString(TAG_SKIN_HEADER);
        // Write skin props.
        stream.writeString(TAG_SKIN_PROPS_HEADER);
        stream.writeSkinProperties(skin.getProperties());
        stream.writeString(TAG_SKIN_PROPS_FOOTER);
        // Write the skin type.
        stream.writeString(TAG_SKIN_TYPE_HEADER);
        stream.writeType(skin.getType());
        stream.writeString(TAG_SKIN_TYPE_FOOTER);
        // Write paint data.
        stream.writeString(TAG_SKIN_PAINT_HEADER);
        if (skin.getPaintData() != null) {
            stream.writeBoolean(true);
            int[] colors = skin.getPaintData().getData();
            for (int i = 0; i < PlayerTextureModel.TEXTURE_OLD_SIZE; i++) {
                stream.writeInt(colors[i]);
            }
        } else {
            stream.writeBoolean(false);
        }
        stream.writeString(TAG_SKIN_PAINT_FOOTER);
        // Write parts
        stream.writeByte(skin.getParts().size());
        for (SkinPart skinPart : skin.getParts()) {
            stream.writeString(TAG_SKIN_PART_HEADER);
            partSerializer.saveSkinPart(skinPart, stream);
            stream.writeString(TAG_SKIN_PART_FOOTER);
        }
        // Write skin footer.
        stream.writeString(TAG_SKIN_FOOTER);
    }


    @Override
    public Skin readFromStream(IInputStream stream, SkinFileOptions options) throws IOException, InvalidCubeTypeException {
        if (!stream.readString().equals(TAG_SKIN_HEADER)) {
            ModLog.error("Error loading skin header.");
        }

        if (!stream.readString().equals(TAG_SKIN_PROPS_HEADER)) {
            ModLog.error("Error loading skin props header.");
        }

        SkinProperties properties = null;
        boolean loadedProps = true;
        IOException e = null;
        try {
            properties = stream.readSkinProperties();
        } catch (IOException propE) {
            ModLog.error("prop load failed");
            e = propE;
            loadedProps = false;
            properties = new SkinProperties();
        }

        if (!stream.readString().equals(TAG_SKIN_PROPS_FOOTER)) {
            ModLog.error("Error loading skin props footer.");
        }

        if (!stream.readString().equals(TAG_SKIN_TYPE_HEADER)) {
            ModLog.error("Error loading skin type header.");
        }

        ISkinType skinType = null;

        if (loadedProps) {
            String regName = stream.readString();
            skinType = SkinTypes.byName(regName);
        } else {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
            } while (!sb.toString().endsWith("armourers:"));
            ModLog.info("Got armourers");
            sb = new StringBuilder();
            sb.append("armourers:");
            while (SkinTypes.byName(sb.toString()) == null) {
                sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
            }
            ModLog.info(sb.toString());
            skinType = SkinTypes.byName(sb.toString());
            ModLog.info("got failed type " + skinType);
        }

        if (!stream.readString().equals(TAG_SKIN_TYPE_FOOTER)) {
            ModLog.error("Error loading skin type footer.");
        }

        if (skinType == null) {
            throw new InvalidCubeTypeException();
        }

        if (!stream.readString().equals(TAG_SKIN_PAINT_HEADER)) {
            ModLog.error("Error loading skin paint header.");
        }

        // TODO: support v2 texture
        SkinPaintData paintData = null;
        boolean hasPaintData = stream.readBoolean();
        if (hasPaintData) {
            paintData = SkinPaintData.v1();
            int[] colors = paintData.getData();
            for (int i = 0; i < PlayerTextureModel.TEXTURE_OLD_SIZE; i++) {
                colors[i] = stream.readInt();
            }
        }

        if (!stream.readString().equals(TAG_SKIN_PAINT_FOOTER)) {
            ModLog.error("Error loading skin paint footer.");
        }

        int size = stream.readByte();
        ArrayList<SkinPart> parts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (!stream.readString().equals(TAG_SKIN_PART_HEADER)) {
                ModLog.error("Error loading skin part header.");
            }
            var part = partSerializer.loadSkinPart(stream, options.getFileVersion());
            parts.add(part);
            if (!stream.readString().equals(TAG_SKIN_PART_FOOTER)) {
                ModLog.error("Error loading skin part footer.");
            }
        }

        if (!stream.readString().equals(TAG_SKIN_FOOTER)) {
            ModLog.error("Error loading skin footer.");
        }

        var builder = new Skin.Builder(skinType);
        builder.properties(properties);
        builder.paintData(paintData);
        builder.parts(parts);
        return builder.build();
    }

    @Override
    public SkinFileHeader readInfoFromStream(IInputStream stream, SkinFileOptions options) throws IOException {
        if (!stream.readString().equals(TAG_SKIN_HEADER)) {
            ModLog.error("Error loading skin header.");
        }

        if (!stream.readString().equals(TAG_SKIN_PROPS_HEADER)) {
            ModLog.error("Error loading skin props header.");
        }

        SkinProperties properties = null;
        boolean loadedProps = true;
        IOException e = null;
        try {
            properties = stream.readSkinProperties();
        } catch (IOException propE) {
            ModLog.error("prop load failed");
            e = propE;
            loadedProps = false;
            properties = new SkinProperties();
        }

        if (!stream.readString().equals(TAG_SKIN_PROPS_FOOTER)) {
            ModLog.error("Error loading skin props footer.");
        }

        if (!stream.readString().equals(TAG_SKIN_TYPE_HEADER)) {
            ModLog.error("Error loading skin type header.");
        }

        ISkinType skinType = null;

        if (loadedProps) {
            String regName = stream.readString();
            skinType = SkinTypes.byName(regName);
        } else {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
            } while (!sb.toString().endsWith("armourers:"));
            ModLog.info("Got armourers");
            sb = new StringBuilder();
            sb.append("armourers:");
            while (SkinTypes.byName(sb.toString()) == null) {
                sb.append(new String(new byte[]{stream.readByte()}, StandardCharsets.UTF_8));
            }
            ModLog.info(sb.toString());
            skinType = SkinTypes.byName(sb.toString());
            ModLog.info("got failed type " + skinType);
        }
        return SkinFileHeader.optimized(options.getFileVersion(), skinType, properties);
    }

    @Override
    public boolean isSupportedVersion(SkinFileOptions options) {
        return options.getFileVersion() == FILE_VERSION;
    }

    @Override
    public int getSupportedVersion() {
        return FILE_VERSION;
    }
}
