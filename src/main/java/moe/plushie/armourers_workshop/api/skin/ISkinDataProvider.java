package moe.plushie.armourers_workshop.api.skin;

public interface ISkinDataProvider {

    <T> void setSkinData(T data);

    <T> T getSkinData();

//    @Nullable
//    static <T extends Comparable<T>> T getValue(Object object, Property<T> property) {
//        HashMap<Property<?>, ?> values = ((ISkinDataProvider) object).getSkinData();
//        if (values != null) {
//            Object value = values.get(property);
//            if (value != null) {
//                return property.getValueClass().cast(value);
//            }
//        }
//        return null;
//    }
//
//    static <T, V extends T> void setValue(Object object, Property<T> property, V value) {
//        HashMap<Property<?>, Object> values = ((ISkinDataProvider) object).getSkinData();
//        if (values == null) {
//            values = new HashMap<>();
//            ((ISkinDataProvider) object).setSkinData(value);
//        }
//        values.put(property, value);
//    }
//
//    interface Property<T> {
//        Class<T> getValueClass();
//    }
}
