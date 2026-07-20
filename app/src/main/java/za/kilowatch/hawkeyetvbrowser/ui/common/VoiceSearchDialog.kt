@file:OptIn(ExperimentalTvMaterial3Api::class)

package za.kilowatch.hawkeyetvbrowser.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.core.voice.VoiceState

@Composable
fun VoiceSearchDialog(
    state: VoiceState,
    onDismiss: () -> Unit,
    onResultConfirmed: (String) -> Unit,
    onRetry: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(480.dp)
                    .background(Color(0xFF181C24), shape = RoundedCornerShape(20.dp))
                    .border(2.dp, Color(0xFF00E5FF).copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.voice_search_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Pulse animation for listening state
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = 1.25f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseScale"
                    )

                    val isListening = state is VoiceState.Listening

                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .scale(if (isListening) pulseScale else 1.0f)
                            .background(
                                color = when (state) {
                                    is VoiceState.Listening -> Color(0xFF00E5FF).copy(alpha = 0.2f)
                                    is VoiceState.Error -> Color(0xFFFF5252).copy(alpha = 0.2f)
                                    is VoiceState.Success -> Color(0xFF00E676).copy(alpha = 0.2f)
                                    else -> Color(0xFF263238)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = when (state) {
                                        is VoiceState.Listening -> Color(0xFF00E5FF)
                                        is VoiceState.Error -> Color(0xFFFF5252)
                                        is VoiceState.Success -> Color(0xFF00E676)
                                        else -> Color(0xFF455A64)
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (state is VoiceState.Error) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = stringResource(R.string.voice_search_mic_desc),
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Status message
                    val message = when (state) {
                        is VoiceState.Idle -> stringResource(R.string.voice_search_idle)
                        is VoiceState.Listening -> stringResource(R.string.voice_search_listening)
                        is VoiceState.Processing -> stringResource(R.string.voice_search_processing)
                        is VoiceState.Success -> "\"${state.recognizedText}\""
                        is VoiceState.Error -> state.message
                    }

                    Text(
                        text = message,
                        fontSize = 18.sp,
                        color = when (state) {
                            is VoiceState.Success -> Color(0xFF00E676)
                            is VoiceState.Error -> Color(0xFFFF5252)
                            else -> Color.White.copy(alpha = 0.9f)
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state is VoiceState.Error) {
                            Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.colors(
                                    containerColor = Color(0xFF00E5FF),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(stringResource(R.string.voice_search_retry))
                            }
                        }

                        if (state is VoiceState.Success) {
                            Button(
                                onClick = { onResultConfirmed(state.recognizedText) },
                                colors = ButtonDefaults.colors(
                                    containerColor = Color(0xFF00E676),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(stringResource(R.string.voice_search_search))
                            }
                        }

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.colors(
                                containerColor = Color(0xFF37474F),
                                contentColor = Color.White
                            )
                        ) {
                            Text(stringResource(R.string.voice_search_cancel))
                        }
                    }
                }
            }
        }
    }
}
