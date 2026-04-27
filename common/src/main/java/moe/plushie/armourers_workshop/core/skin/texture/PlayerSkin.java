package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.ModTextures;
import org.jetbrains.annotations.Nullable;

public class PlayerSkin {

    public static final PlayerSkin DEFAULT = new PlayerSkin(ModTextures.MANNEQUIN_DEFAULT, null, null, PlayerSkinModel.WIDE);

    private final PlayerSkinDescriptor descriptor;
    private final PlayerSkinPart body;
    private final PlayerSkinPart cape;
    private final PlayerSkinPart elytra;
    private final PlayerSkinModel model;

    public PlayerSkin(OpenResourceKey body, @Nullable OpenResourceKey cape, @Nullable OpenResourceKey elytra, PlayerSkinModel model) {
        this(PlayerSkinDescriptor.DEFAULT.withModel(model), new PlayerSkinPart(body), Objects.flatMap(cape, PlayerSkinPart::new), Objects.flatMap(elytra, PlayerSkinPart::new), model);
    }

    public PlayerSkin(PlayerSkinDescriptor descriptor, PlayerSkinPart body, PlayerSkinPart cape, PlayerSkinPart elytra, PlayerSkinModel model) {
        this.descriptor = descriptor;
        this.body = body;
        this.cape = cape;
        this.elytra = elytra;
        this.model = model;
    }

    public PlayerSkin withModel(PlayerSkinModel model) {
        if (this.descriptor.model() != model || this.model != model) {
            return new PlayerSkin(descriptor.withModel(model), body, cape, elytra, model);
        }
        return this;
    }

    public PlayerSkinDescriptor descriptor() {
        return descriptor;
    }

    public PlayerSkinPart body() {
        return body;
    }

    @Nullable
    public PlayerSkinPart cape() {
        return cape;
    }

    @Nullable
    public PlayerSkinPart elytra() {
        return elytra;
    }

    public PlayerSkinModel model() {
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayerSkin that)) return false;
        return model == that.model && Objects.equals(body, that.body) && Objects.equals(cape, that.cape) && Objects.equals(elytra, that.elytra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, body, cape, elytra);
    }

    @Override
    public String toString() {
        var body1 = Objects.flatMap(body, PlayerSkinPart::texture);
        var cape1 = Objects.flatMap(cape, PlayerSkinPart::texture);
        var elytra1 = Objects.flatMap(elytra, PlayerSkinPart::texture);
        return Objects.toString(this, "body", body1, "cape", cape1, "elytra", elytra1, "model", model);
    }
}
