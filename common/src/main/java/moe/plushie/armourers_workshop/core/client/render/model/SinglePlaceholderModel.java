package moe.plushie.armourers_workshop.core.client.render.model;

public class SinglePlaceholderModel<S > extends PlaceholderModel<S, PlaceholderModelPart> {

    public final PlaceholderModelPart root;

    public SinglePlaceholderModel() {
        this.root = abi$getPartByName("root");
    }

    @Override
    protected PlaceholderModelPart createPart(String name) {
        return new PlaceholderModelPart(name);
    }
}
