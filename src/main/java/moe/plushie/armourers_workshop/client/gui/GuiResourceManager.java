package moe.plushie.armourers_workshop.client.gui;

import java.util.HashMap;
import java.util.function.Predicate;

import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiResourceManager implements IResourceManagerReloadListener {

	//private static final HashMap<ResourceLocation, IJsonGui> GUI_RESOURCE_MAP = new HashMap<ResourceLocation, IJsonGui>();
	
	public GuiResourceManager() {
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        if (resourceManager instanceof SimpleReloadableResourceManager) {
        	((SimpleReloadableResourceManager)resourceManager).registerReloadListener(this);
        }
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		ModLogger.log("Loading GUI resources.");
	}
	
	/*public static interface IJsonGui {
		
	}*/
}
