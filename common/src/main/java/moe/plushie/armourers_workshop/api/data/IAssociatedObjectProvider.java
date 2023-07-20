package moe.plushie.armourers_workshop.api.data;

public interface IAssociatedObjectProvider {

    <T> T getAssociatedObject();

    <T> void setAssociatedObject(T data);
}
