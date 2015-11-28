package riskyken.armourersWorkshop.common.library;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

public final class LibraryHelper {
    
    private LibraryHelper() {
    }
    
    public static ArrayList<LibraryFile> getSkinFilesInDirectory(File directory) {
        ArrayList<LibraryFile> fileList = new ArrayList<LibraryFile>();
        
        if (!directory.exists() | !directory.isDirectory()) {
            return fileList;
        }
        
        File[] templateFiles;
        try {
            templateFiles = directory.listFiles();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, "Armour file list load failed.");
            e.printStackTrace();
            return fileList;
        }
        
        for (int i = 0; i < templateFiles.length; i++) {
            if (templateFiles[i].getName().endsWith(".armour")) {
                String cleanName = FilenameUtils.removeExtension(templateFiles[i].getName());
                ISkinType skinType = SkinIOUtils.getSkinTypeNameFromFile(templateFiles[i]);
                if (skinType != null) {
                    fileList.add(new LibraryFile(cleanName, skinType));
                }
            }
        }
        
        Collections.sort(fileList);
        
        return fileList;
    }
}
