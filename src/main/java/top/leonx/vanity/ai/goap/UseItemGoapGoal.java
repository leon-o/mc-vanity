package top.leonx.vanity.ai.goap;

import top.leonx.vanity.entity.OutsiderEntity;

import java.util.function.Function;

public class UseItemGoapGoal extends GoapGoal{

    public UseItemGoapGoal(Function<OutsiderEntity, Boolean> isFit) {
        super("use_item", isFit);
    }
}
