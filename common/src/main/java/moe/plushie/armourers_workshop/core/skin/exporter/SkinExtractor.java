package moe.plushie.armourers_workshop.core.skin.exporter;

//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//import moe.plushie.armourers_workshop.core.utils.SkinLogger;
//import org.apache.logging.log4j.Level;
//
//import moe.plushie.armourers_workshop.ArmourersWorkshop;
//import moe.plushie.armourers_workshop.core.config.ConfigHandler;
//import moe.plushie.armourers_workshop.utils.ModLogger;
//
// /**
//  * Extracts skins from the jar file and places them into the library folder.
//  * @author RiskyKen
//  *
//  */
//public final class SkinExtractor {
//
//    private SkinExtractor() {
//    }
//
//    private static final String SKINS_ASSETS_LOCATION = "assets/armourers_workshop/skins/";
//    private static final String[] SKIN_FILES = {
//        "Angel Wings",
//        "Arbalest",
//        "Arrow",
//        "Barrel",
//        "Bat Wings",
//        "Butterfly Wings",
//        "Chessboard",
//        "Dress Shoes",
//        "Dress Skirt",
//        "Dress Top",
//        "Evil Wings",
//        "Fez",
//        "Fox Ears",
//        "Glass Chair",
//        "Glass Table",
//        "Halo",
//        "Head Bow Left",
//        "Head Bow Right",
//        "Lightsaber (Dual)",
//        "Lightsaber",
//        "Madoka's Head Bows",
//        "Madoka's Shoes",
//        "Madoka's Skirt",
//        "Madoka's Top",
//        "Pika Hood",
//        "Pika Pants",
//        "Pika Paws",
//        "Pika T",
//        "Robot Key",
//        "Sakura's Cat Boots",
//        "Sakura's Cat Chest",
//        "Sakura's Cat Ears",
//        "Sakura's Cat Skirt",
//        "Sakura's Chest",
//        "Sakura's Shoes",
//        "Sakura's Skirt",
//        "Scythe",
//        "Viking Helmet (Blood)",
//        "Viking Helmet",
//        "Witch's Boots",
//        "Witch's Hat",
//        "Witch's Robes",
//        "Witch's Skirt",
//    };
//
//    public static void extractSkins() {
//        if (!ConfigHandler.extractOfficialSkins) {
//            return;
//        }
//
//        for (int i = 0; i < SKIN_FILES.length; i++) {
//            extractSkin(SKIN_FILES[i]);
//        }
//    }
//
//    private static void extractSkin(String fileName) {
//        InputStream input = null;
//        FileOutputStream output = null;
//
//        File outputDir = new File(ArmourersWorkshop.getProxy().getSkinLibraryDirectory(), "official");
//
//        if (!outputDir.exists()) {
//            outputDir.mkdirs();
//        }
//
//        File outputFile = new File(outputDir, fileName + ".armour");
//
//
//
//        if (outputFile.exists()) {
//            if (getFileSize(outputFile) > 0) {
//                return;
//            } else {
//                SkinLogger.log("Deleting corrupted skin file " + fileName);
//                outputFile.delete();
//            }
//        }
//
//        try {
//            SkinLogger.log("Extracting file " + fileName);
//            input = SkinExtractor.class.getClassLoader().getResourceAsStream(SKINS_ASSETS_LOCATION + fileName + ".armour");
//            if (input != null) {
//                output = new FileOutputStream(outputFile);
//                while (input.available() > 0) {
//                    output.write(input.read());
//                }
//                output.flush();
//            } else {
//                SkinLogger.error("Error extracting skin " + fileName);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            StreamUtils.closeQuietly(input);
//            StreamUtils.closeQuietly(output);
//        }
//    }
//
//    private static long getFileSize(File file) {
//        return file.length();
//    }
//}
