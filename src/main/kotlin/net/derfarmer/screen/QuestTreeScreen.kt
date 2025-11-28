package net.derfarmer.screen

import net.derfarmer.QuestManager
import net.derfarmer.quest.BakedQuestNode
import net.derfarmer.quest.QuestNode
import net.derfarmer.utils.GuiHelper
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class QuestTreeScreen(val questTreeID : Int) : BaseQuestScreen() {

    var backedNodes = listOf<BakedQuestNode>()

    init {
        bakeNodes(QuestManager.getQuestTree(questTreeID))
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int,delta: Float) {

        //TODO: Remove in PROD
        bakeNodes(QuestManager.getQuestTree(questTreeID))

        for (node in backedNodes) {
            drawNode(context, node, mouseX, mouseY)
        }
    }

    val nodeU = 367f
    val nodeV = 23f
    val nodeWidth = 30
    val nodeHeight = 34
    val hoverOffset = nodeHeight + 3

    fun drawNode(context : GuiGraphics, questNode: BakedQuestNode, mouseX: Int, mouseY: Int) {

        val isHover = isPointInRect(mouseX, mouseY, questNode.posX1, questNode.posY1, questNode.posX2, questNode.posY2)

        for (line in questNode.lines) {
            GuiHelper.drawLine(context, line)
        }

        context.blit(RenderPipelines.GUI_TEXTURED,texture, questNode.posX1, questNode.posY1,
            nodeU, if (isHover) nodeV + hoverOffset else nodeV , nodeWidth, nodeHeight, 512,256)

        val matrix = context.pose()
        matrix.pushMatrix()

        matrix.translate(questNode.posX1 + 6.6f, questNode.posY1 + 8.7f)
        matrix.scale(1.1f, 1.1f)

        context.renderItem(questNode.item,0,0)

        matrix.popMatrix()
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
                it.questID, it.itemID, it.x, it.y, it.completed, it.connectionsTo,
                ItemStack(item), nodeX, nodeY, nodeX + nodeWidth, nodeY + nodeHeight,
                it.connectionsTo.map { connection -> GuiHelper.Line(nodeMiddleX, nodeMiddleY,
                    (connection.first + bgStartX) - nodeWidth shr 1, (connection.second + bgStartY) - nodeHeight shr 1, 4, textColor) }
            ))
        }

        backedNodes = list
    }

    override fun updateContentDimensions() {
        bakeNodes(backedNodes)
    }
}