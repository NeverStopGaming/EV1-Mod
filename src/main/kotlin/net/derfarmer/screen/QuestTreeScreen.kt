package net.derfarmer.screen

import net.derfarmer.QuestManager
import net.derfarmer.quest.BakedQuestNode
import net.derfarmer.quest.QuestNode
import net.derfarmer.utils.GuiHelper
import net.minecraft.Util
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import kotlin.math.abs
import kotlin.math.sin

class QuestTreeScreen(val questTreeID : Int, override val parent : Screen) : BaseQuestScreen(parent) {

    var backedNodes = listOf<BakedQuestNode>()

    init {
        bakeNodes(QuestManager.getQuestTree(questTreeID))
    }

    val tintColor = ARGB.opaque(0xD5CCFF)
    var hoverID = 0

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

        val t = Util.getMillis() / 500.0
        val ping = abs(sin(t * 0.8 ))
        val color = ARGB.lerp(ping.toFloat(), -1, tintColor)

        hoverID = 0

        for (node in backedNodes) {
            drawNode(context, node, mouseX, mouseY, color)
        }
    }

    val nodeU = 367f
    val nodeV = 23f
    val nodeWidth = 30
    val nodeHeight = 34
    val hoverOffset = nodeHeight + 3

    fun drawNode(context : GuiGraphics, questNode: BakedQuestNode, mouseX: Int, mouseY: Int, tintColor : Int) {

        val isHover = isPointInRect(mouseX, mouseY, questNode.posX1, questNode.posY1, questNode.posX2, questNode.posY2)

        for (line in questNode.lines) {
            GuiHelper.drawLine(context, line)
        }

        if(isHover) {
            hoverID = questNode.questID

            // TODO: Render Tooltip
        }

        context.blit(RenderPipelines.GUI_TEXTURED,texture, questNode.posX1, questNode.posY1,
            nodeU, if (isHover) nodeV + hoverOffset else nodeV , nodeWidth, nodeHeight, 512,256,
            if (questNode.completed) -1 else tintColor)

        GuiHelper.drawItemScaled(context, questNode.item,
            questNode.posX1 + 6.6f, questNode.posY1 + 8.7f, 1.1f)
    }

    fun bakeNodes(nodes : List<QuestNode>) {
        val list = mutableListOf<BakedQuestNode>()

        nodes.forEach {
            val item = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(it.itemID)).get()

            val bgStartX = lastContextWidth - bgWidth  shr 1
            val bgStartY = lastContextHeight - bgHeight  shr 1

            val nodeMiddleX = bgStartX + it.x
            val nodeMiddleY = bgStartY + it.y

            val nodeX = nodeMiddleX - (nodeWidth shr 1)
            val nodeY = nodeMiddleY - (nodeHeight shr 1)

            list.add(BakedQuestNode(
                it.questID, it.itemID,it.title, it.x, it.y, it.completed, it.connectionsTo,
                ItemStack(item), nodeX, nodeY, nodeX + nodeWidth, nodeY + nodeHeight,
                it.connectionsTo.map { connection -> GuiHelper.Line(nodeMiddleX, nodeMiddleY,
                    bgStartX + connection.first, bgStartY + connection.second, 2, textColor) }
            ))
        }

        backedNodes = list
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (hoverID == 0) return super.mouseClicked(mouseButtonEvent, bl)

        turnPage(QuestSelectedScreen(hoverID, this))

        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun updateContentDimensions() {
        bakeNodes(backedNodes)
    }
}