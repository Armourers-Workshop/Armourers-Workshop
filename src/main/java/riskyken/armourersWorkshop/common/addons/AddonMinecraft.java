package riskyken.armourersWorkshop.common.addons;

import net.minecraft.init.Items;
import net.minecraftforge.client.MinecraftForgeClient;
import riskyken.armourersWorkshop.client.render.item.RenderItemBowSkin;
import riskyken.armourersWorkshop.client.render.item.RenderItemSwordSkin;
import riskyken.armourersWorkshop.utils.ModLogger;

public class AddonMinecraft extends AbstractAddon {

    public AddonMinecraft() {
        ModLogger.log("Loading Minecraft Compatibility Addon");
    }
    
    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
        MinecraftForgeClient.registerItemRenderer(Items.wooden_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.stone_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.iron_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.golden_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.diamond_sword, new RenderItemSwordSkin());
        MinecraftForgeClient.registerItemRenderer(Items.bow, new RenderItemBowSkin());
    }

    @Override
    public String getModName() {
        return "Minecraft";
    }

}
