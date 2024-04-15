package moe.plushie.armourers_workshop.core.client.model;

public class SinglePlaceholderModel extends PlaceholderModel {

    public final Part root;

    public SinglePlaceholderModel() {
        this.root = getPart("name");
    }

    public boolean isVisible() {
        return root.isVisible();
    }

}
