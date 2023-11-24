package moe.plushie.armourers_workshop.library.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinPreviewList;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.library.data.impl.ServerSkin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ServerItemList extends SkinPreviewList<ServerSkin> {

    public ServerItemList(CGRect frame) {
        super(frame);
    }

    @Override
    protected String getItemName(ServerSkin value) {
        return value.getName();
    }

    @Override
    protected SkinDescriptor getItemDescriptor(ServerSkin value) {
        return value.getDescriptor();
    }
}
