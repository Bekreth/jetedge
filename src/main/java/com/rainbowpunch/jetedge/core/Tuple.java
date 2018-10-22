package com.rainbowpunch.jetedge.core;

/**
 * This is a generic tuple to contain objects.  It exists to avoid using the anti-pattern of having Map.Entries
 *      be used as a Tuple.
 */
public class Tuple<T, U> {
    private T t;
    private U u;

    public Tuple(T t, U u) {
        this.t = t;
        this.u = u;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public U getU() {
        return u;
    }

    public void setU(U u) {
        this.u = u;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Tuple{");
        sb.append("t=").append(t);
        sb.append(", u=").append(u);
        sb.append('}');
        return sb.toString();
    }
}
