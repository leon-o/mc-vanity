package top.leonx.vanity.ai.goap;

import top.leonx.vanity.entity.OutsiderEntity;

import java.util.function.Function;

public class GoapGoal {
    public String key;
    public Function<OutsiderEntity,Boolean> isFit;

    public GoapGoal(String key, Function<OutsiderEntity, Boolean> isFit) {
        this.key = key;
        this.isFit = isFit;
    }
}
