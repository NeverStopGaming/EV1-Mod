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
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrDefault
import kotlin.math.abs
import kotlin.math.sin

class QuestTreeScreen(val questTreeID: Int, override val parent: Screen) : BaseQuestScreen(parent) {

    var backedNodes = listOf<BakedQuestNode>()

    init {
        QuestManager.requestQuestTree(questTreeID)
    }

    val tintColor = ARGB.opaque(0xD5CCFF)
    var hoverID = 0

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

        val t = Util.getMillis() / 500.0
        val ping = abs(sin(t * 0.8))
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

    fun drawNode(context: GuiGraphics, questNode: BakedQuestNode, mouseX: Int, mouseY: Int, tintColor: Int) {

        val isHover = isPointInRect(mouseX, mouseY, questNode.posX1, questNode.posY1, questNode.posX2, questNode.posY2)

        for (line in questNode.lines) {
            GuiHelper.drawLine(context, line)
        }

        if (isHover) {
            hoverID = questNode.questID

            context.setTooltipForNextFrame(Component.literal(questNode.title), mouseX, mouseY)
        }

        val tint =
            if (questNode.completed) -1 else if (questNode.isLocked) MenuQuestScreen.textColorLocked else tintColor

        context.blit(
            RenderPipelines.GUI_TEXTURED, texture, questNode.posX1, questNode.posY1,
            nodeU, if (isHover) nodeV + hoverOffset else nodeV, nodeWidth, nodeHeight, 512, 256,
            tint
        )

        GuiHelper.drawItemScaled(
            context, questNode.item,
            questNode.posX1 + 6.6f, questNode.posY1 + 8.7f, 1.1f
        )
    }

    fun bakeNodes(nodes: List<QuestNode>) {
        val list = mutableListOf<BakedQuestNode>()

        nodes.forEach { node ->
            val item = ItemStack(
                BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(node.itemID)).getOrDefault(null)
                    ?: return
            )

            val bgStartX = lastContextWidth - bgWidth shr 1
            val bgStartY = lastContextHeight - bgHeight shr 1

            val nodeMiddleX = bgStartX + node.x
            val nodeMiddleY = bgStartY + node.y

            val nodeX = nodeMiddleX - (nodeWidth shr 1)
            val nodeY = nodeMiddleY - (nodeHeight shr 1)

            val filter = nodes.filter { it.connectionsTo.contains(node.questID) }

            val locked = filter.isNotEmpty() && filter.all { !it.completed }

            list.add(
                BakedQuestNode(
                    node.questID, node.itemID, node.title, node.x, node.y, node.completed, node.connectionsTo,
                    item, nodeX, nodeY, nodeX + nodeWidth, nodeY + nodeHeight,
                    node.connectionsTo.map { connection ->
                        val other = nodes.first { it.questID == connection }
                        GuiHelper.Line(
                            nodeMiddleX, nodeMiddleY,
                            bgStartX + other.x, bgStartY + other.y, 2, textColor,

                            )
                    }, locked
                )
            )
        }

        backedNodes = list
    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (hoverID == 0) return super.mouseClicked(mouseButtonEvent, bl)

        if (!backedNodes.first { it.questID == hoverID }.isLocked) {
            turnPage(QuestSelectedScreen(hoverID, this))
        }

        return super.mouseClicked(mouseButtonEvent, bl)
    }

    override fun updateContentDimensions() {
        bakeNodes(backedNodes)
    }
}