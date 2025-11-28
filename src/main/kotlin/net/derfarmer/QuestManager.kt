package net.derfarmer

import net.derfarmer.quest.QuestCategory
import net.derfarmer.quest.QuestNode
import kotlin.collections.mutableListOf

object QuestManager {

    fun getQuestCategorys() : List<QuestCategory> {
         return mutableListOf<QuestCategory>(
            QuestCategory(1,"1. Basic Survival", 87),
            QuestCategory(2,"2. Culinary Delights", 18),
            QuestCategory(3,"3. Warum tut ich das hier", 69),
            QuestCategory(4,"4. Kill me", 420),
            QuestCategory(5,"5. Last mich raus ...", -1),
            QuestCategory(6,"6. ich bin in einen", 43),
            QuestCategory(7,"7. Questbuch gefangen", 64),
        )
    }

    fun getQuestTree(questTreeId : Int ) : List<QuestNode> {
        return mutableListOf(
            QuestNode(10, "stone", 30, 30, true, listOf(Pair(100,100))),
            QuestNode(12, "diamond_sword", 100, 100, true, listOf())
        )
    }
}