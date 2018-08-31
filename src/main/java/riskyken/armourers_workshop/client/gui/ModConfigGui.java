package riskyken.armourers_workshop.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import riskyken.armourers_workshop.common.config.ConfigHandler;
import riskyken.armourers_workshop.common.config.ConfigHandlerClient;
import riskyken.armourers_workshop.common.lib.LibModInfo;

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
                .getCategory(ConfigHandler.CATEGORY_RECIPE))
                .getChildElements());
        
        configs.addAll(new ConfigElement(ConfigHandler.config
                .getCategory(ConfigHandler.CATEGORY_SERVER))
                .getChildElements());
        
        configs.addAll(new ConfigElement(ConfigHandler.config
                .getCategory(ConfigHandler.CATEGORY_COMPATIBILITY))
                .getChildElements());
        
        configs.addAll(new ConfigElement(ConfigHandler.config
                .getCategory(ConfigHandler.CATEGORY_ENTITY_SKINS))
                .getChildElements());
        
        configs.addAll(new ConfigElement(ConfigHandlerClient.config
                .getCategory(ConfigHandlerClient.CATEGORY_CLIENT))
                .getChildElements());
        
        configs.addAll(new ConfigElement(ConfigHandlerClient.config
                .getCategory(ConfigHandlerClient.CATEGORY_SKIN_PREVIEW))
                .getChildElements());
        
        configs.addAll(new ConfigElement(ConfigHandlerClient.config
                .getCategory(ConfigHandlerClient.CATEGORY_DEBUG))
                .getChildElements());
        
        return configs;
    }
}
