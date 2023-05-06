package moe.plushie.armourers_workshop.api.registry;

public interface IPermissionNodeBuilder<T> extends IEntryBuilder<T> {

    IPermissionNodeBuilder<T> level(int level);
}
