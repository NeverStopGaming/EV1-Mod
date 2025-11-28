package net.derfarmer.screen

import net.derfarmer.QuestManager
import net.derfarmer.quest.QuestReward
import net.derfarmer.utils.GuiHelper
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class QuestSelectedScreen(val questId : Int, override val parent : Screen?) : BaseQuestScreen(parent) {

    val quest = QuestManager.getQuest(questId)

    val padding = 20
    var halfWidth = lastContextWidth shr 1
    var bgStartX = halfWidth - bgWidth
    var bgStartY = lastContextHeight - bgHeight  shr 1

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

        context.drawString(minecraft!!.font, quest.title, bgStartX + padding, bgStartY + padding, textColor,false)

        GuiHelper.drawWrapStringScaled(context, quest.description,
            bgStartX + padding, bgStartY + padding + 20, (bgWidth shr 1) - padding * 2, textColor, 0.75f, false)

        GuiHelper.drawWrapStringScaled(context, quest.description2,
             halfWidth + padding,  bgStartY + padding + 20, (bgWidth shr 1) - padding * 2, textColor, 0.75f, false)

        context.drawString(minecraft!!.font, "Belohnung", bgStartX + padding, bgStartY + 170, textColor,false)

        drawRewards(context,bgStartX + padding, bgStartY + 180, mouseX, mouseY)

        drawConditions(context, mouseX, mouseY)
    }

    val itemBgU = 406f
    val itemBgWidth = 16
    val itemBgHeight = 16

    fun drawRewards(context : GuiGraphics, bgStartX : Int, bgStartY : Int, mouseX: Int, mouseY: Int) {
        for ((i, reward) in quest.rewards.withIndex()) {
            drawItem(context, padding * i + bgStartX, bgStartY, mouseX, mouseY, reward)
        }
    }

    fun drawItem(context : GuiGraphics, startX : Int, startY : Int, mouseX: Int, mouseY: Int, reward: QuestReward) {
        val isHover = isPointInRect(mouseX, mouseY, startX, startY, startX + itemBgWidth, startY + itemBgHeight)

        context.blit(RenderPipelines.GUI_TEXTURED,texture, startX, startY,
            itemBgU, if(isHover) itemBgHeight.toFloat() else 0f, itemBgWidth, itemBgHeight, 512, 256)

        val item = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(reward.iconID)).get()

        GuiHelper.drawItemScaled(context,ItemStack(item), startX + 1.6f, startY + 1.6f, 0.8f)
    }

    fun drawConditions(context: GuiGraphics, mouseX: Int, mouseY: Int)  {


    }

    override fun updateContentDimensions() {
        bgStartX = lastContextWidth - bgWidth  shr 1
        bgStartY = lastContextHeight - bgHeight  shr 1
        halfWidth = lastContextWidth shr 1
    }
}