package top.leonx.vanity.util;

public interface TernaryFunc<A,B,C,D> {
    D compute(A a,B b,C c);
}
