package top.leonx.vanity.client.gui.vanity;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.resources.I18n;
import top.leonx.vanity.bodypart.*;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.client.screen.VanityMirrorScreen;
import top.leonx.vanity.hair.IHasIcon;
import top.leonx.vanity.init.ModCapabilityTypes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class WrapVanityItemPage {
    private final List<VanityItemWidget> buttons = Lists.newArrayListWithCapacity(20);
    private        VanityItemWidget       hoveredButton;
    private        Minecraft              minecraft;
    private        ToggleWidget           forwardButton;
    private        ToggleWidget           backButton;
    private        int                    totalPages;
    private        int     currentPage;
    private List<BodyPart> itemList;
    private IHasIcon       lastClickedItem;
    private       Queue<BodyPartStack> selectedItem;
    private       BodyPartGroup        currentGroup;

    private final VanityMirrorScreen     parent;
    private WrapPanel adjustPanel;

    public WrapVanityItemPage(VanityMirrorScreen parent) {
        for(int i = 0; i < 20; ++i) {
            this.buttons.add(new VanityItemWidget());
        }
        this.parent=parent;

    }

    public void init(Minecraft mc, int x, int y) {
        this.minecraft = mc;

        for(int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).setPosition(x + 11 + 25 * (i % 5), y + 31 + 25 * (i / 5));
        }

        this.forwardButton = new ToggleWidget(x + 93, y + 137, 12, 17, false);
        this.forwardButton.initTextureValues(208, 160, 13, 32, VanityMirrorScreen.VANITY_TEX);
        this.backButton = new ToggleWidget(x + 38, y + 137, 12, 17, true);
        this.backButton.initTextureValues(192, 160, 13, 32, VanityMirrorScreen.VANITY_TEX);
        adjustPanel=new WrapPanel(x,y+144,176,32);
    }


    public void updateLists(BodyPartGroup group, Queue<BodyPartStack> selectedItem, boolean backToFirst) {
        currentGroup=group;
        this.selectedItem=selectedItem;
        CharacterState characterState = parent.getContainer().getPlayer().getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(CharacterState.EMPTY);
        this.itemList= BodyPartRegistry.getBodyParts(group).stream().filter(t->t.getSuitableGender().isSuitable(characterState.getGender())).collect(Collectors.toList());
        this.totalPages = (int)Math.ceil((double)itemList.size() / 20.0D);
        if (this.totalPages <= this.currentPage || backToFirst) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int i = 20 * this.currentPage;

        for(int j = 0; j < this.buttons.size(); ++j) {
            VanityItemWidget vanityItemWidget = this.buttons.get(j);
            if (i + j < this.itemList.size()) {
                BodyPart icon = this.itemList.get(i + j);
                vanityItemWidget.init(icon, this);
                vanityItemWidget.visible = true;
                vanityItemWidget.setStateTriggered(selectedItem.stream().anyMatch(t->t.getItem().equals(icon)));

            } else {
                vanityItemWidget.visible = false;
            }
        }

        this.updateArrowButtons();
        this.updateAdjustSlider();
    }

    private void updateAdjustSlider() {
        adjustPanel.children.clear();
        for (BodyPartStack bodyPartStack : selectedItem) {
            List<BodyPartProperty.AdjustableAttribute> attributes = bodyPartStack.getItem().getProperty().adjustableAttributes;
            for (BodyPartProperty.AdjustableAttribute attribute : attributes) {
                float defaultV = attribute.getDefaultV();
                float value=bodyPartStack.getAdjustableAttributes().getOrDefault(attribute.getName(),defaultV);
                adjustPanel.children.add(new AdjustSlider(0, 0, 80, I18n.format(attribute.getTranslateKey())+":","",attribute.getMin(),attribute.getMax(),value,true,true,t->{},
                t-> bodyPartStack.getAdjustableAttributes().put(attribute.getName(), (float) t.getValue())));
            }
        }
    }

    private void updateArrowButtons() {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    public void render(int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (this.totalPages > 1) {
            String s = this.currentPage + 1 + "/" + this.totalPages;
            int i = this.minecraft.fontRenderer.getStringWidth(s);
            this.minecraft.fontRenderer.drawString(s, (float)(x - i / 2 + 73), (float)(y + 141), -1);
        }

        this.hoveredButton = null;

        for(VanityItemWidget VanityItemWidget : this.buttons) {
            VanityItemWidget.render(mouseX, mouseY, partialTicks);
            if (VanityItemWidget.visible && VanityItemWidget.isHovered()) {
                this.hoveredButton = VanityItemWidget;
            }
        }

        this.backButton.render(mouseX, mouseY, partialTicks);
        this.forwardButton.render(mouseX, mouseY, partialTicks);
        adjustPanel.render(mouseX,mouseY,partialTicks);
        //this.overlay.render(mouseX, mouseY, partialTicks);
    }

    public void renderTooltip(int p_193721_1_, int p_193721_2_) {
        if (this.minecraft.currentScreen != null && this.hoveredButton != null) {
            this.minecraft.currentScreen.renderTooltip(this.hoveredButton.getToolTipText(this.minecraft.currentScreen), p_193721_1_, p_193721_2_);
        }

    }

    @Nullable
    public IHasIcon getLastClickedItem() {
        return this.lastClickedItem;
    }


    public void setInvisible() {
        //this.overlay.setVisible(false);
    }

    public boolean onClick(double mouseX, double mouseY, int id, int x, int y, int width, int height) {
        this.lastClickedItem = null;
        if (this.forwardButton.mouseClicked(mouseX, mouseY, id)) {
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else if (this.backButton.mouseClicked(mouseX, mouseY, id)) {
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else if (this.adjustPanel.mouseClicked(mouseX, mouseY, id)){
            return true;
        }else {
            for(VanityItemWidget vanityItemWidget : this.buttons) {
                if (vanityItemWidget.mouseClicked(mouseX, mouseY, id)) {
                    if (id == 0) {
                        if(selectedItem.stream().anyMatch(t->t.getItem().equals(vanityItemWidget.getItem())))
                            selectedItem.removeIf(t->vanityItemWidget.getItem().equals(t.getItem()));
                        else
                            selectedItem.add(new BodyPartStack(vanityItemWidget.getItem(), parent.currentColor));

                        this.lastClickedItem = vanityItemWidget.getItem();
                        checkSelectedItemSize();
                        updateAdjustSlider();
                    }
                    return true;
                }
            }
        }
        return false;
    }
    private void checkSelectedItemSize()
    {
        for(int i=0;i<selectedItem.size()-currentGroup.getMaxStack();i++)
        {
            selectedItem.poll();
        }
        for(VanityItemWidget vanityItemWidget : this.buttons) {
            vanityItemWidget.setStateTriggered(selectedItem.stream().anyMatch(t->t.getItem().equals(vanityItemWidget.getItem())));
        }
    }

    public Minecraft getMinecraft()
    {
        return minecraft;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int id) {
        return adjustPanel.mouseReleased(mouseX, mouseY, id);
    }
}
