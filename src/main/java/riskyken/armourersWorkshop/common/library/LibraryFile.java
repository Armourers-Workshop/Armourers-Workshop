package riskyken.armourersWorkshop.common.library;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class LibraryFile implements Comparable<LibraryFile> {
    
    public final String fileName;
    public final String filePath;
    public final ISkinType skinType;
    public final boolean directory;
    
    public LibraryFile(String fileName, String filePath, ISkinType skinType) {
        this(fileName, filePath, skinType, false);
    }
    
    public LibraryFile(String fileName, String filePath, ISkinType skinType, boolean directory) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.skinType = skinType;
        this.directory = directory;
    }
    
    public void writeToByteBuf(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, fileName);
        ByteBufUtils.writeUTF8String(buf, filePath);
        ByteBufUtils.writeUTF8String(buf, skinType.getRegistryName());
        buf.writeBoolean(directory);
    }
    
    public static LibraryFile readFromByteBuf(ByteBuf buf) {
        String fileName = ByteBufUtils.readUTF8String(buf);
        String filePath = ByteBufUtils.readUTF8String(buf);
        String regName = ByteBufUtils.readUTF8String(buf);
        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
        boolean directory = buf.readBoolean();
        return new LibraryFile(fileName, filePath, skinType, directory);
    }
    
    public String getFullName() {
        return this.filePath + this.fileName;
    }
    
    public boolean isDirectory() {
        return directory;
    }

    @Override
    public int compareTo(LibraryFile o) {
        if (isDirectory() & !o.isDirectory()) {
            return -1;
        } else if (!isDirectory() & o.isDirectory()) {
            return 1;
        }
        return getFullName().compareTo(o.getFullName());
    }
}
