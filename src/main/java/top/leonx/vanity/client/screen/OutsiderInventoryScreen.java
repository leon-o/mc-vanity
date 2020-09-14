package top.leonx.vanity.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.container.OutsiderInventoryContainer;

public class OutsiderInventoryScreen extends ContainerScreen<OutsiderInventoryContainer> {
    public static final ResourceLocation INVENTORY_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/outsider_inventory.png");

    public OutsiderInventoryScreen(OutsiderInventoryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        renderBackground();
        Minecraft.getInstance().getTextureManager().bindTexture(INVENTORY_TEX);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i-32, j, 0, 0, 208, 224);
    }
}
