package moe.plushie.armourers_workshop.builder.data.properties;

import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.api.data.IDataProperty;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DataProperty<T> implements IDataProperty<T> {

    protected T value;
    protected Consumer<Boolean> editingObserver;
    protected final ArrayList<Consumer<T>> valueObservers = new ArrayList<>();


    public void beginEditing() {
        if (editingObserver != null) {
            editingObserver.accept(true);
        }
    }

    public void endEditing() {
        if (editingObserver != null) {
            editingObserver.accept(false);
        }
    }

    @Override
    public void set(T value) {
        if (Objects.equal(this.value, value)) {
            return;
        }
        this.value = value;
        this.valueObservers.forEach(it -> it.accept(value));
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void addObserver(Consumer<T> observer) {
        this.valueObservers.add(observer);
    }

    public void addEditingObserver(Consumer<Boolean> observer) {
        this.editingObserver = observer;
    }
}
