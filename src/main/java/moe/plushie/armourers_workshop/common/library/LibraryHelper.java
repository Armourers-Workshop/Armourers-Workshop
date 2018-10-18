package moe.plushie.armourers_workshop.common.library;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;

public final class LibraryHelper {
    
    private LibraryHelper() {
    }
    
    public static ArrayList<LibraryFile> getSkinFilesInDirectory(File directory, boolean subDirectories) {
        ArrayList<LibraryFile> fileList = new ArrayList<LibraryFile>();
        
        if (!directory.exists() | !directory.isDirectory()) {
            return fileList;
        }
        
        File libraryDir = ArmourersWorkshop.getProxy().getSkinLibraryDirectory();
        
        
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
                String path = templateFiles[i].getPath().replace(templateFiles[i].getName(), "");
                path = path.replace(libraryDir.getPath(), "");
                ISkinType skinType = SkinIOUtils.getSkinTypeNameFromFile(templateFiles[i]);
                if (skinType != null) {
                    fileList.add(new LibraryFile(cleanName, path, skinType));
                }
            }
            else {
                if (templateFiles[i].isDirectory()) {
                    
                    String name = templateFiles[i].getName();
                    String path = templateFiles[i].getParent() + "/";
                    path = path.replace(ArmourersWorkshop.getProxy().getSkinLibraryDirectory().getPath(), "");
                    path = path.replace("\\", "/");
                    
                    if (!name.equals("private")) {
                        fileList.add(new LibraryFile(templateFiles[i].getName(), path, null, true));
                    }
                }
            }
        }
        Collections.sort(fileList);
        
        if (subDirectories) {
            for (int i = 0; i < templateFiles.length; i++) {
                if (templateFiles[i].isDirectory()) {
                    fileList.addAll(getSkinFilesInDirectory(templateFiles[i], true));
                }
            }
        }
        
        
        return fileList;
    }
}
