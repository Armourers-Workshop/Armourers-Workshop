package moe.plushie.armourers_workshop.common.addons;

public class AddonJEI extends ModAddon {

    public AddonJEI() {
        super("jei", "Just Enough Items");
    }

    public boolean isVisible() {
        if (isModLoaded()) {
            try {
                Class<?> c = Class.forName("mezz.jei.config.Config");
                Object object = c.getMethod("isOverlayEnabled").invoke(null);
                if (object != null && object instanceof Boolean) {
                    return !(Boolean) object;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
