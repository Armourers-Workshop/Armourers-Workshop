package moe.plushie.armourers_workshop.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SliceRandomlyAccessor<T> {

    private int selectedIndex;
    private int startIndex;
    private int endIndex;

    private Provider<? extends T> selected;

    private final int count;
    private final List<Provider<? extends T>> slices;

    public SliceRandomlyAccessor(List<Provider<? extends T>> slices) {
        this.slices = checkOrder(slices);
        this.count = slices.size();
        if (count != 0) {
            switchSlice(0);
        }
    }

    public T get(int index) {
        return getSlice(index).get(index - startIndex);
    }

    private Provider<? extends T> getSlice(int index) {
        int cursor = selectedIndex;
        while (true) {
            if (index < startIndex) {
                // required index too low, move to prev slice.
                cursor -= 1;
            } else if (index >= endIndex) {
                // required index too hig, move to next slice.
                cursor += 1;
            } else {
                // yep it is we need.
                return selected;
            }
            if (cursor < 0 || cursor >= count) {
                // we can't found it.
                break;
            }
            switchSlice(cursor);
        }
        throw new IndexOutOfBoundsException("Index out of range: " + index);
    }

    private void switchSlice(int selectedIndex) {
        this.selected = slices.get(selectedIndex);
        this.selectedIndex = selectedIndex;
        this.startIndex = selected.getStartIndex();
        this.endIndex = selected.getEndIndex();
    }

    private List<Provider<? extends T>> checkOrder(List<Provider<? extends T>> slices) {
        ArrayList<Provider<? extends T>> sortedSlices = new ArrayList<>(slices);
        sortedSlices.sort(Comparator.comparingInt(Provider::getStartIndex));
        return sortedSlices;
    }

    public interface Provider<T> {

        T get(int index);

        int getStartIndex();

        int getEndIndex();
    }
}
