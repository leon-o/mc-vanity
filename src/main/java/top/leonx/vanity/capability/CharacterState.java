package top.leonx.vanity.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.util.CharacterStateUtil;
import top.leonx.vanity.util.Gender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CharacterState {
    public static final float            MAX_RELATIONSHIP = 100;
    public static final float            MIN_RELATIONSHIP = -100;
    public static final float            MAX_LOVE         = 100;
    public static final float            MIN_LOVE         = 0;
    public static final CharacterState   EMPTY            = new CharacterState();
    private final       Map<UUID, Float> relationMap      = new HashMap<>();
    private final       Map<UUID, Float> loveMap          = new HashMap<>();
    boolean needInit = true;
    private CompoundNBT root = new CompoundNBT();
    private MOOD        currentMood;

    public static void promoteLoveWith(LivingEntity entity1, LivingEntity entity2, float i) {
        if (CharacterStateUtil.hasCharacterState(entity1) && CharacterStateUtil.hasCharacterState(entity2)) {
            CharacterState state1      = entity1.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
            CharacterState state2      = entity2.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
            float          originValue = (state1.getLoveWith(entity2.getUniqueID()) + state2.getLoveWith(entity1.getUniqueID())) / 2;

            state1.setLoveWith(entity2.getUniqueID(), originValue + i);
            state2.setLoveWith(entity1.getUniqueID(), originValue + i);
        }
    }

    public static void promoteRelationWith(LivingEntity entity1, LivingEntity entity2, float i) {
        if (CharacterStateUtil.hasCharacterState(entity1) && CharacterStateUtil.hasCharacterState(entity2)) {
            CharacterState state1      = entity1.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
            CharacterState state2      = entity2.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
            float          originValue = (state1.getRelationWith(entity2.getUniqueID()) + state2.getRelationWith(entity1.getUniqueID())) / 2;

            state1.setRelationWith(entity2.getUniqueID(), originValue + i);
            state2.setRelationWith(entity1.getUniqueID(), originValue + i);
        }
    }

    public static void setLoveWith(LivingEntity entity1, LivingEntity entity2, float value) {
        if (CharacterStateUtil.hasCharacterState(entity1)) {
            CharacterState state = entity1.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
            state.setLoveWith(entity2.getUniqueID(), value);
        }
        if (CharacterStateUtil.hasCharacterState(entity2)) {
            CharacterState state = entity2.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
            state.setLoveWith(entity1.getUniqueID(), value);
        }
    }

    public static void setRelationWith(LivingEntity entity1, LivingEntity entity2, float value) {
        if (CharacterStateUtil.hasCharacterState(entity1)) {
            CharacterState state = entity1.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
            state.setRelationWith(entity2.getUniqueID(), value);
        }
        if (CharacterStateUtil.hasCharacterState(entity2)) {
            CharacterState state = entity2.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
            state.setRelationWith(entity1.getUniqueID(), value);
        }
    }

    public Gender getGender() {
        String genderStr = root.getString(Keys.GENDER);
        return genderStr.length() > 0 && genderStr.equals(Gender.MALE.name()) ? Gender.MALE : Gender.FEMALE;
    }

    public void setGender(Gender gender) {
        root.putString(Keys.GENDER, gender.name());
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

    public float getRelationWith(LivingEntity entity) {
        return getRelationWith(entity.getUniqueID());
    }

    public float getRelationWith(UUID uuid) {
        return relationMap.getOrDefault(uuid, 0f);
    }

    public CompoundNBT getRoot() {
        writeRelationMap();
        writeLoveMap();
        //root.putUniqueId("followed_entity",followedEntityUuid==null?new UUID(0,0):followedEntityUuid);
        return root;
    }

    public void setRoot(CompoundNBT root) {
        this.root = root;
        ComputeMOOD();
        readRelationMap();
        readLoveMap();
        //followedEntityUuid=root.getUniqueId("followed_entity");
//        if(followedEntityUuid.getMostSignificantBits()==0 && followedEntityUuid.getLeastSignificantBits()==0)
//            followedEntityUuid=null;

    }

    public Gender getSexualOrientation() {
        String str = root.getString(Keys.SexualOrientation);
        return str.length() > 0 ? Gender.valueOf(str) : Gender.FEMALE;
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

    public void setState(String key, float value) {
        root.putFloat(key, value);
    }

    //    @Nullable
//    public UUID getFollowedEntityUUID()
//    {
//        return followedEntityUuid;
//    }
//    public void setFollowedEntity(@Nullable UUID uuid)
//    {
//        followedEntityUuid=uuid;
//    }
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

    private ListNBT convertMapToListNbt(Map<UUID, Float> map) {
        ListNBT list = new ListNBT();
        for (Map.Entry<UUID, Float> entry : map.entrySet()) {
            CompoundNBT entryNbt = new CompoundNBT();
            entryNbt.putUniqueId("id", entry.getKey());
            entryNbt.putFloat("value", entry.getValue());
            list.add(entryNbt);
        }
        return list;
    }

    private void readLoveMap() {
        loveMap.clear();
        ListNBT list = root.getList(Keys.Love, 10);
        for (INBT inbt : list) {
            if (inbt instanceof CompoundNBT) {
                CompoundNBT entry = (CompoundNBT) inbt;
                UUID        id    = entry.getUniqueId("id");
                float       value = entry.getFloat("value");
                loveMap.put(id, value);
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

    private void setLoveWith(UUID id, float value) {
        loveMap.put(id, MathHelper.clamp(value, MIN_LOVE, MAX_LOVE));
    }

    private void setRelationWith(UUID id, float value) {
        relationMap.put(id, MathHelper.clamp(value, MIN_RELATIONSHIP, MAX_RELATIONSHIP));
    }

    private void writeLoveMap() {
        root.put(Keys.Love, convertMapToListNbt(loveMap));
    }

    private void writeRelationMap() {
        root.put(Keys.Relation, convertMapToListNbt(relationMap));
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
