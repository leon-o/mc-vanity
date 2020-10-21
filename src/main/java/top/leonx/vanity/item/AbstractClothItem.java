package top.leonx.vanity.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.leonx.vanity.util.Color;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.capability.ICurio;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractClothItem extends Item implements IDyeableArmorItem {

    public AbstractClothItem() {
        super(new Properties().group(ItemGroup.DECORATIONS));
    }
    @OnlyIn(Dist.CLIENT)
    Model model;

    @OnlyIn(Dist.CLIENT)
    public Model getClothModel(LivingEntity entity){
        if(model==null)
            model=createModel(entity);
        return model;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract RenderType getRenderType();

    @OnlyIn(Dist.CLIENT)
    public abstract Model createModel(LivingEntity entity);

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, LazyOptional.of(ClothesCurio::new));
            }
        };
    }

    public int getColor(ItemStack stack) {
        CompoundNBT compoundnbt = stack.getChildTag("display");
        return compoundnbt != null && compoundnbt.contains("color", 99) ? compoundnbt.getInt("color") : 0xe1bea6;//0xF9E8E8;
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract void render(String identifier, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer,
                                   int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);

    public Color getColor(String identifier,LivingEntity entity)
    {
        LazyOptional<ItemStack> map = CuriosAPI.getCuriosHandler(entity).map(handler -> {
            CurioStackHandler stacks = handler.getCurioMap().get(identifier);
            for(int i=0;i<stacks.getSlots();i++)
            {
                ItemStack itemStack = stacks.getStackInSlot(i);
                if(itemStack.getItem().equals(AbstractClothItem.this))
                    return itemStack;
            }
            return new ItemStack(AbstractClothItem.this);
        });
        int color=0xFFFFFF;
        if(map.isPresent())
        {
            color=getColor(map.orElseThrow(NullPointerException::new));
        }
        float r=(color>>>16) / 255f;
        float g=((color & 0x00FF00)>>8) / 255f;
        float b=(color & 0x0000FF) / 255f;

        return new Color(1,r,g,b);
    }
    public class ClothesCurio implements ICurio
    {
        @Override
        public boolean canRightClickEquip() {
            return true;
        }

        @Override
        public boolean hasRender(String identifier, LivingEntity livingEntity) {
            return true;
        }
        @Override
        public void render(String identifier, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            /*EntityRenderer<? super LivingEntity> render = Minecraft.getInstance().getRenderManager().getRenderer(livingEntity);
            if (render instanceof LivingRenderer) {
                LivingRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer = (LivingRenderer) render;
                EntityModel<LivingEntity>                               model          = livingRenderer.getEntityModel();
                if (model instanceof BipedModel) {
                    BipedModel        bipedModel = (BipedModel) model;
                    AbstractHairModel hair       = getHairModel();

                    bipedModel.getModelHead().translateRotate(matrixStack);
                    IVertexBuilder buffer =renderTypeBuffer.getBuffer(getRenderType());
                    hair.applyPhysic(livingEntity, partialTicks);
                    LazyOptional<ItemStack> map = CuriosAPI.getCuriosHandler(livingEntity).map(handler -> {
                        CurioStackHandler stacks = handler.getCurioMap().get(identifier);
                        for(int i=0;i<stacks.getSlots();i++)
                        {
                            ItemStack itemStack = stacks.getStackInSlot(i);
                            if(itemStack.getItem().equals(AbstractClothItem.this))
                                return itemStack;
                        }
                        return new ItemStack(AbstractClothItem.this);
                    });
                    int color=0xFFFFFF;
                    if(map.isPresent())
                    {
                        color=getColor(map.orElseThrow(NullPointerException::new));
                    }
                    float r=(color>>>16) / 255f;
                    float g=((color & 0x00FF00)>>8) / 255f;
                    float b=(color & 0x0000FF) / 255f;
                    hair.render(matrixStack, buffer, light, OverlayTexture.NO_OVERLAY, r, g, b, 1f);
                }
            }*/

            AbstractClothItem.this.render(identifier, matrixStack, renderTypeBuffer, light, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

/*        @OnlyIn(Dist.CLIENT)
        @Override
        public void onEquipped(String identifier, LivingEntity livingEntity) {
            AbstractClothItem.this.modelMap.put(livingEntity,createModel(livingEntity));
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void onUnequipped(String identifier, LivingEntity livingEntity) {
            AbstractClothItem.this.modelMap.remove(livingEntity,createModel(livingEntity));
        }*/
    }
}
