package top.leonx.vanity.ai.goap;

import java.util.Objects;

public class GoapCost {
    public static final GoapCost PASS=new GoapCost(Integer.MAX_VALUE);
    public static final GoapCost LOW=new GoapCost(0);
    public static final GoapCost MID=new GoapCost(10);
    public static final GoapCost HIGH=new GoapCost(20);
    int cost;
    public GoapCost(int cost)
    {
        this.cost =cost;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoapCost goapCost = (GoapCost) o;
        return cost == goapCost.cost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost);
    }
}
