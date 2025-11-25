package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderAttachment;
import moe.plushie.armourers_workshop.compat.client.AbstractBufferBuilder;

import java.util.ArrayList;
import java.util.List;

public class SkinBufferBuilder extends AbstractBufferBuilder {

    private final ArrayList<IRenderAttachment> attachments = new ArrayList<>();

    public SkinBufferBuilder(int size) {
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
