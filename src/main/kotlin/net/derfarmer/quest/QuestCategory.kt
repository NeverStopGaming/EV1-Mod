package net.derfarmer.quest

open class QuestCategory(open val id : Int, open val title : String, open val completed : Int)

class BakedQuestCategory(override val id : Int,
                         override val title: String,
                         override val completed: Int,
                         val xPos : Int,
                         val yPos : Int,
                         val x2Pos : Int,
                         val y2Pos : Int
) : QuestCategory(id,title, completed)