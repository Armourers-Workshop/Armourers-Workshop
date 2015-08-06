package riskyken.plushieWrapper.proxies;

import riskyken.plushieWrapper.client.RenderBridge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    @Override
    public void preInit() {
        RenderBridge.init();
    }
    
    @Override
    public void init() {
    }
    
    
    @Override
    public void postInit() {
    }
}
