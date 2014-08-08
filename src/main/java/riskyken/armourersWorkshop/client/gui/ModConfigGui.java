package riskyken.armourersWorkshop.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class ModConfigGui extends GuiConfig {

    public ModConfigGui(GuiScreen parent) {
        super(parent, makeConfigScreens(), LibModInfo.ID, false, false, GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
    }

    public static List<IConfigElement> makeConfigScreens() {
        List<IConfigElement> configs = new ArrayList<IConfigElement>();
        configs.addAll(new ConfigElement(ConfigHandler.config
                .getCategory(ConfigHandler.CATEGORY_GENERAL))
                .getChildElements());
        configs.addAll(new ConfigElement(ConfigHandler.config
                .getCategory(ConfigHandler.CATEGORY_MISC)).getChildElements());
        return configs;
    }
}
