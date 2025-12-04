package net.derfarmer.screen

import net.derfarmer.QuestManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.ARGB


open class BaseQuestScreen(open val parent: Screen?) : Screen(Component.literal("Quest Screen")) {

    val texture: ResourceLocation = ResourceLocation.fromNamespaceAndPath("ev1", "textures/gui/book.png")

    val bgWidth = 340
    val bgHeight = 230

    val textColor = ARGB.opaque(0x262626)

    var lastContextWidth = 0
    var lastContextHeight = 0

    override fun renderBackground(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {

        if (lastContextWidth != context.guiWidth() || lastContextHeight != context.guiHeight()) {
            lastContextWidth = context.guiWidth()
            lastContextHeight = context.guiHeight()
            updateContentDimensions()
        }

        val bgStartX = context.guiWidth() - bgWidth shr 1
        val bgStartY = context.guiHeight() - bgHeight shr 1

        context.blit(RenderPipelines.GUI_TEXTURED, texture, bgStartX, bgStartY, 0f, 0f, bgWidth, bgHeight, 512, 256)

        drawArrows(context, mouseX, mouseY)
    }

    val arrowBackU = 356f
    val arrowBackWidth = 14
    val arrowBackHeight = 10

    val arrowMenuU = 341f
    val arrowMenuWidth = 14
    val arrowMenuHeight = 9

    val arrowHoverOffset = 11f

    var isArrowBackHover = false
    var isArrowMenuHover = false

    fun drawArrows(context: GuiGraphics, mouseX: Int, mouseY: Int) {

        val xAMenu = (context.guiWidth() - arrowMenuWidth) shr 1
        val yAMenu = context.guiHeight() / 2 + bgHeight / 2 - 10

        val xABack = (context.guiWidth() / 2 - bgWidth / 2) + 10
        val yABack = context.guiHeight() / 2 + bgHeight / 2 - 10

        isArrowBackHover = isArrowBackHovered(xABack, yABack, mouseX, mouseY)
        isArrowMenuHover = isArrowMenuHovered(xAMenu, yAMenu, mouseX, mouseY)

        val offsetABack = if (isArrowBackHover) arrowHoverOffset else 0f
        val offsetAMenu = if (isArrowMenuHover) arrowHoverOffset else 0f

        context.blit(
            RenderPipelines.GUI_TEXTURED, texture, xAMenu, yAMenu,
            arrowMenuU, offsetAMenu, arrowMenuWidth, arrowMenuHeight, 512, 256
        )

        context.blit(
            RenderPipelines.GUI_TEXTURED, texture, xABack, yABack,
            arrowBackU, offsetABack, arrowBackWidth, arrowBackHeight, 512, 256
        )

    }

    override fun mouseClicked(mouseButtonEvent: MouseButtonEvent, bl: Boolean): Boolean {
        if (!mouseButtonEvent.isDown && mouseButtonEvent.button() != 0) return bl

        if (isArrowBackHover) {
            turnPage(parent)
        } else if (isArrowMenuHover) {
            turnPage(MenuQuestScreen)
        }

        return bl
    }

    fun isArrowMenuHovered(aX: Int, aY: Int, mouseX: Int, mouseY: Int): Boolean {
        return isPointInRect(mouseX, mouseY, aX, aY, aX + arrowMenuWidth, aY + arrowMenuHeight)
    }

    fun isArrowBackHovered(aX: Int, aY: Int, mouseX: Int, mouseY: Int): Boolean {
        return isPointInRect(mouseX, mouseY, aX, aY, aX + arrowBackWidth, aY + arrowBackHeight)
    }

    fun isPointInRect(px: Int, py: Int, left: Int, top: Int, right: Int, bottom: Int): Boolean {
        return px in left..right && py in top..bottom
    }

    fun turnPage(screen: Screen?) {
        Minecraft.getInstance().player?.playSound(SoundEvents.BOOK_PAGE_TURN)
        Minecraft.getInstance().setScreen(screen)


        if (screen is BaseQuestScreen) {
            QuestManager.currentQuestScreen = screen;
        }
    }

    override fun isPauseScreen() = false

    open fun updateContentDimensions() {}

    override fun onClose() {
        super.onClose()
    }

}