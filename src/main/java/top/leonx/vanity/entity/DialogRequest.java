package top.leonx.vanity.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Tuple;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.container.OutsiderInventoryContainer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class DialogRequest {
    private final static Map<String, DialogRequest> OPERATION_MAP = new HashMap<>();
    public static final  DialogRequest              FOLLOW_ME     = DialogRequest.create("follow_me", tuple -> {
        if (tuple.getA() instanceof ServerPlayerEntity) tuple.getB().setFollowedPlayer((ServerPlayerEntity) tuple.getA());
    }, tuple -> tuple.getB().getCharacterState().getRelationWith(tuple.getA().getUniqueID()) > CharacterState.MAX_RELATIONSHIP / 2 && !tuple.getB().getFollowedPlayerId().isPresent());
    public static final  DialogRequest              DISBAND       = DialogRequest.create("disband", tuple -> tuple.getB().setFollowedPlayer(null),
                                                                                         tuple -> tuple.getB().getFollowedPlayerId().isPresent());

    public static final  DialogRequest OPEN_INVENTORY  = DialogRequest.create("open_inventory", tuple -> {
        PlayerEntity   player = tuple.getA();
        OutsiderEntity entity = tuple.getB();
        INamedContainerProvider provider = new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return entity.getDisplayName();
            }

            @Override
            public Container createMenu(int id, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player) {
                return new OutsiderInventoryContainer(id, inventory, entity);
            }
        };
        NetworkHooks.openGui((ServerPlayerEntity) player, provider, t -> t.writeInt(entity.getEntityId()));
    });
    public static final DialogRequest JOIN_US = DialogRequest.create("join_us",tuple -> {
        tuple.getB().setLeaderUUID(tuple.getA().getUniqueID());
    },tuple -> tuple.getB().getLeaderUUID().orElse(new UUID(0,0)).equals(tuple.getA().getUniqueID()));


    private final static DialogRequest DUMMY_OPERATION = new DialogRequest("dummy", (t) -> {}, t -> true);
    private final        String        name;
    private final Consumer<Tuple<PlayerEntity, OutsiderEntity>>          execute;
    private final Function<Tuple<PlayerEntity, OutsiderEntity>, Boolean> premise;

    private DialogRequest(String opCode, Consumer<Tuple<PlayerEntity, OutsiderEntity>> execute, Function<Tuple<PlayerEntity, OutsiderEntity>, Boolean> premise) {
        this.name = opCode;
        this.execute = execute;
        this.premise = premise;
    }

    public static DialogRequest create(String name, Consumer<Tuple<PlayerEntity, OutsiderEntity>> execute) {
        return create(name, execute, t -> true);
    }

    public static DialogRequest create(String name, Consumer<Tuple<PlayerEntity, OutsiderEntity>> execute, Function<Tuple<PlayerEntity, OutsiderEntity>, Boolean> premise) {
        DialogRequest operation = new DialogRequest(name, execute, premise);
        OPERATION_MAP.put(name, operation);
        return operation;
    }

    public static Collection<DialogRequest> getOperations() {
        return OPERATION_MAP.values();
    }

    public static DialogRequest getRequest(String name) {
        return OPERATION_MAP.getOrDefault(name, DUMMY_OPERATION);
    }

    public boolean canExecute(PlayerEntity player, OutsiderEntity outsider) {
        return premise.apply(new Tuple<>(player, outsider));
    }

    public void Execute(PlayerEntity player, OutsiderEntity outsider) {
        execute.accept(new Tuple<>(player, outsider));
    }

    public String getName() {
        return name;
    }

    public String getTranslateKey() {
        return "dialog." + VanityMod.MOD_ID + "." + name;
    }
}
