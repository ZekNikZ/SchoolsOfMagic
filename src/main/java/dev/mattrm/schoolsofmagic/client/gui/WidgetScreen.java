package dev.mattrm.schoolsofmagic.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.mattrm.schoolsofmagic.client.gui.widget.AbstractWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.Map;

public abstract class WidgetScreen extends Screen {
    private Map<Class<? extends AbstractWidget>, List<AbstractWidget>> widgets;
    private List<WidgetScreen> plugins;

    protected int compX;
    protected int compY;
    protected int x;
    protected int y;
    protected int screenWidth;
    protected int screenHeight;

    protected WidgetScreen parent;

    public WidgetScreen(ITextComponent titleIn, int x, int y, int width, int height, @Nullable WidgetScreen parent) {
        super(titleIn);
        this.widgets = Maps.newHashMap();
        this.plugins = Lists.newArrayList();
        this.x = x;
        this.y = y;
        this.screenWidth = width;
        this.screenHeight = height;
        this.parent = parent;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    protected void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    protected void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public final void bindTexture(ResourceLocation loc) {
        this.minecraft.getTextureManager().bindTexture(loc);
    }

    public final void renderItemStack(ItemStack itemStack, int x, int y) {
        this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI((LivingEntity) null, itemStack, x, y);
    }

    protected final void registerPlugin(WidgetScreen plugin) {
        this.plugins.add(plugin);
    }

    @Override
    public void init(Minecraft mc, int scaledWidth, int scaledHeight) {
        super.init(mc, scaledWidth, scaledHeight);
        this.plugins.forEach(p -> p.init(mc, scaledWidth, scaledHeight));
    }

    protected final void addWidget(AbstractWidget widget) {
        this.widgets.computeIfAbsent(widget.getClass(), k -> Lists.newArrayList()).add(widget);
    }

    protected final void renderWidgets() {
        this.widgets.values().forEach(l -> l.forEach(AbstractWidget::preRender));
        this.widgets.values().forEach(l -> l.forEach(AbstractWidget::render));
        this.widgets.values().forEach(l -> l.forEach(AbstractWidget::postRender));
    }

    protected final void renderWidgets(Class<? extends AbstractWidget> clazz) {
        this.widgets.get(clazz).forEach(AbstractWidget::preRender);
        this.widgets.get(clazz).forEach(AbstractWidget::render);
        this.widgets.get(clazz).forEach(AbstractWidget::postRender);
    }

    protected final void checkWidgetHover(double mouseX, double mouseY) {
        this.widgets.values().forEach(l -> l.forEach(w -> {
            if (w.inBounds(mouseX, mouseY)) {
                w.onHover(mouseX - w.bounds.getX1(), mouseY - w.bounds.getY1());
            }
        }));
    }

    protected final void checkWidgetHover(Class<? extends AbstractWidget> clazz, double mouseX, double mouseY) {
        this.widgets.get(clazz).forEach(w -> {
            if (w.inBounds(mouseX, mouseY)) {
                w.onHover(mouseX - w.bounds.getX1(), mouseY - w.bounds.getY1());
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.widgets.values().forEach(l -> l.forEach(w -> {
            if (w.inBounds(mouseX - this.compX, mouseY - this.compY)) {
                w.onClick(mouseX - this.compX - w.bounds.getX1(), mouseY - this.compY - w.bounds.getY1(), button);
            }
        }));

        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected abstract void renderRelative(int relMouseX, int relMouseY, float partialTicks);

    @OverridingMethodsMustInvokeSuper
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.x == -1) {
            compX = (this.width - this.screenWidth) / 2;
        }
//        else if (this.parent != null) {
//            if (this.parent.getX() == -1) {
//                compX = this.x + (this.width - this.parent.getScreenWidth()) / 2;
//            } else {
//                compX = this.x + this.parent.getX();
//            }
//        }
        else {
            compX = this.x;
        }

        if (this.y == -1) {
            compY = (this.height - this.screenHeight) / 2;
        }
//        else if (this.parent != null) {
//            if (this.parent.getY() == -1) {
//                compY = this.y + (this.height - this.parent.getScreenHeight()) / 2;
//            } else {
//                compY = this.y + this.parent.getY();
//            }
//        }
        else {
            compY = this.y;
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef(compX, compY, 0.0f);
        this.renderRelative(mouseX - compX, mouseY - compY, partialTicks);
        RenderSystem.popMatrix();
    }
}


