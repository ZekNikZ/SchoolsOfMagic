package dev.mattrm.schoolsofmagic.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.SchoolsOfMagicMod;
import dev.mattrm.schoolsofmagic.client.data.unlocks.UnlockNode;
import dev.mattrm.schoolsofmagic.client.gui.widget.JournalTabWidget;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class JournalScreen extends WidgetScreen implements IUnlockViewContainer {
    private static final ResourceLocation WINDOW = new ResourceLocation(GlobalConstants.MODID, "textures/gui/journal/window.png");
    private static final int WINDOW_WIDTH = 200;
    private static final int WINDOW_HEIGHT = 200;
    private static final int INSIDE_WIDTH = 182;
    private static final int INSIDE_HEIGHT = 173;
    private static final int INSIDE_X = 9;
    private static final int INSIDE_Y = 18;

    private List<School> availableSchools;
    private School currentSchool;
    public School hoveredSchool;
    private UUID uuid;

    private SchoolUnlocksView insideView;

    public JournalScreen(UUID uuid, ITextComponent titleIn) {
        super(titleIn, -1, -1, WINDOW_WIDTH, WINDOW_HEIGHT, null);
        this.uuid = uuid;

        this.insideView = new SchoolUnlocksView(INSIDE_X, INSIDE_Y, INSIDE_WIDTH, INSIDE_HEIGHT, uuid, this, false);
        this.registerPlugin(this.insideView);

        // TODO: use capabilities to get available schools
        this.availableSchools = SchoolsOfMagicMod.getInstance().getClientSchoolManager().getAllSchools();

        this.availableSchools.sort(Comparator.comparing(School::getPriority));
        this.setCurrentSchool(this.availableSchools.get(0));

        this.createTabs();

        this.insideView.reloadNodes();
    }

    @Override
    public void onClose() {
        SchoolsOfMagicMod.getInstance().getClientUnlockManager().resetTracking();
        super.onClose();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public int getPoints() {
        // TODO: use capabilities to get points
        return 0;
    }

    @Override
    public School getCurrentSchool() {
        return currentSchool;
    }

    public void setCurrentSchool(School school) {
        this.currentSchool = school;
        this.insideView.setCurrentSchool(school);
        this.insideView.reloadCurrentNodes();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        // TODO: actually draw at actual mouseX and mouseY
        this.renderHoverTooltips(mouseX - this.compX, mouseY - this.compY, mouseX, mouseY);
        this.insideView.renderHoverTooltips(mouseX - this.insideView.compX - this.compX, mouseY - this.insideView.compY - this.compY, mouseX, mouseY);
    }

    @Override
    protected void renderRelative(final int mouseX, final int mouseY, final float partialTicks) {
        insideView.render(mouseX, mouseY, partialTicks);

        this.renderWindow();
        this.renderWidgets(JournalTabWidget.class);
    }

    public void renderHoverTooltips(final int mouseX, final int mouseY, int mouseActX, int mouseActY) {
        this.hoveredSchool = null;
        this.checkWidgetHover(JournalTabWidget.class, mouseX, mouseY);
        if (hoveredSchool != null) {
            ITextComponent title = this.hoveredSchool.getName();

            List<String> toolip = new ArrayList<>();
            toolip.add(title.getFormattedText());

            this.renderTooltip(toolip, mouseActX, mouseActY);
        }
    }

    private void renderWindow() {
        RenderSystem.translatef(0f, 0f, 300f);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bindTexture(WINDOW);
        this.blit(0, 0, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        this.font.drawString(this.title.getFormattedText(), (WINDOW_WIDTH - this.font.getStringWidth(this.title.getUnformattedComponentText())) / 2f, 6f, 4210752);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.insideView.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        return this.insideView.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    private void createTabs() {
        final int TABS_PER_SIDE = 6;
        final int TAB_SPACING = 4;

        List<School> rightSide = this.availableSchools.stream().limit(TABS_PER_SIDE).collect(Collectors.toList());
        List<School> leftSide = this.availableSchools.stream().skip(TABS_PER_SIDE).collect(Collectors.toList());

        AtomicInteger i = new AtomicInteger();
        rightSide.forEach(school -> this.addWidget(new JournalTabWidget(JournalTabWidget.Side.RIGHT, school, WINDOW_WIDTH - 4, 5 + (JournalTabWidget.TAB_HEIGHT + TAB_SPACING) * i.getAndIncrement(), this)));
        AtomicInteger j = new AtomicInteger();
        leftSide.forEach(school -> this.addWidget(new JournalTabWidget(JournalTabWidget.Side.LEFT, school, 5 - JournalTabWidget.TAB_WIDTH, 5 + (JournalTabWidget.TAB_HEIGHT + TAB_SPACING) * j.getAndIncrement(), this)));
    }
}
