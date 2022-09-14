package moe.plushie.armourers_workshop.api.common.builder;

public interface IPermissionNodeBuilder<T> extends IEntryBuilder<T> {

    IPermissionNodeBuilder<T> level(int level);
}
