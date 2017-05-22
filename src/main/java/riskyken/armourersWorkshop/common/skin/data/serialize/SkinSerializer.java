package riskyken.armourersWorkshop.common.skin.data.serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.exception.NewerFileVersionException;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.data.SkinTexture;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class SkinSerializer {
    
    public static Skin loadSkin(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        
        int fileVersion = stream.readInt();
        if (fileVersion > Skin.FILE_VERSION) {
            throw new NewerFileVersionException();
        }
        
        SkinProperties properties = new SkinProperties();
        
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
            properties.readFromStream(stream);
        }
        
        ISkinType equipmentSkinType = null;

        if (fileVersion < 5) {
            equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(stream.readByte() - 1);
        } else {
            String regName = stream.readUTF();
            if (regName.equals(SkinTypeRegistry.skinSkirt.getRegistryName())) {
                regName = SkinTypeRegistry.skinLegs.getRegistryName();
            }
            equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
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
            parts.add(new SkinPart(stream, fileVersion));
        }
        
        return new Skin(properties, equipmentSkinType, paintData, parts);
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
            skin.getParts().get(i).writeToStream(stream);
        }
    }
}
