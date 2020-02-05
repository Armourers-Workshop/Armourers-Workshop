package moe.plushie.armourers_workshop.client.gui.style;

import java.io.IOException;
import java.util.HashMap;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;

import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SerializeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiResourceManager implements IResourceManagerReloadListener {

    private static final HashMap<ResourceLocation, GuiStyle> GUI_RESOURCE_MAP = new HashMap<ResourceLocation, GuiStyle>();
    private static final String GUI_ASSETS_LOCATION = "%s:assets/%s";

    public GuiResourceManager() {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        if (resourceManager instanceof SimpleReloadableResourceManager) {
            ((SimpleReloadableResourceManager) resourceManager).registerReloadListener(this);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        ModLogger.log("Loading GUI resources.");
        synchronized (GUI_RESOURCE_MAP) {
            GUI_RESOURCE_MAP.clear();
        }
    }

    public static GuiStyle getGuiJsonInfo(ResourceLocation resourceLocation) {
        synchronized (GUI_RESOURCE_MAP) {
            if (!GUI_RESOURCE_MAP.containsKey(resourceLocation)) {
                GUI_RESOURCE_MAP.put(resourceLocation, loadGuiJsonInfo(resourceLocation));
            }
            return GUI_RESOURCE_MAP.get(resourceLocation);
        }
    }

    private static GuiStyle loadGuiJsonInfo(ResourceLocation resourceLocation) {
        GuiStyle guiStyle = new GuiStyle();
        
        String path = String.format(GUI_ASSETS_LOCATION, resourceLocation.getNamespace(), resourceLocation.getPath());
        try {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
            String data = SerializeHelper.readFile(resource.getInputStream(), Charsets.UTF_8);
            JsonElement jsonElement = SerializeHelper.stringToJson(data);
            guiStyle = GuiStyleSerializer.deserializeJson(jsonElement);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        /*try (InputStream inputStream = GuiResourceManager.class.getClassLoader().getResourceAsStream(path)) {
            String data = SerializeHelper.readFile(inputStream, Charsets.UTF_8);
            JsonElement jsonElement = SerializeHelper.stringToJson(data);
            guiStyle = GuiStyleSerializer.deserializeJson(jsonElement);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return guiStyle;
    }
}
