package top.leonx.vanity.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.entity.OfflineOutsider;
import top.leonx.vanity.util.RenderUtil;

import java.util.function.Consumer;

public class Avatar extends VanityWidget {
    static final ResourceLocation AVATAR_TEX = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/avatar.png");
    final int widthInTex  = 48;
    final int heightInTex = 64;
    public Consumer<Avatar> onTriggered;
    OfflineOutsider outsider;
    public boolean triggered = false;
    public boolean active=true;
    public boolean visible=true;
    private Label nameLabel;
    public Avatar(int x, int y, int width, int height, OfflineOutsider outsider) {
        super(x, y, width, height);
        this.outsider = outsider;
        nameLabel=new Label(()->outsider.getCustomName().getFormattedText(),x,y+40,width,24).setCenter(true);
        addChild(nameLabel);
    }

    @Override
    public void init() {

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        fill(getAbsulateX(), getAbsulateY(), getAbsulateX() + width, getAbsulateY() + height, 0xFF373737);
        RenderUtil.drawOfflineOutsiderOnScreen(getAbsulateX() + width / 2, getAbsulateY() + height, Math.min(width, height) / 2, 0, 0, 30, outsider);
        Minecraft.getInstance().textureManager.bindTexture(AVATAR_TEX);
        float scaleFactorW = width / (float)widthInTex;
        float scaleFactorH = height / (float)heightInTex;

        int i = triggered ? widthInTex : 0;
        int j = isMouseOver(mouseX, mouseY) ? heightInTex : 0;
        blit(getAbsulateX(), getAbsulateY(), (int) (i * scaleFactorW), (int) (j * scaleFactorH), this.width, this.height, (int) (256 * scaleFactorW), (int) (256 * scaleFactorH));
        super.render(mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int partialTick) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        triggered = !triggered;
        this.playDownSound(Minecraft.getInstance().getSoundHandler());

        if (onTriggered == null) return true;
        onTriggered.accept(this);
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)getAbsulateX() && mouseY >= (double)getAbsulateY() && mouseX < (double)(getAbsulateX() + this.width) && mouseY < (double)(getAbsulateY() + this.height);
    }
}
