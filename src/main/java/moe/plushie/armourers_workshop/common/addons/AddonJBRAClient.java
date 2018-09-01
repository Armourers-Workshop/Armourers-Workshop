package moe.plushie.armourers_workshop.common.addons;

public class AddonJBRAClient extends ModAddon {

    public AddonJBRAClient() {
        super(null, "JRBA Client");
    }
    
    @Override
    protected boolean setIsModLoaded() {
        try {
            Class.forName("JinRyuu.JBRA.JBRA");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
