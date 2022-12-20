package moe.plushie.armourers_workshop.api.skin;

public interface ISkinDataProvider {

    <T> T getSkinData();

    <T> void setSkinData(T data);
}
