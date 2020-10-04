package top.leonx.vanity.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.gui.Avatar;
import top.leonx.vanity.client.gui.Label;
import top.leonx.vanity.client.gui.WrapPanel;
import top.leonx.vanity.container.BedContainer;
import top.leonx.vanity.entity.OfflineOutsider;
import top.leonx.vanity.entity.OutsiderEntity;

public class BedScreen extends ContainerScreen<BedContainer> {
    private WrapPanel<Avatar> panel;
    private Label titleLabel;
    static final ResourceLocation BED_BACKGROUND_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/bed.png");

    public BedScreen(BedContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        titleLabel=new Label("Delivery To");
    }

    @Override
    protected void init() {
        xSize=240;
        ySize=192;
        super.init();
        titleLabel.setXY(guiLeft+8,guiTop+8);

        panel=new WrapPanel<>(guiLeft+20, guiTop+26, xSize-36, ySize-36);
        for (OfflineOutsider offlineOutsider : container.entities) {
            Avatar avatar = new Avatar(0, 0, 48, 64, offlineOutsider);
            avatar.marginBottom=avatar.marginTop=2;
            avatar.marginLeft=avatar.marginRight=1;
            panel.children.add(avatar);
        }
        panel.init();
        children.add(panel);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderBackground();
        Minecraft.getInstance().textureManager.bindTexture(BED_BACKGROUND_TEX);
        this.blit(guiLeft,guiTop,0,0,xSize,ySize);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        super.render(mouseX, mouseY, partialTick);
        panel.render(mouseX, mouseY, partialTick);
        titleLabel.render(mouseX, mouseY, partialTick);
    }

/*    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result=false;
        for (IGuiEventListener child : children) {
            result|=child.mouseClicked(mouseX, mouseY, button);
        }
        return result|super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean result=false;
        for (IGuiEventListener child : children) {
            result|=child.mouseReleased(mouseX, mouseY, button);
        }
        return result|super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        boolean result=false;
        for (IGuiEventListener child : children) {
            result|=child.mouseDragged(mouseX, mouseY, button, p_mouseDragged_6_, p_mouseDragged_8_);
        }
        return result|super.mouseDragged(mouseX, mouseY, button, p_mouseDragged_6_, p_mouseDragged_8_);
    }*/
}
