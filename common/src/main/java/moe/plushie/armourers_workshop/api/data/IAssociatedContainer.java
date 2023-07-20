package moe.plushie.armourers_workshop.api.data;

public interface IAssociatedContainer {

    <T> T getAssociatedObject(IAssociatedContainerKey<T> key);

    <T> void setAssociatedObject(T value, IAssociatedContainerKey<T> key);
}
