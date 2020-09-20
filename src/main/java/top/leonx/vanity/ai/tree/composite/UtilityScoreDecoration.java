package top.leonx.vanity.ai.tree.composite;

import net.minecraft.util.math.MathHelper;

public abstract class UtilityScoreDecoration{
    public abstract double decorate(double rawScore,UtilitySelectTask<?> selector);
    public abstract void tick();
    public static class NoneDecoration extends UtilityScoreDecoration{

        @Override
        public double decorate(double rawScore,UtilitySelectTask<?> selector) {
            return rawScore;
        }

        @Override
        public void tick() {

        }
    }
    public static class InertiaDecoration extends UtilityScoreDecoration{
        double factor;
        double lastScore=0;
        public InertiaDecoration(double factor)
        {
            this.factor=factor;
        }

        @Override
        public double decorate(double rawScore,UtilitySelectTask<?> selector) {
            double finalScore= MathHelper.lerp(1-factor, lastScore, rawScore);
            lastScore=finalScore;
            return finalScore;
        }

        @Override
        public void tick() {

        }
    }

    public static class CooldownDecoration extends UtilityScoreDecoration{
        final int maxCooldownTick;
        int cooldown;
        public CooldownDecoration(int maxCooldownTick) {
            this.maxCooldownTick = maxCooldownTick;
        }

        @Override
        public double decorate(double rawScore,UtilitySelectTask<?> selector) {
            if(equals(selector.utilityScoreDecorationMap.getOrDefault(selector.currentTask,null)))
            {
                cooldown=maxCooldownTick;
            }
            return MathHelper.lerp(cooldown/(double)maxCooldownTick,rawScore,0);
        }

        @Override
        public void tick() {
            cooldown =Math.max(0, cooldown--);
        }
    }
}
