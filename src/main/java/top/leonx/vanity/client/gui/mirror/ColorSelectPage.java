package top.leonx.vanity.client.gui.mirror;

import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.util.math.MathHelper;
import top.leonx.vanity.client.screen.VanityMirrorScreen;
import top.leonx.vanity.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ColorSelectPage {

    private ToggleWidget       forwardButton;
    private ToggleWidget       backButton;
    private List<ColorSelectButton> buttons   = new ArrayList<>();
    private List<Integer>      allColors =new ArrayList<>();
    private int currentColor;
    private final int numberPerPage=9;
    int totalPage;
    int currentPage;
    Consumer<Integer> onColorSelected;
    public ColorSelectPage(Consumer<Integer> onColorSelected){
        this.onColorSelected=onColorSelected;
    }
    public void init(int x, int y, int width, int height)
    {
        this.forwardButton = new ToggleWidget(x+width-12, y, 12, 17, false);
        this.forwardButton.initTextureValues(192, 160, 13, 32, VanityMirrorScreen.VANITY_TEX);
        this.backButton = new ToggleWidget(x, y, 12, 17, false);
        this.backButton.initTextureValues(208, 160, 13, 32, VanityMirrorScreen.VANITY_TEX);

        buttons.clear();
        int xStart=x+16;
        for(int i=0;i<numberPerPage;i++)
        {
            ColorSelectButton colorBtn=new ColorSelectButton(xStart,y+1,16,16,false);
            buttons.add(colorBtn);
            xStart+=16;
        }
        allColors.clear();
        allColors.addAll(ColorUtil.COLORS);
        totalPage= (int) Math.ceil(allColors.size()/(float)numberPerPage);

        updateShowingColor();
    }
    public void updateCurrentColor(int color)
    {
        currentColor=color;
        int i = allColors.indexOf(currentColor);
        currentPage=i/numberPerPage;
        buttons.forEach(t->t.setStateTriggered(false));
        buttons.get(MathHelper.clamp(i%numberPerPage,0, buttons.size()-1)).setStateTriggered(true);
        updateShowingColor();
    }
    public void render(int mouseX,int mouseY,float partialTicks)
    {

        forwardButton.render(mouseX,mouseY,partialTicks);
        backButton.render(mouseX,mouseY,partialTicks);
        for (ToggleWidget button : buttons) {
            button.render(mouseX,mouseY,partialTicks);
        }
    }

    public void mouseClicked(double mouseX,double mouseY,int id)
    {
        if(forwardButton.mouseClicked(mouseX,mouseY,id))
        {
            currentPage=Math.min(totalPage,currentPage+1);
            updateShowingColor();
        }else if(backButton.mouseClicked(mouseX,mouseY,id))
        {
            currentPage=Math.max(0,currentPage-1);
            updateShowingColor();
        }else {
            for (ColorSelectButton button : buttons) {
                if(button.mouseClicked(mouseX,mouseY,id))
                {
                    buttons.forEach(t->t.setStateTriggered(false));
                    button.setStateTriggered(true);
                    currentColor=button.getColor();
                    onColorSelected.accept(button.getColor());
                }
            }
        }
    }

    private void updateShowingColor()
    {
        int startIndex=currentPage*numberPerPage;
        for(int i=startIndex;i<startIndex+numberPerPage;i++)
        {
            ColorSelectButton button = buttons.get(i - startIndex);
            if(i<allColors.size())
            {
                button.visible=true;
                button.setColor(allColors.get(i));
                button.setStateTriggered(currentColor==allColors.get(i));
            }else
            {
                button.visible=false;
            }
        }
    }
}
