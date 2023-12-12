package moe.plushie.armourers_workshop.core.client.gui.widget;

import java.util.List;

public class TreeIndexPath {

    private final List<Integer> indexes;

    public TreeIndexPath(List<Integer> indexes) {
        this.indexes = indexes;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }
}
