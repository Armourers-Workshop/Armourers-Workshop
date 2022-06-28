package moe.plushie.armourers_workshop.utils;

import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.exception.NewerFileVersionException;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.StringTextComponent;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public final class SkinIOUtils {

//    public static boolean saveSkinFromFileName(String filePath, String fileName, Skin skin) {
//        filePath = makeFilePathValid(filePath);
//        fileName = makeFileNameValid(fileName);
//        File file = new File(ArmourersWorkshop.getSkinLibraryDirectory(), filePath + fileName);
//        return saveSkinToFile(file, skin);
//    }

    public static boolean saveSkinToFile(File file, Skin skin) {
        ModLog.debug("save skin into '{}'", file);
        try {
            SkinFileUtils.forceMkdirParent(file);
            if (file.exists()) {
                SkinFileUtils.deleteQuietly(file);
            }
            FileOutputStream fos = new FileOutputStream(file);
            saveSkinToStream(fos, skin);
            fos.close();
        } catch (FileNotFoundException e) {
            ModLog.warn("skin file not found.");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            ModLog.error("skin file save failed.");
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
            ModLog.error("Skin file save failed.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    public static Skin loadSkinFromLibraryFile(LibraryFile libraryFile) {
//        return loadSkinFromFileName(libraryFile.getFullName() + SKIN_FILE_EXTENSION);
//    }

//    public static Skin loadSkinFromFileName(String fileName) {
//        File file = new File(ArmourersWorkshop.getSkinLibraryDirectory(), fileName);
//        if (!isInSubDirectory(ArmourersWorkshop.getSkinLibraryDirectory(), file)) {
//            AWLog.warn("Player tried to load a file in a invalid location.");
//            AWLog.warn(String.format("The file was: %s", file.getAbsolutePath().replace("%", "")));
//            return null;
//        }
//        return loadSkinFromFile(file);
//    }

    public static Skin loadSkinFromFile(File file) {
        Skin skin = null;

        try (FileInputStream fis = new FileInputStream(file)) {
            skin = loadSkinFromStream(fis);
        } catch (FileNotFoundException e) {
            ModLog.warn("Skin file not found. {}", file);
        } catch (IOException e) {
            ModLog.error("Skin file load failed.");
            e.printStackTrace();
        } catch (Exception e) {
            ModLog.error("Unable to load skin. Unknown error.");
            e.printStackTrace();
        }

        if (skin == null) {
            skin = loadSkinRecovery(file);
            if (skin != null) {
                ModLog.warn("Loaded skin with recovery system.");
            }
        }

        return skin;
    }

    public static Skin loadSkinFromStream(InputStream inputStream) {
        Skin skin = null;
        try {
            skin = loadSkinFromStream2(inputStream);
        } catch (IOException e) {
            ModLog.error("Skin file load failed.");
            e.printStackTrace();
        } catch (NewerFileVersionException e) {
            ModLog.error("Can not load skin file it was saved in newer version.");
            e.printStackTrace();
        } catch (InvalidCubeTypeException e) {
            ModLog.error("Unable to load skin. Unknown cube types found.");
            e.printStackTrace();
        } catch (Exception e) {
            ModLog.error("Unable to load skin. Unknown error.");
            e.printStackTrace();
        }

        return skin;
    }

    public static Skin loadSkinFromStream2(InputStream inputStream) throws Exception {
        if (inputStream == null) {
            return null;
        }
        Skin skin = null;
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        DataInputStream dis = new DataInputStream(bis);
        try {
            skin = SkinSerializer.readSkinFromStream(dis);
        } finally {
            StreamUtils.closeQuietly(dis, bis);
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

    public static Pair<ISkinType, ISkinProperties>  getTypeNameFromFile(File file) {
        DataInputStream stream = null;
        Pair<ISkinType, ISkinProperties> skinType = null;

        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            skinType = SkinSerializer.readSkinTypeNameFromStream(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ModLog.error("File name: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            ModLog.error("File name: " + file.getName());
        } catch (NewerFileVersionException e) {
            e.printStackTrace();
            ModLog.error("File name: " + file.getName());
        } catch (Exception e) {
            ModLog.error("Unable to load skin name. Unknown error.");
            e.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(stream);
        }

        if (skinType == null) {
            Skin skin = loadSkinRecovery(file);
            if (skin != null) {
                ModLog.warn("Loaded skin with recovery system.");
                skinType = Pair.of(skin.getType(), new SkinProperties());
            }
        }

        return skinType;
    }

//    public static void makeDatabaseDirectory() {
//        File directory = getSkinDatabaseDirectory();
//        AWLog.debug("Loading skin database at: " + directory.getAbsolutePath());
//        copyGlobalDatabase();
//        if (!directory.exists()) {
//            directory.mkdir();
//        }
//    }

//    public static void copyGlobalDatabase() {
//        File dirGlobalDatabase = ArmourersWorkshop.getGlobalSkinDatabaseDirectory();
//        if (dirGlobalDatabase.exists()) {
//            File dirWorldDatabase = getSkinDatabaseDirectory();
//            File[] globalFiles = dirGlobalDatabase.listFiles();
//            for (File globalFile : globalFiles) {
//                File worldFile = new File(dirWorldDatabase, globalFile.getName());
//                if (!globalFile.getName().equals("readme.txt") & !worldFile.exists()) {
//                    try {
//                        FileManager.copyFile(globalFile, worldFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        createGlobalDatabaseReadme();
//    }
//
//    private static void createGlobalDatabaseReadme() {
//        File globalDatabaseReadme = new File(ArmourersWorkshop.getGlobalSkinDatabaseDirectory(), "readme.txt");
//        if (!ArmourersWorkshop.getGlobalSkinDatabaseDirectory().exists()) {
//            ArmourersWorkshop.getGlobalSkinDatabaseDirectory().mkdirs();
//        }
//        if (!globalDatabaseReadme.exists()) {
//            DataOutputStream outputStream = null;
//            try {
//                String crlf = "\r\n";
//                outputStream = new DataOutputStream(new FileOutputStream(globalDatabaseReadme));
//                outputStream.writeBytes("Any files placed in this directory will be copied into the skin-database folder of any worlds that are loaded." + crlf);
//                outputStream.writeBytes("Please read Info for Map & Mod Pack Makers on the main forum post if you want to know how to use this." + crlf);
//                outputStream.writeBytes("http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/2309193-wip-alpha-armourers-workshop-weapon-armour-skins");
//                outputStream.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                StreamUtils.closeQuietly(outputStream);
//            }
//        }
//    }

    public static File getSkinDatabaseDirectory() {
        return null;
//        return new File(DimensionManager.getCurrentSaveRootDirectory(), "skin-database");
    }

    public static boolean createDirectory(File file) {
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    public static void recoverSkins(PlayerEntity player) {
        player.sendMessage(new StringTextComponent("Starting skin recovery."), player.getUUID());
        File skinDir = getSkinDatabaseDirectory();
        if (skinDir.exists() & skinDir.isDirectory()) {
            File recoverDir = new File(System.getProperty("user.dir"), "recovered-skins");
            if (!recoverDir.exists()) {
                recoverDir.mkdirs();
            }
            File[] skinFiles = skinDir.listFiles();
            player.sendMessage(new StringTextComponent(String.format("Found %d skins to be recovered.", skinFiles.length)), player.getUUID());
            player.sendMessage(new StringTextComponent("Working..."), player.getUUID());
            int unnamedSkinCount = 0;
            int successCount = 0;
            int failCount = 0;

            for (int i = 0; i < skinFiles.length; i++) {
                File skinFile = skinFiles[i];
                Skin skin = loadSkinFromFile(skinFile);
                if (skin != null) {
                    SkinProperties properties = skin.getProperties();

                    String fileName = properties.get(null);// skin.getProperties().getPropertyString("fileName", null);
                    String customName = properties.get(SkinProperty.ALL_CUSTOM_NAME);
                    if (!StringUtils.isNullOrEmpty(fileName)) {
                        fileName = makeFileNameValid(fileName);
                        File newSkinFile = new File(recoverDir, fileName + AWConstants.EXT);
                        if (newSkinFile.exists()) {
                            int nameCount = 0;
                            do {
                                nameCount++;
                                newSkinFile = new File(recoverDir, fileName + "-" + nameCount + AWConstants.EXT);
                            } while (newSkinFile.exists());
                        }
                        saveSkinToFile(newSkinFile, skin);
                        successCount++;
                        continue;
                    }
                    if (!StringUtils.isNullOrEmpty(customName)) {
                        customName = makeFileNameValid(customName);
                        File newSkinFile = new File(recoverDir, customName + AWConstants.EXT);
                        if (newSkinFile.exists()) {
                            int nameCount = 0;
                            do {
                                nameCount++;
                                newSkinFile = new File(recoverDir, customName + "-" + nameCount + AWConstants.EXT);
                            } while (newSkinFile.exists());
                        }
                        saveSkinToFile(newSkinFile, skin);
                        successCount++;
                        continue;
                    }
                    unnamedSkinCount++;
                    saveSkinToFile(new File(recoverDir, "unnamed-skin-" + unnamedSkinCount + AWConstants.EXT), skin);
                    successCount++;
                } else {
                    failCount++;
                }
            }
            player.sendMessage(new StringTextComponent("Finished skin recovery."), player.getUUID());
            player.sendMessage(new StringTextComponent(String.format("%d skins were recovered and %d fail recovery.", successCount, failCount)), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("No skins found to recover."), player.getUUID());
        }
    }

    public static void updateSkins(PlayerEntity player) {
        File updateDir = new File(System.getProperty("user.dir"), "skin-update");
        if (!updateDir.exists() & updateDir.isDirectory()) {
            player.sendMessage(new StringTextComponent("Directory skin-update not found."), player.getUUID());
            return;
        }

        File outputDir = new File(updateDir, "updated");
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        File[] skinFiles = updateDir.listFiles();
        player.sendMessage(new StringTextComponent(String.format("Found %d skins to be updated.", skinFiles.length)), player.getUUID());
        player.sendMessage(new StringTextComponent("Working..."), player.getUUID());
        int successCount = 0;
        int failCount = 0;

        for (File skinFile : skinFiles) {
            if (skinFile.isFile()) {
                Skin skin = loadSkinFromFile(skinFile);
                if (skin != null) {
                    if (saveSkinToFile(new File(outputDir, skinFile.getName()), skin)) {
                        successCount++;
                    } else {
                        ModLog.error("Failed to update skin " + skinFile.getName());
                        failCount++;
                    }
                } else {
                    ModLog.error("Failed to update skin " + skinFile.getName());
                    failCount++;
                }
            }
        }
        player.sendMessage(new StringTextComponent("Finished skin update."), player.getUUID());
        player.sendMessage(new StringTextComponent(String.format("%d skins were updated and %d failed.", successCount, failCount)), player.getUUID());
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

//    public static boolean isInLibraryDir(File file) {
//        return isInSubDirectory(file, ArmourersWorkshop.getSkinLibraryDirectory());
//    }
}
