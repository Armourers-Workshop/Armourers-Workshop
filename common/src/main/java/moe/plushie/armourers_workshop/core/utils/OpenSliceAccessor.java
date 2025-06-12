package moe.plushie.armourers_workshop.core.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OpenSliceAccessor<T> {

    private int selectedIndex;
    private int startIndex;
    private int endIndex;

    private Provider<? extends T> selected;

    private final int count;
    private final List<Provider<? extends T>> slices;

    public OpenSliceAccessor(List<Provider<? extends T>> slices) {
        this.slices = checkOrder(slices);
        this.count = slices.size();
        if (count != 0) {
            switchSlice(0);
        }
    }

    public T get(int index) {
        return sliceAt(index).get(index - startIndex);
    }

    private Provider<? extends T> sliceAt(int index) {
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
        this.startIndex = selected.startIndex();
        this.endIndex = selected.endIndex();
    }

    private List<Provider<? extends T>> checkOrder(List<Provider<? extends T>> slices) {
        var sortedSlices = new ArrayList<>(slices);
        sortedSlices.sort(Comparator.comparingInt(Provider::startIndex));
        return sortedSlices;
    }

    public interface Provider<T> {

        T get(int index);

        int startIndex();

        int endIndex();
    }
}
