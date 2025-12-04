package net.derfarmer

import net.derfarmer.quest.Quest
import net.derfarmer.quest.QuestCategory
import net.derfarmer.quest.QuestNode
import net.derfarmer.screen.BaseQuestScreen
import net.derfarmer.screen.MenuQuestScreen
import net.derfarmer.screen.QuestSelectedScreen
import net.derfarmer.screen.QuestTreeScreen
import net.minecraft.client.Minecraft

object QuestManager {

    var currentQuestScreen: BaseQuestScreen? = null

    fun requestCategorys() = MessageManager.sendMessage("c")

    fun requestQuestTree(questTreeId: Int) = MessageManager.sendMessage("l$questTreeId")

    fun requestQuest(questId: Int) = MessageManager.sendMessage("q$questId")

    fun receiveCategorys(data: String) {
        if (currentQuestScreen !is MenuQuestScreen) return
        val screen = currentQuestScreen as MenuQuestScreen

        screen.bakeCategory(EV1Mod.gson.fromJson<Array<QuestCategory>>(data, arrayOf<QuestCategory>()::class.java).toList())
    }

    fun receiveQuestTree(data: String) {
        if (currentQuestScreen !is QuestTreeScreen) return
        val screen = currentQuestScreen as QuestTreeScreen

        screen.bakeNodes(EV1Mod.gson.fromJson<Array<QuestNode>>(data, arrayOf<QuestNode>()::class.java).toList())
    }

    fun receiveQuest(data: String) {
        if (currentQuestScreen !is QuestSelectedScreen) return
        val screen = currentQuestScreen as QuestSelectedScreen

        screen.quest = EV1Mod.gson.fromJson<Quest>(data, Quest::class.java)
    }

    fun openBook() {
        if (Minecraft.getInstance().isSingleplayer) return
        currentQuestScreen = MenuQuestScreen
        requestCategorys()
        Minecraft.getInstance().setScreen(MenuQuestScreen)
    }
}