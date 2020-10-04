package top.leonx.vanity.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ToggleWidget;

import java.util.function.Consumer;

public class PageTurnButton extends VanityWidget {

    private ToggleWidget forwardButton;
    private ToggleWidget backButton;
    public        int          totalPages=0;
    public int currentPage=0;
    public Consumer<Integer> onPageChanged;
    public boolean showNumber=true;
    public int numberColor=0xFFFFFF;
    public PageTurnButton(int x, int y, int width, int height,Consumer<Integer> onPageChanged) {
        super(x, y, width, height);

        this.onPageChanged=onPageChanged;
        this.forwardButton = new ToggleWidget(x, y, 12, 17, false);
        this.backButton = new ToggleWidget(x + width-12, y, 12, 17, true);

    }

    @Override
    public void init() {
        this.forwardButton = new ToggleWidget(getAbsulateX() + width-12, getAbsulateY(), 12, 17, false);
        this.forwardButton.initTextureValues(128, 0, 0, 32, WIDGET_TEX);
        this.backButton = new ToggleWidget(getAbsulateX(), getAbsulateY(), 12, 17, false);
        this.backButton.initTextureValues(144, 0, 0, 32, WIDGET_TEX);
        updateArrowButtons();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.backButton.render(mouseX, mouseY, partialTicks);
        this.forwardButton.render(mouseX, mouseY, partialTicks);
        if(showNumber)
        {
            String str= (currentPage + 1) +"/"+ (totalPages-1);
            int strWidth=Minecraft.getInstance().fontRenderer.getStringWidth(str);
            Minecraft.getInstance().fontRenderer.drawString(str,
                                                            getAbsulateX()+width/2f-strWidth/2f,getAbsulateY()+4,numberColor);
        }
    }

    private void updateArrowButtons() {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 2;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.forwardButton.mouseClicked(x, y, button)) {
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else if (this.backButton.mouseClicked(x, y, button)) {
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        return false;
    }

    private void updateButtonsForPage() {
        if(onPageChanged!=null)
            onPageChanged.accept(currentPage);

        updateArrowButtons();
    }
}
