package top.leonx.vanity.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import top.leonx.vanity.container.VanityMirrorContainer;
import top.leonx.vanity.init.ModTileEntityTypes;

import javax.annotation.Nullable;


public class VanityMirrorTileEntity extends TileEntity implements INamedContainerProvider {
    public VanityMirrorTileEntity() {
        super(ModTileEntityTypes.VANITY_MIRROR.get());
    }


    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Vanity Mirror");
    }

    @Nullable
    @Override
    public Container createMenu(int window_id, PlayerInventory inventory, PlayerEntity player) {
        return new VanityMirrorContainer(window_id,inventory,this);
    }
}
