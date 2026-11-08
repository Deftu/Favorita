package dev.deftu.favorita.render

import dev.deftu.favorita.Favorita
import dev.deftu.favorita.config.FavoritaConfig
import javax.imageio.ImageIO

//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor
//?} elif >=1.21 {
/*import net.minecraft.client.gui.GuiGraphics
*///?} else {
/*import net.minecraft.client.gui.GuiElement
import net.minecraft.client.render.platform.GlStateManager
*///?}

//? if >=26.1 {
typealias SlotGraphics = GuiGraphicsExtractor
//?} elif >=1.21 {
/*typealias SlotGraphics = GuiGraphics
*///?} else {
/*typealias SlotGraphics = Any?
*///?}

/** Draws the tint and padlock over a locked 16px slot, using only solid fills so it renders the same on every version */
object LockOverlay {
    private const val SIZE = 16

    private class Rect(val x: Int, val y: Int, val width: Int, var height: Int, val shade: Int)

    /** The padlock as rectangles of one shade, read once from lock.png and merged down rows to keep the fill count low */
    private val padlock: List<Rect> by lazy {
        val image = runCatching { Favorita::class.java.getResourceAsStream("/assets/favorita/lock.png")!!.use(ImageIO::read) }
            .onFailure { Favorita.LOGGER.error("Failed to read the padlock icon", it) }
            .getOrNull() ?: return@lazy emptyList()
        buildList {
            for (y in 0 until minOf(SIZE, image.height)) {
                var x = 0
                while (x < minOf(SIZE, image.width)) {
                    val argb = image.getRGB(x, y)
                    if (argb ushr 24 < 128) {
                        x++
                        continue
                    }
                    val shade = snap(argb)
                    var end = x + 1
                    while (end < minOf(SIZE, image.width) && image.getRGB(end, y).let { it ushr 24 >= 128 && snap(it) == shade }) end++
                    val above = lastOrNull { it.y + it.height == y && it.x == x && it.width == end - x && it.shade == shade }
                    if (above != null) above.height++ else add(Rect(x, y, end - x, 1, shade))
                    x = end
                }
            }
        }
    }

    // lock.png's near-identical greys are merged into a few shades, so whole areas become one fill each
    private val SHADES = intArrayOf(0, 75, 113, 152, 214)

    private fun snap(argb: Int): Int = SHADES.minBy { kotlin.math.abs(it - (argb and 0xFF)) }

    @JvmStatic
    @JvmOverloads
    fun draw(graphics: SlotGraphics, x: Int, y: Int, alpha: Float = 1f) {
        if (alpha <= 0f) return
        val tint = FavoritaConfig.lockColor.argb
        val tintAlpha = ((tint ushr 24) * alpha).toInt().coerceIn(0, 255)
        begin(graphics)
        try {
            fill(graphics, x, y, x + SIZE, y + SIZE, (tintAlpha shl 24) or (tint and 0xFFFFFF))
            if (FavoritaConfig.showLockIcon) {
                val iconAlpha = (166 * alpha).toInt().coerceIn(0, 255) shl 24
                for (rect in padlock) {
                    val shade = (rect.shade shl 16) or (rect.shade shl 8) or rect.shade
                    fill(graphics, x + rect.x, y + rect.y, x + rect.x + rect.width, y + rect.y + rect.height, iconAlpha or shade)
                }
            }
        } finally {
            end(graphics)
        }
    }

    private fun fill(graphics: SlotGraphics, x1: Int, y1: Int, x2: Int, y2: Int, argb: Int) {
        //? if >=1.21 {
        graphics.fill(x1, y1, x2, y2, argb)
        //?} else {
        /*GuiElement.fill(x1, y1, x2, y2, argb)
        *///?}
    }

    // Items sit at a raised depth until 1.21.6 draws in submission order, so the overlay is lifted above them
    private fun begin(graphics: SlotGraphics) {
        //? if >=1.21 && <1.21.6 {
        /*graphics.pose().pushPose()
        graphics.pose().translate(0f, 0f, 300f)
        *///?} elif <1.21 {
        /*GlStateManager.pushMatrix()
        GlStateManager.translatef(0f, 0f, 300f)
        GlStateManager.disableLighting()
        *///?}
    }

    private fun end(graphics: SlotGraphics) {
        //? if >=1.21 && <1.21.6 {
        /*graphics.pose().popPose()
        *///?} elif <1.21 {
        /*GlStateManager.color4f(1f, 1f, 1f, 1f)
        GlStateManager.enableLighting()
        GlStateManager.popMatrix()
        *///?}
    }
}
