package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.api.client.state.IEntityRenderState;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderMode;
import moe.plushie.armourers_workshop.core.client.render.plugin.EntityRenderPlugin;
import moe.plushie.armourers_workshop.core.client.texture.PlayerSkinLoader;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkin;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentManager;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentType;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;

public class EntityRenderState extends RenderState implements IEntityRenderState {

    protected int id;
    protected int outlineColor;

    protected EntityType<?> type;

    protected double x;
    protected double y;
    protected double z;

    protected double xo;
    protected double yo;
    protected double zo;

    protected boolean isLimitLimbs = false;

    protected boolean isInvisible = false;

    protected boolean isBaby;
    protected boolean isFlying;
    protected boolean isFallFlying;

    protected boolean shouldRenderInGUI = false;

    protected OpenVector3f deltaMovement;
    protected SkinAttachmentManager attachmentManager;

    protected EntityRenderPlugin<?, ?> renderPlugin;
    protected PlayerSkin entityTexture = PlayerSkin.DEFAULT;

    protected final SkinRenderState hands = new SkinRenderState();
    protected final SkinRenderState armors = new SkinRenderState();

    protected final ModelVisibilityState visibilityState = new ModelVisibilityState();

    protected final HashMap<OpenEquipmentSlot, Boolean> shouldRenderEquipmentSlots = new HashMap<>();

    public int id() {
        return id;
    }

    public int outlineColor() {
        return outlineColor;
    }

    public EntityType<?> type() {
        return type;
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public boolean isFallFlying() {
        return isFallFlying;
    }

    public boolean isBaby() {
        return isBaby;
    }

    public void setLimitLimbs(boolean limitLimbs) {
        isLimitLimbs = limitLimbs;
    }

    public boolean isLimitLimbs() {
        return isLimitLimbs;
    }

    public SkinRenderState hands() {
        return hands;
    }

    public SkinRenderState armors() {
        return armors;
    }

    public ModelVisibilityState visibilityState() {
        return visibilityState;
    }

    public PlayerSkin entityTexture() {
        return entityTexture;
    }

    public void setRenderPlugin(EntityRenderPlugin<?, ?> renderPlugin) {
        this.renderPlugin = renderPlugin;
    }

    public EntityRenderPlugin<?, ?> renderPlugin() {
        return renderPlugin;
    }

    public BakedArmature getArmature(BakedArmature armature) {
        if (renderPlugin != null) {
            return renderPlugin.getArmature(armature);
        }
        return null;
    }

    public void setAttachmentPose(SkinAttachmentType attachmentType, int index, SkinAttachmentPose pose) {
        if (attachmentManager != null) {
            attachmentManager.put(attachmentType, index, pose);
        }
    }

    public SkinAttachmentPose getAttachmentPose(SkinAttachmentType attachmentType, int index) {
        if (attachmentManager != null) {
            return attachmentManager.get(attachmentType, index);
        }
        return null;
    }

    public void setRenderEquipmentSlot(OpenEquipmentSlot equipment, boolean flag) {
        shouldRenderEquipmentSlots.put(equipment, flag);
    }

    public boolean shouldRenderEquipment(OpenEquipmentSlot equipment) {
        return shouldRenderEquipmentSlots.getOrDefault(equipment, false);
    }

    public boolean shouldRenderInGUI() {
        return shouldRenderInGUI;
    }

    public static void extract(Entity entity, EntityRenderState renderState) {
        var renderData = EntityRenderData.of(entity);
        //
        renderState.id = entity.getId();
        renderState.type = entity.getType();
        renderState.outlineColor = entity.getOutlineColor();
        renderState.x = entity.getX();
        renderState.y = entity.getY();
        renderState.z = entity.getZ();
        renderState.xo = entity.xOld;
        renderState.yo = entity.yOld;
        renderState.zo = entity.zOld;
        renderState.isFlying = false;
        renderState.isFallFlying = false;
        renderState.isBaby = false;
        renderState.isInvisible = entity.isInvisible();
        renderState.entityTexture = PlayerSkinLoader.getInstance().loadSkin(entity);
        renderState.animationManager = renderData.animationManager();
        renderState.attachmentManager = renderData.attachmentManager();
        //
        renderState.hands.prepare(renderData.itemSkins());
        renderState.armors.prepare(renderData.armorSkins());
        //
        renderState.isLimitLimbs = renderData.isLimitLimbs();
        renderState.shouldRenderInGUI = SkinRenderMode.inGUI();

        renderState.visibilityState.prepare(renderData);
    }
}
