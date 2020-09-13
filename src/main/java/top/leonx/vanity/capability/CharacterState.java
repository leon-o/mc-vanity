package top.leonx.vanity.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CharacterState {
    public static final CharacterState EMPTY = new CharacterState();
    private final Map<UUID, Float> relationMap = new HashMap<>();
    private final Map<UUID, Float> loveMap     = new HashMap<>();
    boolean needInit = true;
    private       CompoundNBT      root        = new CompoundNBT();
    private       MOOD             currentMood;
    private UUID followedEntityUuid;
    public Gender getGender() {
        String genderStr = root.getString(Keys.GENDER);
        return genderStr.length() > 0 && genderStr.equals("male") ? Gender.MALE : Gender.FEMALE;
    }

    public void setGender(Gender gender) {
        root.putString("gender", gender.toString());
    }

    public float getLoveWith(UUID uuid) {
        return loveMap.getOrDefault(uuid, 0f);
    }

    public MOOD getMOOD() {
        if (currentMood == null) ComputeMOOD();

        return currentMood;
    }

    public void setMOOD(MOOD targetMood) {
        CompoundNBT moodNbt = root.getCompound(Keys.MOOD);
        for (MOOD mood : MOOD.ALL_MOODS) {
            moodNbt.putFloat(mood.name(), 0);
        }
        moodNbt.putFloat(targetMood.name(), 100f);
        root.put(Keys.MOOD, moodNbt);
        currentMood = targetMood;
    }

    public float getRelationWith(UUID uuid) {
        return relationMap.getOrDefault(uuid, 0f);
    }

    public CompoundNBT getRoot() {
        writeRelationMap();
        writeLoveMap();
        root.putUniqueId("followed_entity",followedEntityUuid==null?new UUID(0,0):followedEntityUuid);
        return root;
    }

    public void setRoot(CompoundNBT root) {
        this.root = root;
        ComputeMOOD();
        readRelationMap();
        readLoveMap();
        followedEntityUuid=root.getUniqueId("followed_entity");
        if(followedEntityUuid.getMostSignificantBits()==0 && followedEntityUuid.getLeastSignificantBits()==0)
            followedEntityUuid=null;

    }

    public Gender getSexualOrientation() {
        String str = root.getString(Keys.SexualOrientation);
        return Gender.valueOf(str);
    }

    public void setSexualOrientation(Gender gender) {
        root.putString(Keys.SexualOrientation, gender.name());
    }

    public float getState(String key) {
        return root.getFloat(key);
    }

    public boolean isNeedInit() {
        return needInit;
    }

    public void setNeedInit(boolean needInit) {
        this.needInit = needInit;
    }

    public void setLoveWith(UUID id, float value) {
        loveMap.put(id, value);
    }

    public void setRelationWith(UUID id, float value) {
        relationMap.put(id, value);
    }

    public void setState(String key, float value) {
        root.putFloat(key, value);
    }
    @Nullable
    public UUID getFollowedEntityUUID()
    {
        return followedEntityUuid;
    }
    public void setFollowedEntity(@Nullable UUID uuid)
    {
        followedEntityUuid=uuid;
    }
    private void ComputeMOOD() {
        currentMood = MOOD.NORMAL;
        CompoundNBT moodNbt = root.getCompound(Keys.MOOD);

        float max = 0;
        for (MOOD mood : MOOD.ALL_MOODS) {
            float v = moodNbt.getFloat(mood.name());
            if (v > max && v > 1f) {
                max = v;
                currentMood = mood;
            }
        }
    }

    private void readLoveMap() {
        loveMap.clear();
        ListNBT list = root.getList(Keys.Love, 10);
        for (INBT inbt : list) {
            if (inbt instanceof CompoundNBT) {
                CompoundNBT entry = (CompoundNBT) inbt;
                UUID        id    = entry.getUniqueId("id");
                float       value = entry.getFloat("value");
                relationMap.put(id, value);
            }
        }
    }

    private void readRelationMap() {
        relationMap.clear();
        ListNBT list = root.getList(Keys.Relation, 10);
        for (INBT inbt : list) {
            if (inbt instanceof CompoundNBT) {
                CompoundNBT entry = (CompoundNBT) inbt;
                UUID        id    = entry.getUniqueId("id");
                float       value = entry.getFloat("value");
                relationMap.put(id, value);
            }
        }
    }

    private void writeLoveMap() {
        ListNBT list = new ListNBT();
        for (Map.Entry<UUID, Float> entry : relationMap.entrySet()) {
            CompoundNBT entryNbt = new CompoundNBT();
            entryNbt.putUniqueId("id", entry.getKey());
            entryNbt.putFloat("value", entry.getValue());
        }
        root.put(Keys.Love, list);
    }

    private void writeRelationMap() {
        ListNBT list = new ListNBT();
        for (Map.Entry<UUID, Float> entry : relationMap.entrySet()) {
            CompoundNBT entryNbt = new CompoundNBT();
            entryNbt.putUniqueId("id", entry.getKey());
            entryNbt.putFloat("value", entry.getValue());
        }
        root.put(Keys.Relation, list);
    }

    public enum Gender {
        MALE, FEMALE,
    }

    public enum MOOD {
        NORMAL, HAPPY, ANGRY, SAD, SURPRISED;

        public final static MOOD[] ALL_MOODS = {NORMAL, HAPPY, ANGRY, SAD, SURPRISED};

    }

    public static final class Keys {
        public final static String GENDER            = "gender";
        public static final String MOOD              = "mood";
        public final static String SexualOrientation = "sex_orientation";
        public final static String Relation          = "relation";
        public final static String Love              = "love";
    }
}
