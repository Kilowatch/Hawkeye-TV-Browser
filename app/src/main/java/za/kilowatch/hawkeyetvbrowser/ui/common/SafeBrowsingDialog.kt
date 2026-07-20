@file:OptIn(ExperimentalTvMaterial3Api::class)

package za.kilowatch.hawkeyetvbrowser.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun SafeBrowsingDialog(
    url: String,
    onLeave: () -> Unit,
    onProceedAnyway: () -> Unit
) {
    Dialog(onDismissRequest = onLeave) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(520.dp)
                    .background(Color(0xFF1E1010), shape = RoundedCornerShape(20.dp))
                    .border(2.dp, Color(0xFFFF5252), RoundedCornerShape(20.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFFF5252).copy(alpha = 0.2f), shape = RoundedCornerShape(36.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Security Warning",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.safebrowsing_warning_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5252)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.safebrowsing_warning_msg),
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = url,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onLeave,
                            colors = ButtonDefaults.colors(
                                containerColor = Color(0xFF00E5FF),
                                contentColor = Color.Black
                            )
                        ) {
                            Text(stringResource(R.string.safebrowsing_leave))
                        }

                        Button(
                            onClick = onProceedAnyway,
                            colors = ButtonDefaults.colors(
                                containerColor = Color(0xFF37474F),
                                contentColor = Color.White
                            )
                        ) {
                            Text(stringResource(R.string.safebrowsing_proceed))
                        }
                    }
                }
            }
        }
    }
}
