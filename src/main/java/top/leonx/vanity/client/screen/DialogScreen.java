package top.leonx.vanity.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.gui.vanity.DialogButton;
import top.leonx.vanity.container.OutsiderContainer;

import java.util.ArrayList;
import java.util.List;

public class DialogScreen extends ContainerScreen<OutsiderContainer> {
    public static final ResourceLocation DIALOG_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/dialog.png");
    int halfH,halfW,panelLeft,panelTop,panelBottom,panelWidth=300;
    private final List<DialogButton> dialogButtons=new ArrayList<>();
    public DialogScreen(OutsiderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        halfH=height/2;
        halfW=width/2;
        panelLeft=halfW-panelWidth/2;
        panelTop=height-96;
        panelBottom=panelTop+80;
        dialogButtons.clear();

        int dialogButtonStartX=panelLeft+4;
        int dialogButtonWidth=64;
        int dialogButtonMargin=2;
        for (int i = 0; i < 4; i++) {
            DialogButton dialogButton = new DialogButton(dialogButtonStartX + i * (dialogButtonWidth + dialogButtonMargin), panelBottom - 34, dialogButtonWidth, 32);
            dialogButtons.add(dialogButton);
            this.addButton(dialogButton);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //this.renderBackground();
        Minecraft.getInstance().textureManager.bindTexture(DIALOG_TEX);
        blit(panelLeft,height-90,0,0,panelWidth,80,panelWidth,256);
    }
}
