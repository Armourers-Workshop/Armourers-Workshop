package moe.plushie.armourers_workshop.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.common.exception.NewerFileVersionException;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinSerializer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.DimensionManager;

public final class SkinIOUtils {

    public static final String SKIN_FILE_EXTENSION = ".armour";

    public static boolean saveSkinFromFileName(String filePath, String fileName, Skin skin) {
        filePath = makeFilePathValid(filePath);
        fileName = makeFileNameValid(fileName);
        File file = new File(ArmourersWorkshop.getProxy().getSkinLibraryDirectory(), filePath + fileName);
        return saveSkinToFile(file, skin);
    }

    public static boolean saveSkinToFile(File file, Skin skin) {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            saveSkinToStream(fos, skin);
            fos.flush();
        } catch (FileNotFoundException e) {
            ModLogger.log(Level.WARN, "Skin file not found.");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Skin file save failed.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveSkinToStream(OutputStream outputStream, Skin skin) {
        try (BufferedOutputStream bos = new BufferedOutputStream(outputStream); DataOutputStream dos = new DataOutputStream(bos)) {
            SkinSerializer.writeToStream(skin, dos);
            dos.flush();
            bos.flush();
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Skin file save failed.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Skin loadSkinFromLibraryFile(LibraryFile libraryFile) {
        return loadSkinFromFileName(libraryFile.getFullName() + SKIN_FILE_EXTENSION);
    }

    public static Skin loadSkinFromFileName(String fileName) {
        File file = new File(ArmourersWorkshop.getProxy().getSkinLibraryDirectory(), fileName);
        if (!isInSubDirectory(ArmourersWorkshop.getProxy().getSkinLibraryDirectory(), file)) {
            ModLogger.log(Level.WARN, "Player tried to load a file in a invalid location.");
            ModLogger.log(Level.WARN, String.format("The file was: %s", file.getAbsolutePath().replace("%", "")));
            return null;
        }
        return loadSkinFromFile(file);
    }

    public static Skin loadSkinFromFile(File file) {
        Skin skin = null;

        try (FileInputStream fis = new FileInputStream(file)) {
            skin = loadSkinFromStream(fis);
        } catch (FileNotFoundException e) {
            ModLogger.log(Level.WARN, "Skin file not found.");
            ModLogger.log(Level.WARN, file);
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Skin file load failed.");
            e.printStackTrace();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, "Unable to load skin. Unknown error.");
            e.printStackTrace();
        }

        if (skin == null) {
            skin = loadSkinRecovery(file);
            if (skin != null) {
                ModLogger.log(Level.WARN, "Loaded skin with recovery system.");
            }
        }

        return skin;
    }

    public static Skin loadSkinFromStream(InputStream inputStream) {
        Skin skin = null;

        try (BufferedInputStream bis = new BufferedInputStream(inputStream); DataInputStream dis = new DataInputStream(bis)) {
            skin = SkinSerializer.readSkinFromStream(dis);
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Skin file load failed.");
            e.printStackTrace();
        } catch (NewerFileVersionException e) {
            ModLogger.log(Level.ERROR, "Can not load skin file it was saved in newer version.");
            e.printStackTrace();
        } catch (InvalidCubeTypeException e) {
            ModLogger.log(Level.ERROR, "Unable to load skin. Unknown cube types found.");
            e.printStackTrace();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, "Unable to load skin. Unknown error.");
            e.printStackTrace();
        }

        return skin;
    }

    private static Skin loadSkinRecovery(File file) {
        Skin skin = null;
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            ArrayList<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < fileBytes.length; i++) {
                if (fileBytes[i] == 0x0A && fileBytes[i - 1] != 0) {
                    indexes.add(i - 1);
                }
            }
            byte[] newFile = new byte[fileBytes.length - indexes.size()];
            int newFileIndex = 0;
            for (int i = 0; i < fileBytes.length; i++) {
                if (!isInArrayList(i, indexes)) {
                    newFile[newFileIndex] = fileBytes[i];
                    newFileIndex++;
                }
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(newFile);
            skin = loadSkinFromStream(bais);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return skin;
    }

    private static boolean isInArrayList(int index, ArrayList<Integer> list) {
        for (int j = 0; j < list.size(); j++) {
            if (index == list.get(j)) {
                return true;
            }
        }
        return false;
    }

    public static ISkinType getSkinTypeNameFromFile(File file) {
        DataInputStream stream = null;
        ISkinType skinType = null;

        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            skinType = SkinSerializer.readSkinTypeNameFromStream(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ModLogger.log(Level.ERROR, "File name: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            ModLogger.log(Level.ERROR, "File name: " + file.getName());
        } catch (NewerFileVersionException e) {
            e.printStackTrace();
            ModLogger.log(Level.ERROR, "File name: " + file.getName());
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, "Unable to load skin name. Unknown error.");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }

        if (skinType == null) {
            Skin skin = loadSkinRecovery(file);
            if (skin != null) {
                ModLogger.log(Level.WARN, "Loaded skin with recovery system.");
                skinType = skin.getSkinType();
            }
        }

        return skinType;
    }

    public static void makeDatabaseDirectory() {
        File directory = getSkinDatabaseDirectory();
        ModLogger.log("Loading skin database at: " + directory.getAbsolutePath());
        copyGlobalDatabase();
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public static void copyGlobalDatabase() {
        File dirGlobalDatabase = ArmourersWorkshop.getInstance().getProxy().getGlobalSkinDatabaseDirectory();
        if (dirGlobalDatabase.exists()) {
            File dirWorldDatabase = getSkinDatabaseDirectory();
            File[] globalFiles = dirGlobalDatabase.listFiles();
            for (int i = 0; i < globalFiles.length; i++) {
                File globalFile = globalFiles[i];
                File worldFile = new File(dirWorldDatabase, globalFile.getName());
                if (!globalFile.getName().equals("readme.txt") & !worldFile.exists()) {
                    try {
                        FileUtils.copyFile(globalFile, worldFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        createGlobalDatabaseReadme();
    }

    private static void createGlobalDatabaseReadme() {
        File globalDatabaseReadme = new File(ArmourersWorkshop.getInstance().getProxy().getGlobalSkinDatabaseDirectory(), "readme.txt");
        if (!ArmourersWorkshop.getInstance().getProxy().getGlobalSkinDatabaseDirectory().exists()) {
            ArmourersWorkshop.getInstance().getProxy().getGlobalSkinDatabaseDirectory().mkdirs();
        }
        if (!globalDatabaseReadme.exists()) {
            DataOutputStream outputStream = null;
            try {
                String crlf = "\r\n";
                outputStream = new DataOutputStream(new FileOutputStream(globalDatabaseReadme));
                outputStream.writeBytes("Any files placed in this directory will be copied into the skin-database folder of any worlds that are loaded." + crlf);
                outputStream.writeBytes("Please read Info for Map & Mod Pack Makers on the main forum post if you want to know how to use this." + crlf);
                outputStream.writeBytes("http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/2309193-wip-alpha-armourers-workshop-weapon-armour-skins");
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    public static File getSkinDatabaseDirectory() {
        return new File(DimensionManager.getCurrentSaveRootDirectory(), "skin-database");
    }

    public static boolean createDirectory(File file) {
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    public static void recoverSkins(EntityPlayer player) {
        player.sendMessage(new TextComponentString("Starting skin recovery."));
        File skinDir = getSkinDatabaseDirectory();
        if (skinDir.exists() & skinDir.isDirectory()) {
            File recoverDir = new File(System.getProperty("user.dir"), "recovered-skins");
            if (!recoverDir.exists()) {
                recoverDir.mkdirs();
            }
            File[] skinFiles = skinDir.listFiles();
            player.sendMessage(new TextComponentString(String.format("Found %d skins to be recovered.", skinFiles.length)));
            player.sendMessage(new TextComponentString("Working..."));
            int unnamedSkinCount = 0;
            int successCount = 0;
            int failCount = 0;

            for (int i = 0; i < skinFiles.length; i++) {
                File skinFile = skinFiles[i];
                Skin skin = loadSkinFromFile(skinFile);
                if (skin != null) {
                    String fileName = skin.getProperties().getPropertyString("fileName", null);
                    String customName = SkinProperties.PROP_ALL_CUSTOM_NAME.getValue(skin.getProperties());
                    if (!StringUtils.isNullOrEmpty(fileName)) {
                        fileName = makeFileNameValid(fileName);
                        File newSkinFile = new File(recoverDir, fileName + SKIN_FILE_EXTENSION);
                        if (newSkinFile.exists()) {
                            int nameCount = 0;
                            while (true) {
                                nameCount++;
                                newSkinFile = new File(recoverDir, fileName + "-" + nameCount + SKIN_FILE_EXTENSION);
                                if (!newSkinFile.exists()) {
                                    break;
                                }
                            }
                        }
                        saveSkinToFile(newSkinFile, skin);
                        successCount++;
                        continue;
                    }
                    if (!StringUtils.isNullOrEmpty(customName)) {
                        customName = makeFileNameValid(customName);
                        File newSkinFile = new File(recoverDir, customName + SKIN_FILE_EXTENSION);
                        if (newSkinFile.exists()) {
                            int nameCount = 0;
                            while (true) {
                                nameCount++;
                                newSkinFile = new File(recoverDir, customName + "-" + nameCount + SKIN_FILE_EXTENSION);
                                if (!newSkinFile.exists()) {
                                    break;
                                }
                            }
                        }
                        saveSkinToFile(newSkinFile, skin);
                        successCount++;
                        continue;
                    }
                    unnamedSkinCount++;
                    saveSkinToFile(new File(recoverDir, "unnamed-skin-" + unnamedSkinCount + SKIN_FILE_EXTENSION), skin);
                    successCount++;
                } else {
                    failCount++;
                }
            }
            player.sendMessage(new TextComponentString("Finished skin recovery."));
            player.sendMessage(new TextComponentString(String.format("%d skins were recovered and %d fail recovery.", successCount, failCount)));
        } else {
            player.sendMessage(new TextComponentString("No skins found to recover."));
        }
    }

    public static void updateSkins(EntityPlayer player) {
        File updateDir = new File(System.getProperty("user.dir"), "skin-update");
        if (!updateDir.exists() & updateDir.isDirectory()) {
            player.sendMessage(new TextComponentString("Directory skin-update not found."));
            return;
        }

        File outputDir = new File(updateDir, "updated");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        File[] skinFiles = updateDir.listFiles();
        player.sendMessage(new TextComponentString(String.format("Found %d skins to be updated.", skinFiles.length)));
        player.sendMessage(new TextComponentString("Working..."));
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < skinFiles.length; i++) {
            File skinFile = skinFiles[i];
            if (skinFile.isFile()) {
                Skin skin = loadSkinFromFile(skinFile);
                if (skin != null) {
                    if (saveSkinToFile(new File(outputDir, skinFile.getName()), skin)) {
                        successCount++;
                    } else {
                        ModLogger.log(Level.ERROR, "Failed to update skin " + skinFile.getName());
                        failCount++;
                    }
                } else {
                    ModLogger.log(Level.ERROR, "Failed to update skin " + skinFile.getName());
                    failCount++;
                }
            }
        }
        player.sendMessage(new TextComponentString("Finished skin update."));
        player.sendMessage(new TextComponentString(String.format("%d skins were updated and %d failed.", successCount, failCount)));
    }

    public static boolean isInSubDirectory(File dir, File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            // return true;
        }
        if (file.getParentFile().equals(dir)) {
            return true;
        }
        return isInSubDirectory(dir, file.getParentFile());
    }

    public static String makeFileNameValid(String fileName) {
        fileName = fileName.replaceAll("[<>:\"/\\\\|?*]", "_");
        return fileName;
    }

    public static String makeFilePathValid(String filePath) {
        filePath = filePath.replace("\\", "/");
        filePath = filePath.replace("../", "_");
        filePath = filePath.replaceAll("[<>:\"|?*]", "_");
        return filePath;
    }

    public static boolean isInLibraryDir(File file) {
        return isInSubDirectory(file, ArmourersWorkshop.getProxy().getSkinLibraryDirectory());
    }
}
