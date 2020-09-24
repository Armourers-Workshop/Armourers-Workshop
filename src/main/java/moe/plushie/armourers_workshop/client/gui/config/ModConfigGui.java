package moe.plushie.armourers_workshop.client.gui.config;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModConfigGui extends GuiConfig {

    public ModConfigGui(GuiScreen parent) {
        super(parent, makeConfigScreens(), LibModInfo.ID, false, false, GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
    }

    public static List<IConfigElement> makeConfigScreens() {
        List<IConfigElement> configs = new ArrayList<IConfigElement>();
        configs.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CATEGORY_GENERAL)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CATEGORY_WARDROBE)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CATEGORY_LIBRARY)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CATEGORY_RECIPE)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CATEGORY_HOLIDAY)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CATEGORY_ENTITY_SKINS)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandler.config.getCategory(ConfigHandler.CATEGORY_CACHE)).getChildElements());
        
        configs.addAll(new ConfigElement(ConfigHandlerClient.config.getCategory(ConfigHandlerClient.CATEGORY_MISC)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandlerClient.config.getCategory(ConfigHandlerClient.CATEGORY_PERFORMANCE)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandlerClient.config.getCategory(ConfigHandlerClient.CATEGORY_CACHE)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandlerClient.config.getCategory(ConfigHandlerClient.CATEGORY_SKIN_PREVIEW)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandlerClient.config.getCategory(ConfigHandlerClient.CATEGORY_TOOLTIP)).getChildElements());
        configs.addAll(new ConfigElement(ConfigHandlerClient.config.getCategory(ConfigHandlerClient.CATEGORY_DEBUG)).getChildElements());
        return configs;
    }
}
