package activetransportation.myapplication;

public class Holder<T> {
    private T value;

    public Holder(T value) {
        setValue(value);
    }

    T getValue() {
        return value;
    }

    void setValue(T value) {
        this.value = value;
    }
}
