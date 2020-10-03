package top.leonx.vanity.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.gui.mirror.*;
import top.leonx.vanity.container.VanityMirrorContainer;
import top.leonx.vanity.util.ColorUtil;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartStack;

import java.util.*;

import static top.leonx.vanity.util.RenderUtil.drawEntityOnScreen;

public class VanityMirrorScreen extends ContainerScreen<VanityMirrorContainer> {

    public static final ResourceLocation            VANITY_TEX         = new ResourceLocation(VanityMod.MOD_ID, "textures/gui/vanity.png");
    private final       WrapVanityItemPage          wrapVanityItemPage = new WrapVanityItemPage(this);
    private final       ColorSelectPage             colorSelectPage;
    private final       ExtendedButton              confirmButton;
    public              List<VanityTabToggleWidget> vanityTabs         = new ArrayList<>();
    public              int                         panelLeft;
    public  int                   panelTop;
    public  int                   currentColor;
    private VanityTabToggleWidget currentTab;
    public VanityMirrorScreen(VanityMirrorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        confirmButton = new ExtendedButton(0, height / 2, 40, 20, "APPLY", this::onConfirmBtnPress);
        colorSelectPage = new ColorSelectPage(this::onColorSelected);
    }

    @Override
    protected void init() {
        super.init();
        int halfW = width / 2;
        int halfH = height / 2;
        panelLeft = halfW + 24;
        panelTop = halfH - 112;
        vanityTabs.clear();
        BodyPartGroup.GROUPS.values().forEach(t -> vanityTabs.add(new VanityTabToggleWidget(t)));
        vanityTabs.forEach(t -> t.setStateTriggered(false));
        currentTab = vanityTabs.get(0);
        currentTab.setStateTriggered(true);
        wrapVanityItemPage.init(minecraft, panelLeft + 8, panelTop + 8);
        colorSelectPage.init(panelLeft + 8, panelTop + 197, 176, 16);

        //slider=new AdjustSlider(panelLeft+7,panelTop+100,96,"a","b",0,100,50,false,true,null);

        this.addButton(confirmButton);

        updatePages();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialsTick) {
        this.renderBackground();
        drawGuiContainerBackgroundLayer(partialsTick,mouseX,mouseY);
//        InventoryScreen.drawEntityOnScreen(100, height - 50, 80, 100 - mouseX, 60 - mouseY, minecraft.player);
        drawEntityOnScreen(100, height - 50, 80, 0, 0,30, minecraft.player);
        for (VanityTabToggleWidget vanityTabToggleWidget : this.vanityTabs) {
            vanityTabToggleWidget.render(mouseX, mouseY, partialsTick);
        }
        this.wrapVanityItemPage.render(panelLeft, panelTop, mouseX, mouseY, partialsTick);
        colorSelectPage.render(mouseX, mouseY, partialsTick);
        for (net.minecraft.client.gui.widget.Widget button : this.buttons) {
            button.render(mouseX, mouseY, partialsTick);
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getInstance().getTextureManager().bindTexture(VANITY_TEX);

        blit(panelLeft, panelTop, 0, 0, 192, 224, 256, 256);


        this.updateTabs();
    }

    private void updateTabs() {
        int i = panelLeft /*this.xOffset*/ - 29;
        int j = panelTop + 3;
        int k = 27;
        int l = 0;

        for (VanityTabToggleWidget vanityTabToggleWidget : this.vanityTabs) {
//            RecipeBookCategories recipebookcategories = vanityTabToggleWidget.func_201503_d();
//            if (recipebookcategories != RecipeBookCategories.SEARCH && recipebookcategories != RecipeBookCategories.FURNACE_SEARCH) {
//                if (vanityTabToggleWidget.func_199500_a(this.recipeBook)) {
//                    vanityTabToggleWidget.setPosition(i, j + 27 * l++);
//                    vanityTabToggleWidget.startAnimation(this.mc);
//                }
//            } else {
            vanityTabToggleWidget.visible = true;
            vanityTabToggleWidget.setPosition(i, j + 27 * l++);
//            }
        }

    }

    private void updatePages() {
        Queue<BodyPartStack> selectedItems = container.selectedVanityItems.computeIfAbsent(currentTab.getGroup(), t -> new LinkedList<>());
        currentColor=selectedItems.size()==0? ColorUtil.COLORS.get(0):selectedItems.peek().getColor();

        wrapVanityItemPage.updateLists(currentTab.getGroup(), selectedItems, true);
        colorSelectPage.updateCurrentColor(currentColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int id) {
        for (VanityTabToggleWidget vanityTabToggleWidget : this.vanityTabs) {
            if (vanityTabToggleWidget.mouseClicked(mouseX, mouseY, id)) {
                if (this.currentTab != vanityTabToggleWidget) {
                    this.currentTab.setStateTriggered(false);
                    this.currentTab = vanityTabToggleWidget;
                    this.currentTab.setStateTriggered(true);
                    //this.updateCollections(true);
                    updatePages();
                }
            }
        }
        if (wrapVanityItemPage.onClick(mouseX, mouseY, id, 0, 0, 0, 0)) {
            container.applyToClientPlayer();
        }

        colorSelectPage.mouseClicked(mouseX, mouseY, id);
        return super.mouseClicked(mouseX, mouseY, id);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int id) {
        return wrapVanityItemPage.mouseReleased(mouseX, mouseY, id);
    }

    private void onColorSelected(Integer color) {
        currentColor = color;
        container.selectedVanityItems.get(currentTab.getGroup()).forEach(t -> t.setColor(color));
    }

    private void onConfirmBtnPress(Button button) {
        container.updateToServer();
    }

}
