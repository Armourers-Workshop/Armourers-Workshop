package riskyken.armourersWorkshop.common.addons;

public class AddonShaders extends ModAddon {

    public AddonShaders() {
        super(null, "Shaders Mod");
    }

    @Override
    protected boolean setIsModLoaded() {
        try {
            Class.forName("shadersmodcore.client.Shaders");
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
