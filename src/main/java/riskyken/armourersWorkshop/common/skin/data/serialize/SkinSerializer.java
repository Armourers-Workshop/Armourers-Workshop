package riskyken.armourersWorkshop.common.skin.data.serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.exception.NewerFileVersionException;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.data.SkinTexture;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.StreamUtils;

public final class SkinSerializer {
    
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
    
    private SkinSerializer() {}
    
    public static void writeToStream(Skin skin, DataOutputStream stream) throws IOException {
        // Write the skin file version.
        stream.writeInt(Skin.FILE_VERSION);
        // Write skin header.
        StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_HEADER);
        // Write skin props.
        StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_PROPS_HEADER);
        skin.getProperties().writeToStream(stream);
        StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_PROPS_FOOTER);
        // Write the skin type.
        StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_TYPE_HEADER);
        stream.writeUTF(skin.getSkinType().getRegistryName());
        StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_TYPE_FOOTER);
        // Write paint data.
        StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_PAINT_HEADER);
        if (skin.hasPaintData()) {
            stream.writeBoolean(true);
            for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
                stream.writeInt(skin.getPaintData()[i]);
            }
        } else {
            stream.writeBoolean(false);
        }
        StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_PAINT_FOOTER);
        //Write parts
        stream.writeByte(skin.getParts().size());
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_PART_HEADER);
            SkinPartSerializer.saveSkinPart(skinPart, stream);
            StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_PART_FOOTER);
        }
        // Write skin footer.
        StreamUtils.writeString(stream, Charsets.US_ASCII, TAG_SKIN_FOOTER);
    }
    
    public static Skin readSkinFromStream(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        int fileVersion = stream.readInt();
        if (fileVersion > Skin.FILE_VERSION) {
            throw new NewerFileVersionException();
        }
        if (fileVersion > 12) {
            String header = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!header.equals(TAG_SKIN_HEADER)) {
                ModLogger.log(Level.ERROR, "Error loading skin header.");
            }
            
            String propsHeader = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!propsHeader.equals(TAG_SKIN_PROPS_HEADER)) {
                ModLogger.log(Level.ERROR, "Error loading skin props header.");
            }
        }
        
        SkinProperties properties = new SkinProperties();
        boolean loadedProps = true;
        IOException e = null;
        if (fileVersion < 12) {
            String authorName = stream.readUTF();
            String customName = stream.readUTF();
            String tags = "";
            if (!(fileVersion < 4)) {
                tags = stream.readUTF(); 
            } else {
                tags = "";
            }
            properties.setProperty(Skin.KEY_AUTHOR_NAME, authorName);
            properties.setProperty(Skin.KEY_CUSTOM_NAME, customName);
            if (tags != null && !tags.equalsIgnoreCase("")) {
                properties.setProperty(KEY_TAGS, tags);
            }
        } else {
            try {
                properties.readFromStream(stream, fileVersion);
            } catch (IOException propE) {
                ModLogger.log(Level.ERROR, "prop load failed");
                e = propE;
                loadedProps = false;
            }
        }
        
        if (fileVersion > 12) {
            String propsFooter = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!propsFooter.equals(TAG_SKIN_PROPS_FOOTER)) {
                ModLogger.log(Level.ERROR, "Error loading skin props footer.");
            }
            
            String typeHeader = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!typeHeader.equals(TAG_SKIN_TYPE_HEADER)) {
                ModLogger.log(Level.ERROR, "Error loading skin type header.");
            }
        }
        
        ISkinType equipmentSkinType = null;

        if (fileVersion < 5) {
            if (loadedProps) {
                equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(stream.readByte() - 1);
            } else {
                throw e;
            }
        } else {
            if (loadedProps) {
                String regName = stream.readUTF();
                if (regName.equals(SkinTypeRegistry.oldSkinSkirt.getRegistryName())) {
                    regName = SkinTypeRegistry.skinLegs.getRegistryName();
                }
                equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
            } else {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    sb.append(new String(new byte[] {stream.readByte()}, "UTF-8"));
                    if (sb.toString().endsWith("armourers:")) {
                        break;
                    }
                }
                ModLogger.log("Got armourers");
                sb = new StringBuilder();
                sb.append("armourers:");
                while (SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(sb.toString()) == null) {
                    sb.append(new String(new byte[] {stream.readByte()}, "UTF-8"));
                }
                ModLogger.log(sb.toString());
                equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(sb.toString());
                ModLogger.log("got failed type " + equipmentSkinType);
            }
        }
        
        if (fileVersion > 12) {           
            String typeFooter = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!typeFooter.equals(TAG_SKIN_TYPE_FOOTER)) {
                ModLogger.log(Level.ERROR, "Error loading skin type footer.");
            }
        }
        
        if (equipmentSkinType == null) {
            throw new InvalidCubeTypeException();
        }
        
        if (fileVersion > 12) {           
            String typeFooter = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!typeFooter.equals(TAG_SKIN_PAINT_HEADER)) {
                ModLogger.log(Level.ERROR, "Error loading skin paint header.");
            }
        }
        int[] paintData = null;
        if (fileVersion > 7) {
            boolean hasPaintData = stream.readBoolean();
            if (hasPaintData) {
                paintData = new int[SkinTexture.TEXTURE_SIZE];
                for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
                    paintData[i] = stream.readInt();
                }
            }
        }
        if (fileVersion > 12) {           
            String typeFooter = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!typeFooter.equals(TAG_SKIN_PAINT_FOOTER)) {
                ModLogger.log(Level.ERROR, "Error loading skin paint footer.");
            }
        }
        
        int size = stream.readByte();
        ArrayList<SkinPart> parts = new ArrayList<SkinPart>();
        for (int i = 0; i < size; i++) {
            if (fileVersion > 12) {           
                String partHeader = StreamUtils.readString(stream, Charsets.US_ASCII);
                if (!partHeader.equals(TAG_SKIN_PART_HEADER)) {
                    ModLogger.log(Level.ERROR, "Error loading skin part header.");
                }
            }
            SkinPart part = SkinPartSerializer.loadSkinPart(stream, fileVersion);
            if (fileVersion > 12) {           
                String partFooter = StreamUtils.readString(stream, Charsets.US_ASCII);
                if (!partFooter.equals(TAG_SKIN_PART_FOOTER)) {
                    ModLogger.log(Level.ERROR, "Error loading skin part footer.");
                }
            }
            parts.add(part);
        }
        
        if (fileVersion > 12) {
            String footer = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!footer.equals(TAG_SKIN_FOOTER)) {
                ModLogger.log(Level.ERROR, "Error loading skin footer.");
            }
        }
        
        return new Skin(properties, equipmentSkinType, paintData, parts);
    }
    
    public static ISkinType readSkinTypeNameFromStream(DataInputStream stream) throws IOException, NewerFileVersionException {
        int fileVersion = stream.readInt();
        if (fileVersion > Skin.FILE_VERSION) {
            throw new NewerFileVersionException();
        }
        if (fileVersion > 12) {
            String header = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!header.equals(TAG_SKIN_HEADER)) {
                ModLogger.log(Level.ERROR, "Error loading skin header.");
            }
            
            String propsHeader = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!propsHeader.equals(TAG_SKIN_PROPS_HEADER)) {
                ModLogger.log(Level.ERROR, "Error loading skin props header.");
            }
        }
        
        SkinProperties properties = new SkinProperties();
        boolean loadedProps = true;
        IOException e = null;
        if (fileVersion < 12) {
            String authorName = stream.readUTF();
            String customName = stream.readUTF();
            String tags = "";
            if (!(fileVersion < 4)) {
                tags = stream.readUTF(); 
            } else {
                tags = "";
            }
            properties.setProperty(Skin.KEY_AUTHOR_NAME, authorName);
            properties.setProperty(Skin.KEY_CUSTOM_NAME, customName);
            if (tags != null && !tags.equalsIgnoreCase("")) {
                properties.setProperty(KEY_TAGS, tags);
            }
        } else {
            try {
                properties.readFromStream(stream, fileVersion);
            } catch (IOException propE) {
                ModLogger.log(Level.ERROR, "prop load failed");
                e = propE;
                loadedProps = false;
            }
        }
        
        if (fileVersion > 12) {
            String propsFooter = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!propsFooter.equals(TAG_SKIN_PROPS_FOOTER)) {
                ModLogger.log(Level.ERROR, "Error loading skin props footer.");
            }
            
            String typeHeader = StreamUtils.readString(stream, Charsets.US_ASCII);
            if (!typeHeader.equals(TAG_SKIN_TYPE_HEADER)) {
                ModLogger.log(Level.ERROR, "Error loading skin type header.");
            }
        }
        
        ISkinType equipmentSkinType = null;

        if (fileVersion < 5) {
            if (loadedProps) {
                equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(stream.readByte() - 1);
            } else {
                throw e;
            }
        } else {
            if (loadedProps) {
                String regName = stream.readUTF();
                if (regName.equals(SkinTypeRegistry.oldSkinSkirt.getRegistryName())) {
                    regName = SkinTypeRegistry.skinLegs.getRegistryName();
                }
                equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
            } else {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    sb.append(new String(new byte[] {stream.readByte()}, "UTF-8"));
                    if (sb.toString().endsWith("armourers:")) {
                        break;
                    }
                }
                ModLogger.log("Got armourers");
                sb = new StringBuilder();
                sb.append("armourers:");
                while (SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(sb.toString()) == null) {
                    sb.append(new String(new byte[] {stream.readByte()}, "UTF-8"));
                }
                ModLogger.log(sb.toString());
                equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(sb.toString());
                ModLogger.log("got failed type " + equipmentSkinType);
            }
        }
        return equipmentSkinType;
    }
}
