package top.leonx.vanity.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.container.OutsiderInventoryContainer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class OutsiderRequest {
    private final static Map<String, OutsiderRequest> OPERATION_MAP   = new HashMap<>();
    public static final  OutsiderRequest              FOLLOW_ME       = OutsiderRequest.create("follow_me", tuple -> {
        if (tuple.getA() instanceof ServerPlayerEntity) tuple.getB().setFollowedPlayer((ServerPlayerEntity) tuple.getA());
    }, tuple -> tuple.getB().getCharacterState().getRelationWith(tuple.getA().getUniqueID()) > CharacterState.MAX_RELATIONSHIP / 2 && tuple.getB().getFollowedPlayer() == null);
    public static final OutsiderRequest DISBAND = OutsiderRequest.create("disband", tuple -> tuple.getB().setFollowedPlayer(null), tuple -> tuple.getB().getFollowedPlayer() != null);

    public static final OutsiderRequest OPEN_INVENTORY = OutsiderRequest.create("open_inventory", tuple -> {
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
    private final static OutsiderRequest              DUMMY_OPERATION = new OutsiderRequest("dummy", (t) -> {}, t -> true);
    private final String                                                 name;
    private final Consumer<Tuple<PlayerEntity, OutsiderEntity>>          execute;
    private final Function<Tuple<PlayerEntity, OutsiderEntity>, Boolean> premise;

    private OutsiderRequest(String opCode, Consumer<Tuple<PlayerEntity, OutsiderEntity>> execute, Function<Tuple<PlayerEntity, OutsiderEntity>, Boolean> premise) {
        this.name = opCode;
        this.execute = execute;
        this.premise = premise;
    }

    public static OutsiderRequest create(String name, Consumer<Tuple<PlayerEntity, OutsiderEntity>> execute) {
        return create(name, execute, t -> true);
    }

    public static OutsiderRequest create(String name, Consumer<Tuple<PlayerEntity, OutsiderEntity>> execute, Function<Tuple<PlayerEntity, OutsiderEntity>, Boolean> premise) {
        OutsiderRequest operation = new OutsiderRequest(name, execute, premise);
        OPERATION_MAP.put(name, operation);
        return operation;
    }

    public static Collection<OutsiderRequest> getOperations() {
        return OPERATION_MAP.values();
    }

    public static OutsiderRequest getRequest(String name) {
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
        return "outsiderRequest." + VanityMod.MOD_ID + "." + name;
    }
}
