package net.derfarmer.quest

import net.derfarmer.utils.GuiHelper
import net.minecraft.world.item.ItemStack

open class QuestNode(open val questID: Int, open val itemID: String, open val x: Int, open val y: Int,
                     open val completed: Boolean,val connectionsTo: List<Pair<Int, Int>>)

class BakedQuestNode(override val questID : Int, override val itemID : String,
                     override val x : Int, override val y : Int,
                     override val completed : Boolean, connectionsTo : List<Pair<Int, Int>>,
                     val item : ItemStack, val posX1 : Int, val posY1: Int, val posX2 : Int,val posY2 : Int,
                    val lines : List<GuiHelper.Line>)
    : QuestNode(questID, itemID, x, y, completed, connectionsTo)
