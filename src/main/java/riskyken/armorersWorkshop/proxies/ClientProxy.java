package riskyken.armorersWorkshop.proxies;

import cpw.mods.fml.client.registry.ClientRegistry;
import riskyken.armorersWorkshop.client.render.RenderBlockArmorer;
import riskyken.armorersWorkshop.common.tileentities.TileEntityArmorerChest;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void init() {}
	
	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArmorerChest.class, new RenderBlockArmorer());
	}
	
	@Override
	public void postInit() {}
	
}