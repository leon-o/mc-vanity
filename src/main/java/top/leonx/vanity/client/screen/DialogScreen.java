package top.leonx.vanity.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.gui.dialog.DialogButton;
import top.leonx.vanity.client.gui.dialog.ProcessBar;
import top.leonx.vanity.container.OutsiderContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DialogScreen extends ContainerScreen<OutsiderContainer> {
    public static final ResourceLocation DIALOG_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/dialog.png");
    int halfH,halfW, dialogPanelLeft, dialogPanelTop, dialogPanelBottom, dialogPanelWidth =256,dialogPanelHeight=80;
    int infoPanelLeft,infoPanelTop,infoPanelBottom,infoPanelWidth=80,infoPanelHeight=96;
    private final List<DialogButton> dialogButtons=new ArrayList<>();
    private ProcessBar relationShipProcessBar;
    private ProcessBar loveShipProcessBar;
    public DialogScreen(OutsiderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        halfH=height/2;
        halfW=width/2;
        infoPanelLeft=halfW-(dialogPanelWidth+infoPanelWidth)/2;
        infoPanelTop=height-infoPanelHeight;
        infoPanelBottom=infoPanelTop+infoPanelHeight;

        dialogPanelLeft =infoPanelLeft+infoPanelWidth;
        dialogPanelTop =height-dialogPanelHeight;
        dialogPanelBottom = dialogPanelTop +dialogPanelHeight;
        dialogButtons.clear();

        int dialogButtonStartX= dialogPanelLeft +4;
        int dialogButtonWidth=48;
        int dialogButtonMargin=2;
        for (int i = 0; i < 5; i++) {
            DialogButton dialogButton = new DialogButton(dialogButtonStartX + i * (dialogButtonWidth + dialogButtonMargin), dialogPanelBottom - 34, dialogButtonWidth, 24,"",null);
            dialogButtons.add(dialogButton);
            this.addButton(dialogButton);
        }
        dialogButtons.get(0).setMessage("FOLLOW ME");
        dialogButtons.get(0).onPress=(s)->{
            boolean followed =Objects.equals(container.outsider.getCharacterState().getFollowedEntityUUID(), container.getPlayer().getUniqueID());
            if(followed)
            {
                container.requestOperation(OutsiderContainer.DISBAND);
                s.setMessage("FOLLOW ME");
            }else{
                container.requestOperation(OutsiderContainer.FOLLOW_ME);
                s.setMessage("DISBAND");
            }
        };
        relationShipProcessBar= new ProcessBar(infoPanelLeft+8,infoPanelTop+24,72,5);
        loveShipProcessBar= new ProcessBar(infoPanelLeft+8,infoPanelTop+32,72,5);
        loveShipProcessBar.type= ProcessBar.Type.PINK;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //this.renderBackground();
        Minecraft.getInstance().textureManager.bindTexture(DIALOG_TEX);
        blit(infoPanelLeft,infoPanelTop,0,160,infoPanelWidth,infoPanelHeight);
        blit(dialogPanelLeft, dialogPanelTop, 0, 0, dialogPanelWidth, dialogPanelHeight);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        relationShipProcessBar.value=partialTicks;
        relationShipProcessBar.render(mouseX,mouseY,partialTicks);
        loveShipProcessBar.value=1-partialTicks;
        loveShipProcessBar.render(mouseX,mouseY,partialTicks);
    }
}
