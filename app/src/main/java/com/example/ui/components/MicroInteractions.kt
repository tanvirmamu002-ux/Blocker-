package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Adds an iOS-like tactile press bounce + haptic feedback interaction to any composable.
 */
fun Modifier.bounceClick(
    scaleDownTo: Float = 0.94f,
    onClick: (() -> Unit)? = null
): Modifier = this.then(
    Modifier.pointerInput(Unit) {
        // Handled in composable wrapper when needed or via pointer input
    }
)

/**
 * Composable wrapper / Modifier extension for interactive tactile press with Haptics
 */
@Composable
fun Modifier.pressScaleEffect(
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            scale.animateTo(scaleDown, spring(stiffness = Spring.StiffnessHigh))
        } else {
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        }
    }

    return this
        .scale(scale.value)
        .pointerInput(Unit) {
            while (true) {
                awaitPointerEventScope {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    val up = waitForUpOrCancellation()
                    isPressed = false
                    if (up != null) {
                        onClick()
                    }
                }
            }
        }
}

/**
 * Animated Celebration Success Visualizer (Confetti & Pulse Checkmark)
 */
@Composable
fun CelebrationSuccessAnimation(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    tintColor: Color = Color(0xFF5BA88C)
) {
    val progress = remember { Animatable(0f) }
    val checkScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            checkScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_ring")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )

    val particles = remember {
        List(14) {
            val angle = Random.nextDouble(0.0, 2.0 * Math.PI)
            val distance = Random.nextDouble(40.0, 80.0)
            val particleSize = Random.nextFloat() * 4f + 3f
            val color = listOf(
                tintColor,
                Color(0xFF6B9AC4),
                Color(0xFFD4984F),
                Color(0xFF8B7EC8),
                Color(0xFFE58B88)
            ).random()
            ParticleData(angle, distance, particleSize, color)
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Particle explosion Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.toPx() / 2, size.toPx() / 2)
            val currentProgress = progress.value

            // Outer subtle pulsing wave
            drawCircle(
                color = tintColor.copy(alpha = pulseAlpha),
                radius = (size.toPx() / 2.2f) * pulseScale,
                style = Stroke(width = 2.dp.toPx())
            )

            // Particles
            particles.forEach { p ->
                val dist = (p.distance * currentProgress).toFloat()
                val px = center.x + (dist * cos(p.angle)).toFloat()
                val py = center.y + (dist * sin(p.angle)).toFloat()
                val alpha = (1f - currentProgress).coerceIn(0f, 1f)

                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size * (1f - currentProgress * 0.3f),
                    center = Offset(px, py)
                )
            }
        }

        // Center spring icon
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = "Success",
            tint = tintColor,
            modifier = Modifier
                .size(size * 0.55f)
                .scale(checkScale.value)
        )
    }
}

private data class ParticleData(
    val angle: Double,
    val distance: Double,
    val size: Float,
    val color: Color
)
