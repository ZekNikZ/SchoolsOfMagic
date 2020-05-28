package dev.mattrm.schoolsofmagic;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mattrm.schoolsofmagic.client.misc.ModItemGroups;
import dev.mattrm.schoolsofmagic.common.block.ModBlocks;
import dev.mattrm.schoolsofmagic.common.item.ModItems;
import dev.mattrm.schoolsofmagic.common.item.WandItemBase;
import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = GlobalConstants.MODID)
public class MainEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    public static ConcurrentLinkedDeque<Pair<Double, Double>> _wandPoints = new ConcurrentLinkedDeque<>();
    public static final double SENSITIVITY = 1.0f;
    public static final int NUM_POINTS = 300;
    public static AtomicInteger currentNumPoints;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderEvent(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            if (WandItemBase.isUsingWand()) {
                Minecraft mc = Minecraft.getInstance();

                _wandPoints.add(new Pair<>(Minecraft.getInstance().mouseHelper.getMouseX(), Minecraft.getInstance().mouseHelper.getMouseY()));
                currentNumPoints.incrementAndGet();

                if (currentNumPoints.get() > NUM_POINTS) {
                    _wandPoints.remove();
                    currentNumPoints.decrementAndGet();
                }

                double minX = _wandPoints.stream().mapToDouble(doubleDoublePair -> doubleDoublePair.getKey()).min().orElse(0d);
                double minY = _wandPoints.stream().mapToDouble(doubleDoublePair -> doubleDoublePair.getValue()).min().orElse(0d);
                double maxX = _wandPoints.stream().mapToDouble(doubleDoublePair -> doubleDoublePair.getKey()).max().orElse(0d);
                double maxY = _wandPoints.stream().mapToDouble(doubleDoublePair -> doubleDoublePair.getValue()).max().orElse(0d);
                double avgX = (maxX + minX) / 2;
                double avgY = (maxY + minY) / 2;

                int scaledWidth = mc.getMainWindow().getScaledWidth();
                int scaledHeight = mc.getMainWindow().getScaledHeight();

//            List<Pair<Double, Double>> wandPoints = new LinkedList<>(_wandPoints);


                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                RenderSystem.pushMatrix();
                RenderSystem.disableBlend();
                RenderSystem.disableTexture();
                RenderSystem.depthMask(false);
                RenderSystem.translated(scaledWidth / 2 - avgX, scaledHeight / 2 - avgY, 0.0d);
                RenderSystem.scaled(SENSITIVITY, SENSITIVITY, SENSITIVITY);

                GL11.glLineWidth(6f);

                float size = currentNumPoints.floatValue();
                int i = 0;
                bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                for (Pair<Double, Double> point : _wandPoints) {
//                    bufferBuilder.pos(point.getKey(), point.getValue(), 0.0D).color(1f, 1f, 1f, 0.5f).endVertex();
                    bufferBuilder.pos(point.getKey(), point.getValue(), 0.0D).color((float) MathHelper.clampedLerp(0f, 1f, (float) (i++ + 50) / size), 1f, 1f, 1f).endVertex();
                }
                RenderSystem.enableAlphaTest();
                tessellator.draw();

                RenderSystem.depthMask(true);
                RenderSystem.enableTexture();
                RenderSystem.popMatrix();
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.player != null) {
            if (WandItemBase.isUsingWand()) {
                event.player.sendMessage(new StringTextComponent("x: " + Minecraft.getInstance().mouseHelper.getMouseX() + ", y: " + Minecraft.getInstance().mouseHelper.getMouseY()));
            }

            if (WandItemBase.isUsingWand() && event.player.getHeldItem(Hand.MAIN_HAND).getItem() != ModItems.WAND_BASE.get()) {
                WandItemBase.stopUsingWand();
                event.player.sendMessage(new StringTextComponent("STOPPED USING"));
            }
        }
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
        ModBlocks.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(block -> {
                            final Item.Properties properties = new Item.Properties().group(ModItemGroups.SCHOOLS_OF_MAGIC_ITEM_GROUP);
                            final BlockItem blockItem = new BlockItem(block, properties);
                            blockItem.setRegistryName(block.getRegistryName());
                            ModItems.BLOCK_ITEMS.put(block.getRegistryName().getPath(), blockItem);
                            itemRegistryEvent.getRegistry().register(blockItem);
                        }
                );
    }
}
