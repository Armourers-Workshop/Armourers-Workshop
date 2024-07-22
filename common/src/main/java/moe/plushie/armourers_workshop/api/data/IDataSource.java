package moe.plushie.armourers_workshop.api.data;

public interface IDataSource {

    void connect() throws Exception;

    void disconnect() throws Exception;
}
