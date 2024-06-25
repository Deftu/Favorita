package dev.deftu.favorita.client

import dev.deftu.favorita.FavoritaConstants
import dev.deftu.omnicore.annotations.GameSide
import dev.deftu.omnicore.annotations.Side
import dev.deftu.omnicore.client.render.OmniMatrixStack
import dev.deftu.omnicore.client.render.OmniRenderState
import dev.deftu.omnicore.client.render.OmniTessellator
import org.jetbrains.annotations.ApiStatus.Internal
import java.awt.Color

object FavoritaRenderer {

    /**
     * Draws a [width] pixel border around a rectangle.
     */
    @Internal
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun drawBorder(
        stack: OmniMatrixStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        size: Float,
    ) {
        drawBlock(stack, FavoritaConstants.GOLD_COLOR, x.toFloat(), y.toFloat(), x + width.toFloat(), y + size) // Top line
        drawBlock(stack, FavoritaConstants.GOLD_COLOR, x.toFloat(), y.toFloat(), x + size, y + height.toFloat()) // Left line
        drawBlock(stack, FavoritaConstants.GOLD_COLOR, x.toFloat(), y + height.toFloat() - size, x + width.toFloat(), y + height.toFloat()) // Bottom line
        drawBlock(stack, FavoritaConstants.GOLD_COLOR, x + width.toFloat() - size, y.toFloat(), x + width.toFloat(), y + height.toFloat()) // Right line
    }

    @GameSide(Side.CLIENT)
    private fun drawBlock(
        stack: OmniMatrixStack,
        color: Color,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ) {
        OmniRenderState.enableBlend()
        OmniRenderState.setBlendFuncSeparate(OmniRenderState.SrcFactor.SRC_ALPHA, OmniRenderState.DstFactor.ONE_MINUS_SRC_ALPHA, OmniRenderState.SrcFactor.ONE, OmniRenderState.DstFactor.ZERO)

        val red = color.red / 255f
        val green = color.green / 255f
        val blue = color.blue / 255f
        val alpha = color.alpha / 255f

        val buffer = OmniTessellator.getFromBuffer()
        buffer.beginWithDefaultShader(OmniTessellator.DrawModes.QUADS, OmniTessellator.VertexFormats.POSITION_COLOR)
        buffer.vertex(stack, x1, y2, 0f).color(red, green, blue, alpha).next()
        buffer.vertex(stack, x2, y2, 0f).color(red, green, blue, alpha).next()
        buffer.vertex(stack, x2, y1, 0f).color(red, green, blue, alpha).next()
        buffer.vertex(stack, x1, y1, 0f).color(red, green, blue, alpha).next()
        buffer.draw()

        OmniRenderState.disableBlend()
    }

}
