package org.abx.util;

import java.io.Serializable;

public class Pair<T, U> implements Serializable {
    public T first;
    public U second;

    public Pair(T t, U u) {
        this.first = t;
        this.second = u;
    }
}