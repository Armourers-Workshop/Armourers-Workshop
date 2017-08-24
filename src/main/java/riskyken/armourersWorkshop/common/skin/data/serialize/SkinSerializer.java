package riskyken.armourersWorkshop.common.skin.data.serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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

public final class SkinSerializer {
    
    private SkinSerializer() {}
    
    public static Skin loadSkin(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        
        int fileVersion = stream.readInt();
        if (fileVersion > Skin.FILE_VERSION) {
            throw new NewerFileVersionException();
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
                properties.setProperty(Skin.KEY_TAGS, tags);
            }
        } else {
            try {
                properties.readFromStream(stream);
            } catch (IOException propE) {
                ModLogger.log(Level.ERROR, "prop load failed");
                e = propE;
                loadedProps = false;
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
                if (regName.equals(SkinTypeRegistry.skinSkirt.getRegistryName())) {
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
        
        if (equipmentSkinType == null) {
            throw new InvalidCubeTypeException();
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
        
        int size = stream.readByte();
        ArrayList<SkinPart> parts = new ArrayList<SkinPart>();
        for (int i = 0; i < size; i++) {
            SkinPart part = SkinPartSerializer.loadSkinPart(stream, fileVersion);
            parts.add(part);
        }
        
        return new Skin(properties, equipmentSkinType, paintData, parts);
    }
    
    public static ISkinType readSkinTypeNameFromStream(DataInputStream stream) throws IOException, NewerFileVersionException {
        int fileVersion = stream.readInt();
        if (fileVersion > Skin.FILE_VERSION) {
            throw new NewerFileVersionException();
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
                properties.setProperty(Skin.KEY_TAGS, tags);
            }
        } else {
            try {
                properties.readFromStream(stream);
            } catch (IOException propE) {
                ModLogger.log(Level.ERROR, "prop load failed");
                e = propE;
                loadedProps = false;
            }
        }
        
        ISkinType skinType;
        
        if (fileVersion < 5) {
            if (loadedProps) {
                skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(stream.readByte() - 1);
            } else {
                throw e;
            }
        } else {
            if (loadedProps) {
                String regName = stream.readUTF();
                if (regName.equals(SkinTypeRegistry.skinSkirt.getRegistryName())) {
                    regName = SkinTypeRegistry.skinLegs.getRegistryName();
                }
                skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
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
                skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(sb.toString());
                ModLogger.log("got failed type " + skinType);
            }
        }
        return skinType;
    }
    
    public static void writeToStream(Skin skin, DataOutputStream stream) throws IOException {
        stream.writeInt(Skin.FILE_VERSION);
        skin.getProperties().writeToStream(stream);
        stream.writeUTF(skin.getSkinType().getRegistryName());
        if (skin.hasPaintData()) {
            stream.writeBoolean(true);
            for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
                stream.writeInt(skin.getPaintData()[i]);
            }
        } else {
            stream.writeBoolean(false);
        }
        stream.writeByte(skin.getParts().size());
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            SkinPartSerializer.saveSkinPart(skinPart, stream);
        }
    }
}
