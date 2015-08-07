package riskyken.armourersWorkshop.common.skin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

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
        "Arbalest Bow",
        "Fez",
        "Fox Ears",
        "Halo",
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
            return;
        }
        
        try {
            ModLogger.log("Extracting file " + fileName);
            input = SkinExtractor.class.getClassLoader().getSystemResourceAsStream(SKINS_ASSETS_LOCATION + fileName + ".armour");
            output = new FileOutputStream(outputFile);
            while (input.available() > 0) {
                output.write(input.read());
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }
}
