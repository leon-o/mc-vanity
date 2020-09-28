package top.leonx.vanity.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;

public class NeedsStatus {
    public LivingEntity entity;

    public static final int maxLevel=20;
    public float socialLevel=20;
    public float cleanLevel=20;
    public float entertainmentLevel=20;
    public int needsTickTimer=0;
    public NeedsStatus(LivingEntity entity) {
        this.entity = entity;
    }

    public void tick()
    {
        Difficulty difficulty = entity.world.getDifficulty();
        float factor=1;
        if(difficulty==Difficulty.EASY)
            factor=0.5f;
        else if(difficulty==Difficulty.HARD)
            factor=1.5f;
        else if(difficulty==Difficulty.PEACEFUL)
            factor=0.2f;

        socialLevel= MathHelper.clamp(0.06f*factor,0,maxLevel) ;
        cleanLevel=MathHelper.clamp(0.01f*factor,0,maxLevel);
        entertainmentLevel=MathHelper.clamp(0.015f*factor,0,maxLevel);
    }

    /**
     * Reads the food data for the entity.
     */
    public void read(CompoundNBT compound) {
        if (compound.contains("foodLevel", 99)) {
            this.socialLevel = compound.getFloat("socialLevel");
            this.cleanLevel = compound.getFloat("cleanLevel");
            this.entertainmentLevel = compound.getFloat("entertainmentLevel");
            this.needsTickTimer = compound.getInt("needsTickTimer");
        }

    }

    /**
     * Writes the food data for the entity.
     */
    public void write(CompoundNBT compound) {
        compound.putFloat("socialLevel", this.socialLevel);
        compound.putFloat("cleanLevel", this.cleanLevel);
        compound.putFloat("entertainmentLevel", this.entertainmentLevel);
        compound.putInt("needsTickTimer", this.needsTickTimer);
    }
}
