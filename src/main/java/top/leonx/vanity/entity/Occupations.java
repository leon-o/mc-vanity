package top.leonx.vanity.entity;

import top.leonx.vanity.VanityMod;

public enum Occupations {
    COOKER,
    BUILDER,
    FARMER, LUMBERJACK,
    MINER, WEAPON_MAKER, DRESSMAKER, RESEARCHER, TRADER, DELIVERYMAN, SOLDIER;

    public String getTranslateKey()
    {
        return String.format("occupation.%s.%s", VanityMod.MOD_ID,this.name());
    }
}
