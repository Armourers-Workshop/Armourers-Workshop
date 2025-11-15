package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModTextures;

@SuppressWarnings("unused")
public class AbstractPlayerSkin extends AbstractPlayerSkinImpl {

    private static final AbstractPlayerSkin DEFAULT_SKIN = new AbstractPlayerSkin(ModTextures.MANNEQUIN_DEFAULT, null, null, EntityTextureDescriptor.Model.WIDE);

    private final OpenResourceLocation body;
    private final OpenResourceLocation cape;
    private final OpenResourceLocation elytra;
    private final EntityTextureDescriptor.Model model;

    public AbstractPlayerSkin(OpenResourceLocation body, OpenResourceLocation cape, OpenResourceLocation elytra, EntityTextureDescriptor.Model model) {
        this.body = body;
        this.cape = cape;
        this.elytra = elytra;
        this.model = model;
    }

    public static AbstractPlayerSkin getDefaultSkin() {
        return DEFAULT_SKIN;
    }

    public OpenResourceLocation body() {
        return body;
    }

    public OpenResourceLocation cape() {
        return cape;
    }

    public OpenResourceLocation elytra() {
        return elytra;
    }

    public EntityTextureDescriptor.Model model() {
        return model;
    }
}
