package ch.leafit.webfauna.Utils;

/**
 * Created by marius on 09/07/14.
 *
 * Can be used as workaround to be able to create "proper" out-params
 */
public class OutParam<T> {
    private T mValue;
    public OutParam(){

    }

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
    }
}
