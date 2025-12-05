package net.derfarmer

import com.google.gson.Gson
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory


object EV1Mod : ModInitializer {
    val logger = LoggerFactory.getLogger("ev1-mod")

    private val runCommand: KeyMapping =
        KeyBindingHelper.registerKeyBinding(KeyMapping("key.quest.open", GLFW.GLFW_KEY_Y, KeyMapping.Category.GAMEPLAY))

    override fun onInitialize() {
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            MessageManager.isFirstMessage = false
        }

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { _: Minecraft ->
            if (runCommand.isDown) {
                QuestManager.openBook()
            }
        })
    }

    val gson = Gson()
}