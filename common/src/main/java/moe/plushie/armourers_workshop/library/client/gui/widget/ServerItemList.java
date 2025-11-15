package moe.plushie.armourers_workshop.library.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinPreviewList;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.library.data.impl.ServerSkin;

public class ServerItemList extends SkinPreviewList<ServerSkin> {

    public ServerItemList(CGRect frame) {
        super(frame);
    }

    @Override
    protected String getItemName(ServerSkin value) {
        return value.name();
    }

    @Override
    protected SkinDescriptor getItemDescriptor(ServerSkin value) {
        return value.descriptor();
    }
}
