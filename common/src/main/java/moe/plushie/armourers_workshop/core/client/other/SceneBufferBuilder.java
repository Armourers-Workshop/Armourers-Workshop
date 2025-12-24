package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IRenderAttachment;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferBuilder;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SceneBufferBuilder extends AbstractBufferBuilder {

    private final ArrayList<IRenderAttachment> attachments = new ArrayList<>();

    public SceneBufferBuilder(int size) {
        super(size);
    }

    public void setupRenderState() {
        attachments.forEach(IRenderAttachment::setupRenderState);
    }

    public void clearRenderState() {
        attachments.forEach(IRenderAttachment::clearRenderState);
    }

    public void addAttachment(IRenderAttachment attachment) {
        attachments.add(attachment);
    }

    public void removeAttachment(IRenderAttachment attachment) {
        attachments.remove(attachment);
    }

    public List<? extends IRenderAttachment> attachments() {
        return attachments;
    }
}
