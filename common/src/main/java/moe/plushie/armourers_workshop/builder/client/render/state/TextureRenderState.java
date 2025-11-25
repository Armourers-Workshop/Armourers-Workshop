package moe.plushie.armourers_workshop.builder.client.render.state;

import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.guide.GuideDataProvider;
import moe.plushie.armourers_workshop.core.client.texture.PaintableTexture;
import moe.plushie.armourers_workshop.core.client.texture.PlayerSkinBakery;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.client.Minecraft;

public class TextureRenderState implements GuideDataProvider {

    private static final DataContainer.Key<TextureRenderState> KEY = DataContainer.key("TextureRenderState");

    protected final PaintableTexture texture;
    protected final OpenResourceLocation location;
    protected int lastVersion;
    protected boolean shouldRenderOverlay = false;
    protected SkinProperties skinProperties = SkinProperties.EMPTY;

    public TextureRenderState(String name) {
        this.texture = new PaintableTexture("dynamic/paintable/" + name);
        this.location = ModConstants.key("dynamic/paintable/" + name);
        // register a texture into texture manager.
        Minecraft.getInstance().getTextureManager().register(location, texture);
    }

    public static TextureRenderState of(ArmourerBlockEntity entity) {
        // bind a texture render state into entity.
        var renderState = DataContainer.get(entity, KEY);
        if (renderState == null) {
            renderState = new TextureRenderState(String.format("at-%08x", entity.getBlockPos().asLong()));
            DataContainer.set(entity, KEY, renderState);
        }
        // extract the texture render state by the entity.
        renderState.skinProperties = entity.skinProperties();
        renderState.texture.setRefer(PlayerSkinBakery.getInstance().loadSkin(entity.textureDescriptor()));
        renderState.texture.setPaintData(entity.paintData());
        return renderState;
    }

    public void setRenderOverlay(boolean shouldRenderOverlay) {
        this.shouldRenderOverlay = shouldRenderOverlay;
    }

    @Override
    public boolean shouldRenderOverlay(SkinProperty<Boolean> property) {
        //  must check after the enable rendering.
        if (shouldRenderOverlay) {
            return !skinProperties.get(property);
        }
        return false;
    }

    public OpenResourceLocation location() {
        return location;
    }
}

