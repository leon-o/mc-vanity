package top.leonx.vanity.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import top.leonx.vanity.data.NbtIO;
import top.leonx.vanity.data.TrinityDataParameter;

import java.util.Optional;
import java.util.UUID;

public interface IOutsider {
    //TrinityDataParameter<Optional<ITextComponent>> CUSTOM_NAME        =new TrinityDataParameter<>(2, DataSerializers.OPTIONAL_TEXT_COMPONENT, "CustomName", NbtIO.OPTIONAL_TEXT_COMPONENT_IO);
    TrinityDataParameter<Optional<UUID>>           FOLLOWED_PLAYER_ID =new  TrinityDataParameter<>(127,DataSerializers.OPTIONAL_UNIQUE_ID,"follow_player", NbtIO.OPTIONAL_UUID_IO);
    TrinityDataParameter<Optional<UUID>> LEADER   = new TrinityDataParameter<>(128,DataSerializers.OPTIONAL_UNIQUE_ID, "leader", NbtIO.OPTIONAL_UUID_IO);
    TrinityDataParameter<Optional<BlockPos>>  HOME_POS = new TrinityDataParameter<>(129, DataSerializers.OPTIONAL_BLOCK_POS, "home", NbtIO.OPTIONAL_BLOCK_POS_IO);
    default void registerDataParameter()
    {
        getDataManager().register(FOLLOWED_PLAYER_ID,Optional.empty());
        getDataManager().register(LEADER,Optional.empty());
        getDataManager().register(HOME_POS,Optional.empty());
    }
    default void writeDataParameter(CompoundNBT nbt)
    {
        Optional<UUID> leaderUuid = getLeaderUUID();
        FOLLOWED_PLAYER_ID.write(nbt,getFollowedPlayerId());
        LEADER.write(nbt,leaderUuid);
        HOME_POS.write(nbt,getHome());
    }

    default void readDataParameter(CompoundNBT nbt)
    {
        setFollowedPlayerId(FOLLOWED_PLAYER_ID.read(nbt).orElse(null));
        setLeaderUUID(LEADER.read(nbt).orElse(null));
        setHome(HOME_POS.read(nbt).orElse(null));
    }

    EntityDataManager getDataManager();

    default void setLeaderUUID(UUID uuid){
        getDataManager().set(LEADER,Optional.ofNullable(uuid));
    }
    default Optional<UUID> getLeaderUUID(){
        return getDataManager().get(LEADER);
    }

    default void setHome(BlockPos pos){
        getDataManager().set(HOME_POS,Optional.ofNullable(pos));
    }
    default Optional<BlockPos> getHome(){
        return getDataManager().get(HOME_POS);
    }

    default void setFollowedPlayerId(UUID uuid){getDataManager().set(FOLLOWED_PLAYER_ID,Optional.ofNullable(uuid));}
    default Optional<UUID> getFollowedPlayerId(){return getDataManager().get(FOLLOWED_PLAYER_ID);}
}
