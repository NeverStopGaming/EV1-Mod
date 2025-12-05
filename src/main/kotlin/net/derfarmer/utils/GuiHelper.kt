package net.derfarmer.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.FormattedText
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import kotlin.math.atan2


object GuiHelper {

    fun drawStringScaled(
        graphics: GuiGraphics,
        text: String,
        x: Float,
        y: Float,
        color: Int,
        scale: Float,
        shadow: Boolean = false
    ) {
        if (scale == 1f) {
            graphics.drawString(Minecraft.getInstance().font, text, x.toInt(), y.toInt(), color, shadow)
            return
        }

        val poseStack = graphics.pose()
        poseStack.pushMatrix()
        poseStack.translate(x, y)
        poseStack.scale(scale, scale)
        graphics.drawString(Minecraft.getInstance().font, text, 0, 0, color, shadow)
        poseStack.popMatrix()
    }

    fun drawWrapStringScaled(
        graphics: GuiGraphics,
        text: String,
        x: Int,
        y: Int,
        width: Int,
        color: Int,
        scale: Float,
        shadow: Boolean = false
    ) {
        if (scale == 1f) {
            graphics.drawString(Minecraft.getInstance().font, text, x.toInt(), y.toInt(), color, shadow)
            return
        }

        val poseStack = graphics.pose()
        poseStack.pushMatrix()
        poseStack.translate(x.toFloat(), y.toFloat())
        poseStack.scale(scale, scale)
        graphics.drawWordWrap(
            Minecraft.getInstance().font, FormattedText.of(text), 0, 0,
            (width / scale).toInt(), color, shadow
        )
        poseStack.popMatrix()
    }

    fun drawTextureScaled(
        context: GuiGraphics,
        resourceLocation: ResourceLocation,
        x: Float,
        y: Float,
        u: Float,
        v: Float,
        width: Int,
        height: Int,
        textureWidth: Int,
        textureHeight: Int,
        scale: Float
    ) {
        val matrix = context.pose()
        matrix.pushMatrix()

        matrix.translate(x, y)
        matrix.scale(scale, scale)

        context.blit(
            RenderPipelines.GUI_TEXTURED, resourceLocation, 0, 0,
            u, v, width, height, textureWidth, textureHeight
        )

        matrix.popMatrix()
    }

    fun drawLine(context: GuiGraphics, line: Line) {
        val matrix = context.pose()
        matrix.pushMatrix()

        val dx = (line.toX - line.fromX).toFloat()
        val dy = (line.toY - line.fromY).toFloat()

        val angle = atan2(dy, dx) //
        val length = kotlin.math.hypot(dx, dy)

        matrix.translate(line.fromX.toFloat(), line.fromY.toFloat())
        matrix.rotate(angle)

        val halfH = line.width / 2
        context.fill(0, -halfH, length.toInt(), line.width, line.color)

        matrix.popMatrix()
    }

    data class Line(val fromX: Int, val fromY: Int, val toX: Int, val toY: Int, val width: Int, val color: Int) {}

    fun drawItemScaled(context: GuiGraphics, item: ItemStack, x: Float, y: Float, scale: Float) {
        val poseStack = context.pose()
        poseStack.pushMatrix()
        poseStack.translate(x, y)
        poseStack.scale(scale, scale)
        context.renderItem(item, 0, 0)
        poseStack.popMatrix()
    }
}