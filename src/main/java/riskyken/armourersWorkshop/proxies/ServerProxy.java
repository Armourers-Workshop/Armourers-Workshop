package riskyken.armourersWorkshop.proxies;

import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;

public class ServerProxy extends CommonProxy {

    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
    }

    @Override
    public void postInit() {
    }

    @Override
    public void addCustomArmour(String playerName, CustomArmourItemData armourData) {
    }

    @Override
    public void removeCustomArmour(String playerName, ArmourerType type) {
    }

    @Override
    public void removeAllCustomArmourData(String playerName) {
    }

    @Override
    public boolean playerHasSkirt(String playerName) {
        return false;
    }
}
