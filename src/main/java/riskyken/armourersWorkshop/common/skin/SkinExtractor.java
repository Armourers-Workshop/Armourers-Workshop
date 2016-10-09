package riskyken.armourersWorkshop.common.skin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

/**
 * Extracts skins from the jar file and places them into the library folder.
 * @author RiskyKen
 *
 */
public final class SkinExtractor {
    
    private SkinExtractor() {
    }
    
    private static final String SKINS_ASSETS_LOCATION = "assets/armourersworkshop/skins/";
    private static final String[] SKIN_FILES = {
        "Angel Wings",
        "Arbalest",
        "Arrow",
        "Barrel",
        "Bat Wings",
        "Butterfly Wings",
        "Chessboard",
        "Dress Shoes",
        "Dress Skirt",
        "Dress Top",
        "Evil Wings",
        "Fez",
        "Fox Ears",
        "Glass Chair",
        "Glass Table",
        "Halo",
        "Head Bow Left",
        "Head Bow Right",
        "Lightsaber (Dual)",
        "Lightsaber",
        "Madoka's Head Bows",
        "Madoka's Shoes",
        "Madoka's Skirt",
        "Madoka's Top",
        "Pika Hood",
        "Pika Pants",
        "Pika Paws",
        "Pika T",
        "Robot Key",
        "Sakura's Cat Boots",
        "Sakura's Cat Chest",
        "Sakura's Cat Ears",
        "Sakura's Cat Skirt",
        "Sakura's Chest",
        "Sakura's Shoes",
        "Sakura's Skirt",
        "Scythe",
        "Viking Helmet (Blood)",
        "Viking Helmet",
        "Witch's Boots",
        "Witch's Hat",
        "Witch's Robes",
        "Witch's Skirt",
    };
    
    public static void extractSkins() {
        if (!ConfigHandler.extractOfficialSkins) {
            return;
        }
        
        for (int i = 0; i < SKIN_FILES.length; i++) {
            extractSkin(SKIN_FILES[i]);
        }
    }
    
    private static void extractSkin(String fileName) {
        InputStream input = null;
        FileOutputStream output = null;
        File outputFile = new File(SkinIOUtils.getSkinLibraryDirectory(), fileName + ".armour");
        if (outputFile.exists()) {
            if (getFileSize(outputFile) > 0) {
                return;
            } else {
                ModLogger.log("Deleting corrupted skin file " + fileName);
                outputFile.delete();
            }
        }
        
        try {
            ModLogger.log("Extracting file " + fileName);
            input = SkinExtractor.class.getClassLoader().getResourceAsStream(SKINS_ASSETS_LOCATION + fileName + ".armour");
            if (input != null) {
                output = new FileOutputStream(outputFile);
                while (input.available() > 0) {
                    output.write(input.read());
                }
                output.flush();
            } else {
                ModLogger.log(Level.ERROR, "Error extracting skin " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }
    
    private static long getFileSize(File file) {
        return file.length();
    }
}
