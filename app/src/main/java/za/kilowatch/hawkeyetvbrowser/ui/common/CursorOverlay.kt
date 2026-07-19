package za.kilowatch.hawkeyetvbrowser.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

/** Scroll zone threshold in px — must match CursorController.scrollZoneHeight */
private const val SCROLL_ZONE_PX = 120f

@Composable
fun CursorOverlay(
    cursorX: Float,
    cursorY: Float,
    cursorColor: Color,
    visible: Boolean,
    topScrollZoneEnabled: Boolean = false,
    toolbarVisible: Boolean = true,
    toolbarHeight: Float = 0f,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val infiniteTransition = rememberInfiniteTransition(label = "CursorAnimations")

    // Slow, premium rotation for the outer reticle
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CursorRotation"
    )

    // Breathing pulse for ambient glow aura
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CursorGlowPulse"
    )

    // Bounce offset for scroll indicators
    val scrollBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScrollIndicatorBounce"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val topLimit = if (toolbarVisible) toolbarHeight else 0f
        val inTopZone = topScrollZoneEnabled && cursorY <= topLimit + SCROLL_ZONE_PX
        val inBottomZone = cursorY >= h - SCROLL_ZONE_PX

        // --- Top scroll-zone bar (Glow) ---
        // Glow is rendered right below the toolbar when the toolbar is visible
        val topAlpha = if (inTopZone) 0.35f else 0.03f
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    cursorColor.copy(alpha = topAlpha),
                    Color.Transparent
                ),
                startY = topLimit,
                endY = topLimit + SCROLL_ZONE_PX
            ),
            topLeft = Offset(0f, topLimit),
            size = Size(w, SCROLL_ZONE_PX)
        )

        // --- Bottom scroll-zone bar (Glow) ---
        val botAlpha = if (inBottomZone) 0.35f else 0.03f
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    cursorColor.copy(alpha = botAlpha)
                ),
                startY = h - SCROLL_ZONE_PX,
                endY = h
            ),
            topLeft = Offset(0f, h - SCROLL_ZONE_PX),
            size = Size(w, SCROLL_ZONE_PX)
        )

        val center = Offset(cursorX, cursorY)

        // 1. Ambient pulsing outer gold/cyan aura
        drawCircle(
            color = cursorColor.copy(alpha = 0.08f),
            radius = 32f * pulseGlow,
            center = center
        )

        // 2. Focused mid-glow ring
        drawCircle(
            color = cursorColor.copy(alpha = 0.2f),
            radius = 18f,
            center = center
        )

        // 3. Rotating Dashed Reticle Ring (Ultra Premium Look)
        rotate(rotation, center) {
            drawCircle(
                color = cursorColor.copy(alpha = 0.65f),
                radius = 12f,
                center = center,
                style = Stroke(
                    width = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )
            )
        }

        // 4. Sharp inner solid target circle
        drawCircle(
            color = cursorColor,
            radius = 3.5f,
            center = center
        )

        // 5. Dynamic premium scroll arrows with spring bounce motion
        if (inTopZone) {
            val arrowOffset = -22f - scrollBounce
            val path = Path().apply {
                moveTo(cursorX, cursorY + arrowOffset - 8f)
                lineTo(cursorX - 5f, cursorY + arrowOffset)
                lineTo(cursorX + 5f, cursorY + arrowOffset)
                close()
            }
            drawPath(path = path, color = cursorColor.copy(alpha = 0.85f))
        } else if (inBottomZone) {
            val arrowOffset = 22f + scrollBounce
            val path = Path().apply {
                moveTo(cursorX, cursorY + arrowOffset + 8f)
                lineTo(cursorX - 5f, cursorY + arrowOffset)
                lineTo(cursorX + 5f, cursorY + arrowOffset)
                close()
            }
            drawPath(path = path, color = cursorColor.copy(alpha = 0.85f))
        }
    }
}
