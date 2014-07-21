package riskyken.armorersWorkshop.common;

import riskyken.armorersWorkshop.common.config.ConfigHandler;
import riskyken.armorersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ModFMLEventHandler {
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if(eventArgs.modID.equals(LibModInfo.ID)) {
			ConfigHandler.loadConfigFile();
		}
	}
	
}
