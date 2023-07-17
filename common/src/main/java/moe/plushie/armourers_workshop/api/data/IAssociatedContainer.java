package moe.plushie.armourers_workshop.api.data;

public interface IAssociatedContainer {

    <T> T getAssociatedObject(IAssociatedContainerKey<T> key);

    <T> void setAssociatedObject(IAssociatedContainerKey<T> key, T value);
}
