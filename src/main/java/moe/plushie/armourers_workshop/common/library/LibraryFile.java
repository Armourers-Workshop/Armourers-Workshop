package moe.plushie.armourers_workshop.common.library;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.library.ILibraryFile;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class LibraryFile implements Comparable<LibraryFile>, ILibraryFile {
    
    public final String fileName;
    public final String filePath;
    public final ISkinType skinType;
    public final boolean directory;
    
    public LibraryFile(String fileName, String filePath, ISkinType skinType) {
        this(fileName, filePath, skinType, false);
    }
    
    public LibraryFile(String fullFilePath) {
        fullFilePath = fullFilePath.replace("\\", "/");
        String[] splitFile = fullFilePath.split("/");
        this.fileName = splitFile[splitFile.length - 1];
        this.filePath = fullFilePath.substring(0, fullFilePath.length() - fileName.length());
        this.skinType = null;
        this.directory = false;
    }
    
    public LibraryFile(String fileName, String filePath, ISkinType skinType, boolean directory) {
        this.fileName = fileName.replace("\\", "/");
        this.filePath = filePath.replace("\\", "/");
        this.skinType = skinType;
        this.directory = directory;
    }
    
    public void writeToByteBuf(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, fileName);
        ByteBufUtils.writeUTF8String(buf, filePath);
        buf.writeBoolean(directory);
        buf.writeBoolean(skinType != null);
        if (skinType != null) {
            ByteBufUtils.writeUTF8String(buf, skinType.getRegistryName());
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (directory ? 1231 : 1237);
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LibraryFile other = (LibraryFile) obj;
        if (directory != other.directory)
            return false;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (filePath == null) {
            if (other.filePath != null)
                return false;
        } else if (!filePath.equals(other.filePath))
            return false;
        return true;
    }

    public static LibraryFile readFromByteBuf(ByteBuf buf) {
        String fileName = ByteBufUtils.readUTF8String(buf);
        String filePath = ByteBufUtils.readUTF8String(buf);
        boolean directory = buf.readBoolean();
        ISkinType skinType = null;
        if (buf.readBoolean()) {
            String regName = ByteBufUtils.readUTF8String(buf);
            skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
        }
        return new LibraryFile(fileName, filePath, skinType, directory);
    }
    
    @Override
    public String getFullName() {
        return this.filePath + this.fileName;
    }
    
    public boolean isDirectory() {
        return directory;
    }

    @Override
    public int compareTo(LibraryFile o) {
        if (isDirectory() & !o.isDirectory()) {
            return getFullName().compareToIgnoreCase(o.getFullName()) - 1000000;
        } else if (!isDirectory() & o.isDirectory()) {
            return getFullName().compareToIgnoreCase(o.getFullName()) + 1000000;
        }
        return getFullName().compareToIgnoreCase(o.getFullName());
    }

    @Override
    public String toString() {
        return "LibraryFile [fileName=" + fileName + ", filePath=" + filePath + ", skinType=" + skinType + ", directory=" + directory + "]";
    }
}
