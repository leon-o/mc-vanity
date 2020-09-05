package top.leonx.vanity.client.gui.vanity;

import net.minecraft.client.gui.widget.ToggleWidget;
import top.leonx.vanity.client.screen.VanityMirrorScreen;

public class ColorSelectButton extends ToggleWidget {
    private int color;
    public ColorSelectButton(int xIn, int yIn, int widthIn, int heightIn, boolean triggered) {
        super(xIn, yIn, widthIn, heightIn, triggered);
        initTextureValues(224, 160, 16, 16, VanityMirrorScreen.VANITY_TEX);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        super.renderButton(mouseX, mouseY, partialTicks);
        fill(x+2,y+2,x+width-2,y+height-2,0xFF000000|color);
    }
}
