package riskyken.armourersWorkshop.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
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
                .getCategory(ConfigHandler.CATEGORY_COMPATIBILITY))
                .getChildElements());
        /*
        configs.addAll(new ConfigElement(ConfigHandler.config
                .getCategory(ConfigHandlerClient.CATEGORY_CLIENT))
                .getChildElements());
        */
        return configs;
    }
}
