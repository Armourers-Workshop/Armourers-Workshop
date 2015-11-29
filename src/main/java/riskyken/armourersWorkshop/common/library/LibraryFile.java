package riskyken.armourersWorkshop.common.library;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class LibraryFile implements Comparable<LibraryFile> {
    
    public final String fileName;
    public final ISkinType skinType;
    
    public LibraryFile(String fileName, ISkinType skinType) {
        this.fileName = fileName;
        this.skinType = skinType;
    }
    
    public void writeToByteBuf(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, fileName);
        ByteBufUtils.writeUTF8String(buf, skinType.getRegistryName());
    }
    
    public static LibraryFile readFromByteBuf(ByteBuf buf) {
        String fileName = ByteBufUtils.readUTF8String(buf);
        String regName = ByteBufUtils.readUTF8String(buf);
        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
        return new LibraryFile(fileName, skinType);
    }

    @Override
    public int compareTo(LibraryFile o) {
        return fileName.compareTo(o.fileName);
    }
}
