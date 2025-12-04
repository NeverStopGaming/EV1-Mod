package net.derfarmer

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.network.chat.Component


object MessageManager {

    const val COMMAND = "fabricdata "
    var isFirstMessage = false

    fun receive(text: Component): Boolean {

        val message = text.string

        if (message.startsWith("/")) return false

        if (!isFirstMessage) {
            sendMessage("0")
            isFirstMessage = true
            return true
        }

        EV1Mod.logger.info(text.string)

        if (message.startsWith(COMMAND)) {
            parseMessage(message.substring(COMMAND.length))
            return true
        }

        return false
    }

    fun sendMessage(data: String) {

        val client = Minecraft.getInstance()
        val handler = client.connection ?: return

        handler.sendCommand("$COMMAND$data")
    }

    fun parseMessage(msg: String) {

        val msgType = msg[0]
        val msgData = msg.substring(1)
        val data = msgData.split(";")

        EV1Mod.logger.info("Got message from Server($msgType): $msgData")

        when (msgType) {
            't' -> {
                Minecraft.getInstance().toastManager.addToast(
                    SystemToast.multiline(
                        Minecraft.getInstance(),
                        SystemToast.SystemToastId.NARRATOR_TOGGLE,
                        Component.literal(data[0]),
                        Component.literal(data[1])
                    )
                )
            }
            'q' -> QuestManager.receiveQuest(msgData)
            'l' -> QuestManager.receiveQuestTree(msgData)
            'c' -> QuestManager.receiveCategorys(msgData)
        }

    }
}