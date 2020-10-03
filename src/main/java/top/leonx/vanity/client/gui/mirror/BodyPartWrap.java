package top.leonx.vanity.client.gui.mirror;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.ChangePageButton;

import java.util.ArrayList;
import java.util.List;

public class BodyPartWrap extends AbstractGui implements IRenderable, IGuiEventListener {
    int x,y,width,height;
    public BodyPartWrap(int xIn, int yIn, int widthIn, int heightIn) {
        x=xIn;
        y=yIn;
        width=widthIn;
        height=heightIn;
        prevPageButton = new ChangePageButton(xIn, yIn + heightIn - 13, false, $ -> {
            pageNumber=Math.max(0,--pageNumber);
            if(pageNumber==0)
                prevPageButton.active=false;
            nextPageButton.active=true;
        }, true);
        nextPageButton = new ChangePageButton(xIn+widthIn-23, yIn + heightIn - 13, true, $ -> {
            int maxPage=children.size()/entryCountEachPage;
            pageNumber=Math.min(maxPage,++pageNumber);
            if(pageNumber>=maxPage)
                nextPageButton.active=false;
            prevPageButton.active=true;
        }, true);
    }
    ChangePageButton prevPageButton,nextPageButton;
    public final List<Widget> children           =new ArrayList<>();
    public       int          entryCountEachPage =4;
    public int pageNumber=0;
    public int marginRight=8;
    public int marginLeft=8;
    public int marginTop=4;

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        //super.render(mouseX, mouseY, partialTick);
        int left=0,top=0,maxHeightThisLine=0;
        for (int i=pageNumber*entryCountEachPage;i<Math.min(children.size(),(pageNumber+1)*entryCountEachPage);i++) {
            Widget widget=children.get(i);
            if(left+widget.getWidth()>width)
            {
                left=0;
                top+=maxHeightThisLine+marginTop;
                maxHeightThisLine=0;
            }
            maxHeightThisLine=Math.max(widget.getHeight(),maxHeightThisLine);
            widget.x=x+left+marginLeft;
            widget.y=y+top;
            widget.render(mouseX, mouseY, partialTick);
            left+=widget.getWidth()+marginRight;
        }
        prevPageButton.render(mouseX, mouseY, partialTick);
        nextPageButton.render(mouseX, mouseY, partialTick);
        prevPageButton.renderButton(mouseX, mouseY, partialTick);
        nextPageButton.renderButton(mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int btn) {
        boolean flag=false;
        for (int i=pageNumber*entryCountEachPage;i<Math.min(children.size(),(pageNumber+1)*entryCountEachPage);i++) {
            flag |=children.get(i).mouseClicked(mouseX,mouseY,btn);
        }
        flag |=prevPageButton.mouseClicked(mouseX,mouseY,btn);
        flag |=nextPageButton.mouseClicked(mouseX,mouseY,btn);
        return flag;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int btn) {
        boolean flag=false;
        for (int i=pageNumber*entryCountEachPage;i<Math.min(children.size(),(pageNumber+1)*entryCountEachPage);i++) {
            flag |=children.get(i).mouseReleased(mouseX,mouseY,btn);
        }
        flag |=prevPageButton.mouseReleased(mouseX,mouseY,btn);
        flag |=nextPageButton.mouseReleased(mouseX,mouseY,btn);
        return flag;
    }
}
