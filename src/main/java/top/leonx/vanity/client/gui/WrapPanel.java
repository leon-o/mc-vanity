package top.leonx.vanity.client.gui;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class WrapPanel<T extends LayoutElement & IGuiEventListener> extends VanityWidget {
    private final List<NonNullList<T>> pagedChildren = new ArrayList<>();
    public        NonNullList<T>       children      = NonNullList.create();
    public PageTurnButton pageTurnButton;
    int pageIndex;
    public WrapPanel(int x, int y, int width, int height) {
        super(x, y, width, height);
        pageTurnButton=new PageTurnButton(0,height-20,width,18,t-> this.pageIndex= t);
        addChild(pageTurnButton);
    }

    @Override
    public void init() {
        updateLayout();
        pageTurnButton.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        if(pageIndex<0 || pageIndex >= pagedChildren.size()) return;

        NonNullList<T> childrenInPage = pagedChildren.get(pageIndex);
        for (T t : childrenInPage) {
            t.render(mouseX,mouseY,partialTick);
        }

        super.render(mouseX, mouseY, partialTick);
    }

    public void updateLayout() {
        pagedChildren.clear();
        int            currentX    = 0, currentY = 0;
        NonNullList<T> currentPage = NonNullList.create();
        pagedChildren.add(currentPage);
        for (T child : children) {

            if (currentX+child.width+child.marginLeft+child.marginRight >= width && currentPage.size()>=1) {
                currentX = 0;
                Optional<Integer> maxHeightOpt = currentPage.stream().map(t -> t.height+t.marginBottom+t.marginTop).max(Comparator.naturalOrder());
                currentY += maxHeightOpt.get();
            }
            if(currentY+child.height+child.marginTop+child.marginBottom>=height)
            {
                currentPage = NonNullList.create();
                pagedChildren.add(currentPage);//start new page
                currentY=0;
            }
            child.setXY(getAbsulateX()+currentX+child.marginLeft,getAbsulateY()+currentY+child.marginTop);
            currentPage.add(child);
            currentX+=child.width+child.marginLeft+child.marginRight;
        }
        pagedChildren.add(currentPage);
        pageTurnButton.totalPages=pagedChildren.size();
    }
    public NonNullList<T> getCurrentPageChildren()
    {
        if(pageIndex<0 || pageIndex >= pagedChildren.size()) return NonNullList.create();
        return pagedChildren.get(pageIndex);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result=false;
        for (IGuiEventListener child : getCurrentPageChildren()) {
            result|=child.mouseClicked(mouseX, mouseY, button);
        }
        result|=pageTurnButton.mouseClicked(mouseX, mouseY, button);
        return result;
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean result=false;
        for (IGuiEventListener child : getCurrentPageChildren()) {
            result|=child.mouseReleased(mouseX, mouseY, button);
        }
        result|=pageTurnButton.mouseReleased(mouseX, mouseY, button);
        return result;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        boolean result=false;
        for (IGuiEventListener child : getCurrentPageChildren()) {
            result|=child.mouseDragged(mouseX, mouseY, button, p_mouseDragged_6_, p_mouseDragged_8_);
        }
        result|=pageTurnButton.mouseDragged(mouseX, mouseY, button, p_mouseDragged_6_, p_mouseDragged_8_);
        return result;
    }
}
