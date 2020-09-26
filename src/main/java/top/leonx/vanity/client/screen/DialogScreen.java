package top.leonx.vanity.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.client.gui.Label;
import top.leonx.vanity.client.gui.dialog.DialogButton;
import top.leonx.vanity.client.gui.dialog.ProcessBar;
import top.leonx.vanity.container.OutsiderDialogContainer;
import top.leonx.vanity.entity.DialogRequest;

import java.util.ArrayList;
import java.util.List;

public class DialogScreen extends ContainerScreen<OutsiderDialogContainer> {
    public static final ResourceLocation DIALOG_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/dialog.png");
    int halfH,halfW, dialogPanelLeft, dialogPanelTop, dialogPanelBottom, dialogPanelWidth =256,dialogPanelHeight=80;
    int infoPanelLeft,infoPanelTop,infoPanelBottom,infoPanelWidth=80,infoPanelHeight=96;
    private final List<DialogButton> dialogButtons=new ArrayList<>();
    private final List<Label> labels=new ArrayList<>();
    private ProcessBar relationShipProcessBar;
    private ProcessBar loveShipProcessBar;
    public DialogScreen(OutsiderDialogContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        halfH=height/2;
        halfW=width/2;
        infoPanelLeft=halfW-(dialogPanelWidth+infoPanelWidth)/2;
        infoPanelTop=height-infoPanelHeight-16;
        infoPanelBottom=infoPanelTop+infoPanelHeight;

        dialogPanelLeft =infoPanelLeft+infoPanelWidth;
        dialogPanelTop =height-dialogPanelHeight-16;
        dialogPanelBottom = dialogPanelTop +dialogPanelHeight;
        updateDialogButtons();
        addLabel(new Label(()-> container.outsider.getName().getString()).setXY(infoPanelLeft+4,infoPanelTop+4));
        addLabel(new Label("Relationship").setXY(infoPanelLeft+4,infoPanelTop+24));
        relationShipProcessBar= new ProcessBar(infoPanelLeft+4,infoPanelTop+32,68,5).setMaxValue(CharacterState.MAX_RELATIONSHIP);
        addLabel(new Label("Love").setXY(infoPanelLeft+4,infoPanelTop+40));
        loveShipProcessBar= new ProcessBar(infoPanelLeft+4,infoPanelTop+48,68,5).setMaxValue(CharacterState.MAX_LOVE);
        loveShipProcessBar.type= ProcessBar.Type.PINK;
    }
    private void addLabel(Label label)
    {
        this.labels.add(label);
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //this.renderBackground();
        Minecraft.getInstance().textureManager.bindTexture(DIALOG_TEX);
        blit(infoPanelLeft,infoPanelTop,0,160,infoPanelWidth,infoPanelHeight);
        blit(dialogPanelLeft, dialogPanelTop, 0, 0, dialogPanelWidth, dialogPanelHeight);
    }
    private int dialogButtonPage=0;
    private void updateDialogButtons()
    {
        buttons.removeAll(dialogButtons);
        dialogButtons.clear();

        final int dialogButtonStartX= dialogPanelLeft +4;
        final int dialogButtonWidth=96;
        final int dialogButtonMargin=2;
        final int buttonNumPerPage=3;

        for (int i = dialogButtonPage*buttonNumPerPage;
             i < Math.min(container.availableRequests.size(),(dialogButtonPage+1)*buttonNumPerPage);
             i++) {

            DialogButton  dialogButton = new DialogButton(dialogButtonStartX + i * (dialogButtonWidth + dialogButtonMargin), dialogPanelBottom - 34, dialogButtonWidth, 48,"",null);
            DialogRequest request      = container.availableRequests.get(i);
            dialogButton.setMessage(I18n.format(request.getTranslateKey()));
            dialogButton.onPress=e->container.requestOperation(request);
            dialogButtons.add(dialogButton);
            addButton(dialogButton);
        }
    }
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        relationShipProcessBar.render(mouseX,mouseY,partialTicks);
        loveShipProcessBar.render(mouseX,mouseY,partialTicks);
        for (Label label : labels) {
            label.render(mouseX,mouseY,partialTicks);
        }
    }

    @Override
    public void tick() {
        super.tick();
        relationShipProcessBar.value=container.outsider.getCharacterState().getRelationWith(container.getPlayer().getUniqueID());
        loveShipProcessBar.value=container.outsider.getCharacterState().getLoveWith(container.getPlayer().getUniqueID());

    }
}
