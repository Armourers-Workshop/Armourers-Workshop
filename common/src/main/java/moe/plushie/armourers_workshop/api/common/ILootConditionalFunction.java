package moe.plushie.armourers_workshop.api.common;

public interface ILootConditionalFunction extends ILootFunction {

    interface Serializer<T extends ILootConditionalFunction> extends ILootFunction.Serializer<T> {
    }
}
