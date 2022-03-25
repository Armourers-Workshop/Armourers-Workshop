package moe.plushie.armourers_workshop.api.skin;

public interface ISkinDataProvider {

    <T> void setSkinData(T data);

    <T> T getSkinData();
}
