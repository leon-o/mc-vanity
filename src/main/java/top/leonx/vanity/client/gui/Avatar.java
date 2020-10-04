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

    public Avatar(int x, int y, int width, int height, OfflineOutsider outsider) {
        super(x, y, width, height);
        this.outsider = outsider;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        fill(x, y, x + width, y + height, 0xFF373737);
        RenderUtil.drawOfflineOutsiderOnScreen(x + width / 2, y + height, Math.min(width, height) / 2, 0, 0, 30, outsider);
        Minecraft.getInstance().textureManager.bindTexture(AVATAR_TEX);
        float scaleFactorW = width / (float)widthInTex;
        float scaleFactorH = height / (float)heightInTex;

        int i = triggered ? widthInTex : 0;
        int j = isMouseOver(mouseX, mouseY) ? heightInTex : 0;
        blit(this.x, this.y, (int) (i * scaleFactorW), (int) (j * scaleFactorH), this.width, this.height, (int) (256 * scaleFactorW), (int) (256 * scaleFactorH));
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
        return this.active && this.visible && mouseX >= (double)this.x && mouseY >= (double)this.y && mouseX < (double)(this.x + this.width) && mouseY < (double)(this.y + this.height);
    }
}
