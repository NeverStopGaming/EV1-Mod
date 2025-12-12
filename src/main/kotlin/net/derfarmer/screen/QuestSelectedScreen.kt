package net.derfarmer.screen

import net.derfarmer.MessageManager
import net.derfarmer.QuestManager
import net.derfarmer.quest.Quest
import net.derfarmer.quest.QuestConditionType
import net.derfarmer.utils.GuiHelper
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrDefault
import kotlin.math.roundToInt

class QuestSelectedScreen(val questId: Int, override val parent: Screen?) : BaseQuestScreen(parent) {

    val white = ARGB.opaque(0xFFfafa)

    var quest: Quest? = null

    init {
        QuestManager.requestQuest(questId)
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

        if (quest == null) {
            context.drawString(
                minecraft!!.font,
                "Lade Daten.",
                bgStartX + padding,
                bgStartY + padding,
                textColor,
                false
            )
            return
        }

        render(context, mouseX, mouseY, quest!!)
    }

    val padding = 20
    var halfWidth = lastContextWidth shr 1
    var bgStartX = halfWidth - bgWidth
    var bgStartY = lastContextHeight - bgHeight shr 1

    fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, quest: Quest) {
        context.drawString(minecraft!!.font, quest.title, bgStartX + padding, bgStartY + padding, textColor, false)

        GuiHelper.drawWrapStringScaled(
            context, quest.description,
            bgStartX + padding, bgStartY + padding + 20, (bgWidth shr 1) - padding * 2, textColor, 0.75f, false
        )

        GuiHelper.drawWrapStringScaled(
            context, quest.description2,
            halfWidth + padding, bgStartY + padding + 20, (bgWidth shr 1) - padding * 2, textColor, 0.75f, false
        )

        if (quest.rewards.isNotEmpty()) {
            context.drawString(minecraft!!.font, "Belohnung", bgStartX + padding, bgStartY + 170, textColor, false)
        }

        drawRewards(context, bgStartX + padding, bgStartY + 180, mouseX, mouseY, quest)


        context.drawString(minecraft!!.font, "Aufgaben", halfWidth + padding, bgStartY + 130, textColor, false)
        drawConditions(context, mouseX, mouseY, quest)
    }

    val itemBgU = 406f
    val itemBgWidth = 16
    val itemBgHeight = 16

    fun drawRewards(context: GuiGraphics, bgStartX: Int, bgStartY: Int, mouseX: Int, mouseY: Int, quest: Quest) {
        for ((i, reward) in quest.rewards.withIndex()) {
            drawItem(context, padding * i + bgStartX, bgStartY, mouseX, mouseY, reward.iconID, reward.tooltip)
        }
    }

    fun drawItem(
        context: GuiGraphics,
        startX: Int,
        startY: Int,
        mouseX: Int,
        mouseY: Int,
        iconID: String,
        tooltip: String
    ) {
        val isHover = isPointInRect(mouseX, mouseY, startX, startY, startX + itemBgWidth, startY + itemBgHeight)

        GuiHelper.drawTextureScaled(
            context, texture, startX.toFloat(), startY.toFloat(),
            itemBgU, if (isHover) itemBgHeight.toFloat() else 0f, itemBgWidth, itemBgHeight, 512, 256, 1.2f
        )

        val item = ItemStack(
            BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(iconID)).getOrDefault(null) ?: return
        )

        if (isHover) {
            context.setTooltipForNextFrame(Component.literal(tooltip), mouseX, mouseY)
        }

        GuiHelper.drawItemScaled(context, item, startX + 1.6f, startY + 1.6f, 1f)
    }

    val buttonWidth = 64
    val buttonHeight = 16
    val buttonU = 423f

    var isButtonHover = false

    fun drawConditions(context: GuiGraphics, mouseX: Int, mouseY: Int, quest: Quest) {

        for ((i, condition) in quest.conditions.withIndex()) {

            var startX = padding * i + halfWidth + padding
            var startY = bgStartY + 140

            // TODO: PLEASE FIX THIS SHIT
            if (i > 5) {
                startX = padding * (i - 6) + halfWidth + padding
                startY = bgStartY + 160
            }

            if (i > 11) {
                startX = padding * (i - 12) + halfWidth + padding
                startY = bgStartY + 180
            }

            val itemID = when(condition.type) {
                QuestConditionType.KILL_MOB -> "${condition.id}_spawn_egg"
                else -> condition.id
            }

            drawItem(
                context,
                startX,
                startY,
                mouseX,
                mouseY,
                itemID,
                condition.tooltip + " (${condition.currentAmount}/${condition.amount})"
            )

            val progress = (100.0 / condition.amount) * condition.currentAmount

            GuiHelper.drawStringScaled(
                context, "${progress.roundToInt()}%",
                startX + 1.6f, startY + (itemBgHeight * 1.2f) - (minecraft!!.font.lineHeight * 0.8f),
                if (progress == 100.0) MenuQuestScreen.textColorComplete else white, 0.7f, true
            )
        }


        if (quest.conditions.any { it.type == QuestConditionType.SUBMIT_ITEM }) {

            val startX = halfWidth + padding
            val startY = bgStartY + 165

            isButtonHover = isPointInRect(mouseX, mouseY, startX, startY, startX + buttonWidth, startY + buttonHeight)

            context.blit(
                RenderPipelines.GUI_TEXTURED,
                texture, startX, startY, buttonU, if (isButtonHover) 16f else 0f,
                buttonWidth, buttonHeight, 512, 256
            )

            GuiHelper.drawStringScaled(context, "Abgeben", startX + 10f, startY + 4f, textColor, 1f)
        }
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {

        if (isButtonHover) {
            MessageManager.sendMessage("s$questId")
        }

        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun updateContentDimensions() {
        bgStartX = lastContextWidth - bgWidth shr 1
        bgStartY = lastContextHeight - bgHeight shr 1
        halfWidth = lastContextWidth shr 1
    }
}