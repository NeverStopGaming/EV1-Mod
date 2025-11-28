package net.derfarmer.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.FormattedText
import net.minecraft.world.item.ItemStack
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.atanh
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.tan

object GuiHelper {

    fun drawStringScaled(
        graphics: GuiGraphics,
        text: String,
        x: Int,
        y: Int,
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
        graphics.drawWordWrap(Minecraft.getInstance().font, FormattedText.of(text), 0,0,
            (width / scale).toInt(), color, shadow)
        poseStack.popMatrix()
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

    data class Line(val fromX : Int, val fromY : Int, val toX : Int, val toY : Int, val width : Int, val color : Int) {}

    fun drawItemScaled(context: GuiGraphics, item : ItemStack, x : Float, y : Float, scale : Float) {
        val poseStack = context.pose()
        poseStack.pushMatrix()
        poseStack.translate(x, y)
        poseStack.scale(scale, scale)
        context.renderItem(item,0,0)
        poseStack.popMatrix()
    }
}