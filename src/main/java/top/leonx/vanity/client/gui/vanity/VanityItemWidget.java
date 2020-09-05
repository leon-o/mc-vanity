package top.leonx.vanity.client.gui.vanity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ToggleWidget;
import top.leonx.vanity.client.screen.VanityMirrorScreen;
import top.leonx.vanity.container.VanityMirrorContainer;
import top.leonx.vanity.bodypart.AbstractBodyPart;

import java.util.ArrayList;
import java.util.List;

public class VanityItemWidget extends ToggleWidget {
    private VanityMirrorContainer container;
    private AbstractBodyPart      icon;
    private float                 time;
    private  float                  animationTime;
    private  int                    currentIndex;

    public VanityItemWidget() {
        super(0, 0, 25, 25, false);
    }

    public void init(AbstractBodyPart icon, WrapVanityItemPage vanityItemPage) {
        this.icon = icon;
        this.container = (VanityMirrorContainer) vanityItemPage.getMinecraft().player.openContainer;
        initTextureValues(192,96,32,32,VanityMirrorScreen.VANITY_TEX);
        //List<IRecipe<?>> list = p_203400_1_.getRecipes(this.book.isFilteringCraftable(this.field_203401_p));

//        for(IRecipe<?> irecipe : list) {
//            if (this.book.isNew(irecipe)) {
//                vanityItemPage.recipesShown(list);
//                this.animationTime = 15.0F;
//                break;
//            }
//        }

    }


    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        super.renderButton(mouseX,mouseY,partialTicks);
        Minecraft.getInstance().getTextureManager().bindTexture(icon.getIconLocation());
        blit(this.x+(this.width-16)/2, this.y+(this.height-16)/2, 0, 0, 16, 16,16,16);

    }


    public List<String> getToolTipText(Screen p_191772_1_) {


        return new ArrayList<>();
    }

    public int getWidth() {
        return 25;
    }

    protected boolean isValidClickButton(int id) {
        return id == 0 || id == 1;
    }

    public AbstractBodyPart getItem() {
        return icon;
    }
}
