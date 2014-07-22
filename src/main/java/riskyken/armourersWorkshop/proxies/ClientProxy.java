package riskyken.armourersWorkshop.proxies;

import cpw.mods.fml.client.registry.ClientRegistry;
import riskyken.armourersWorkshop.client.render.RenderBlockArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerChest;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void init() {}
	
	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmourerChest.class, new RenderBlockArmourer());
	}
	
	@Override
	public void postInit() {}
	
}