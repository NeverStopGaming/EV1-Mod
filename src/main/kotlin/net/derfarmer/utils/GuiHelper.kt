package net.derfarmer.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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

    fun drawLine(context : GuiGraphics, line : Line) {
        val matrix = context.pose()
        matrix.pushMatrix()

        val dx = line.toX - line.fromX
        val dy = line.toY - line.fromY

        val deg = atan2(dx.toDouble(), dy.toDouble()) * 180 / PI

        matrix.translate(line.fromX.toFloat(), line.fromY.toFloat())
        matrix.rotate(deg.toFloat())

        context.fill(0, 0,  line.toX, line.width, line.color);

        matrix.popMatrix()
    }

    data class Line(val fromX : Int, val fromY : Int, val toX : Int, val toY : Int, val width : Int, val color : Int) {}
}