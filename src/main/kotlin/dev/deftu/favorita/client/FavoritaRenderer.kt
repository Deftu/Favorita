package dev.deftu.favorita.client

import dev.deftu.favorita.FavoritaConstants
import dev.deftu.favorita.utils.IdentifierUtils
import dev.deftu.omnicore.annotations.GameSide
import dev.deftu.omnicore.annotations.Side
import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.client.render.OmniMatrixStack
import dev.deftu.omnicore.client.render.OmniRenderState
import dev.deftu.omnicore.client.render.OmniTessellator
import net.minecraft.util.Identifier
import org.jetbrains.annotations.ApiStatus.Internal
import java.awt.Color

object FavoritaRenderer {

    private val LOCK = IdentifierUtils.create(FavoritaConstants.ID, "lock.png")

    @JvmField
    val RED_BACKGROUND = Color(201, 42, 67, 89)

    @JvmField
    val ORANGE_BACKGROUND = Color(240, 185, 79, 89)

    @Internal
    @JvmStatic
    @JvmOverloads
    @GameSide(Side.CLIENT)
    fun drawSlotOverlay(
        stack: OmniMatrixStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        drawLock: Boolean = true,
        color: Color = RED_BACKGROUND
    ) {
        // Draw the red background
        drawBlock(
            stack,
            color,
            x.toFloat(),
            y.toFloat(),
            x + width.toFloat(),
            y + height.toFloat()
        )

        if (drawLock) {
            // Draw the lock itself
            drawTexture(
                LOCK,
                stack,
                Color(255, 255, 255, 166),
                x.toFloat(),
                y.toFloat(),
                x + width.toFloat(),
                y + height.toFloat()
            )
        }
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
        stack.push()
        OmniRenderState.enableBlend()
        OmniRenderState.setBlendFuncSeparate(OmniRenderState.SrcFactor.SRC_ALPHA, OmniRenderState.DstFactor.ONE_MINUS_SRC_ALPHA, OmniRenderState.SrcFactor.ONE, OmniRenderState.DstFactor.ZERO)

        val red = color.red / 255f
        val green = color.green / 255f
        val blue = color.blue / 255f
        val alpha = color.alpha / 255f

        val buffer = OmniTessellator.getFromBuffer()
        buffer.beginWithDefaultShader(OmniTessellator.DrawModes.QUADS, OmniTessellator.VertexFormats.POSITION_COLOR)
        buffer
            .vertex(stack, x1, y2, 0f)
            .color(red, green, blue, alpha)
            .next()
        buffer
            .vertex(stack, x2, y2, 0f)
            .color(red, green, blue, alpha)
            .next()
        buffer
            .vertex(stack, x2, y1, 0f)
            .color(red, green, blue, alpha)
            .next()
        buffer
            .vertex(stack, x1, y1, 0f)
            .color(red, green, blue, alpha)
            .next()
        buffer.draw()

        OmniRenderState.disableBlend()
        stack.pop()
    }

    @GameSide(Side.CLIENT)
    private fun drawTexture(
        identifier: Identifier,
        stack: OmniMatrixStack,
        color: Color,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ) {
        stack.push()
        OmniRenderState.enableBlend()
        OmniRenderState.setBlendFuncSeparate(OmniRenderState.SrcFactor.SRC_ALPHA, OmniRenderState.DstFactor.ONE_MINUS_SRC_ALPHA, OmniRenderState.SrcFactor.ONE, OmniRenderState.DstFactor.ZERO)

        val red = color.red / 255f
        val green = color.green / 255f
        val blue = color.blue / 255f
        val alpha = color.alpha / 255f

        stack.translate(0f, 0f, 300f)
        OmniClient.getTextureManager().bindTexture(0, identifier)

        val buffer = OmniTessellator.getFromBuffer()
        buffer.beginWithDefaultShader(OmniTessellator.DrawModes.QUADS, OmniTessellator.VertexFormats.POSITION_TEXTURE_COLOR)
        buffer
            .vertex(stack, x1, y2, 0f)
            .texture(0f, 1f)
            .color(red, green, blue, alpha)
            .next()
        buffer
            .vertex(stack, x2, y2, 0f)
            .texture(1f, 1f)
            .color(red, green, blue, alpha)
            .next()
        buffer
            .vertex(stack, x2, y1, 0f)
            .texture(1f, 0f)
            .color(red, green, blue, alpha)
            .next()
        buffer
            .vertex(stack, x1, y1, 0f)
            .texture(0f, 0f)
            .color(red, green, blue, alpha)
            .next()
        buffer.draw()

        OmniRenderState.disableBlend()
        stack.pop()
    }

}
