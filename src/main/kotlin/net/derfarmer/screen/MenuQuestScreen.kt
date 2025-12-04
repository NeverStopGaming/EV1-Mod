package net.derfarmer.screen

import net.derfarmer.QuestManager
import net.derfarmer.quest.BakedQuestCategory
import net.derfarmer.quest.QuestCategory
import net.derfarmer.utils.GuiHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ARGB

object MenuQuestScreen : BaseQuestScreen(null) {

    val logo: ResourceLocation = ResourceLocation.fromNamespaceAndPath("ev1", "icon.png")

    private var backedCategorys = listOf<BakedQuestCategory>()

    const val LOGO_WIDTH = 512
    const val LOGO_HEIGHT = 512

    val textColorComplete = ARGB.opaque(0x50D13D)
    val textColorLocked = ARGB.opaque(0x949494)
    val textColorHover = ARGB.opaque(0xC4C4C4)

    var hoverID = 0

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

        val x = (context.guiWidth() - bgWidth shr 1) + 25
        val y = (context.guiHeight() - bgHeight shr 1) + 25

        val matrix = context.pose()
        matrix.pushMatrix()

        matrix.translation(x.toFloat(), y.toFloat())
        matrix.scale(0.25f)

        context.blit(RenderPipelines.GUI_TEXTURED, logo, 0, 0, 0f, 0f, LOGO_WIDTH, LOGO_HEIGHT, LOGO_WIDTH, LOGO_HEIGHT)

        matrix.popMatrix()

        hoverID = 0

        for (category in backedCategorys) {

            val isHover = isPointInRect(mouseX, mouseY, category.xPos, category.yPos, category.x2Pos, category.y2Pos)
            var text = if (isHover) textColorHover else textColor
            var description = "${category.completed}% abgeschlossen"

            if (isHover) hoverID = category.id

            if (category.completed >= 100) {
                text = textColorComplete
            } else if (category.completed == -1) {
                text = textColorLocked
                description = "Nicht Freigeschaltet"
            }

            context.drawString(minecraft!!.font, category.title, category.xPos, category.yPos, text, isHover);


            GuiHelper.drawStringScaled(
                context, description,
                category.xPos + 25f, category.yPos + 10f, text, 0.6f, isHover
            );
        }

        context.drawString(minecraft!!.font, "Mehr Quests mehr Spaß", x, y + 150, textColor, false);
    }

    fun bakeCategory(categorys: List<QuestCategory>) {
        val list = mutableListOf<BakedQuestCategory>()
        val betweenOffset = 25

        val offset = (lastContextHeight shr 1) - (categorys.size * betweenOffset shr 1)

        categorys.forEach {

            val x = (lastContextWidth shr 1) + 15
            val y = list.size * betweenOffset + offset

            list.add(
                BakedQuestCategory(
                    it.id, it.title, it.completed, x, y,
                    x + Minecraft.getInstance().font.width(it.title),
                    Minecraft.getInstance().font.lineHeight + y
                )
            )
        }

        backedCategorys = list
    }

    override fun updateContentDimensions() {
        bakeCategory(backedCategorys)
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (hoverID == 0) return false
        if (backedCategorys.first { it.id == hoverID }.completed == -1 ) return false

        turnPage(QuestTreeScreen(hoverID, this))

        return super.mouseClicked(mouseButtonEvent, bl)
    }
}