@file:OptIn(ExperimentalTvMaterial3Api::class)

package za.kilowatch.hawkeyetvbrowser.ui.reader

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import za.kilowatch.hawkeyetvbrowser.R
import za.kilowatch.hawkeyetvbrowser.core.reader.ArticleData

enum class ReaderTheme(val bgHex: String, val textHex: String, val labelRes: Int) {
    DARK("#121212", "#E0E0E0", R.string.reader_theme_dark),
    SEPIA("#F4ECD8", "#3E2723", R.string.reader_theme_sepia),
    LIGHT("#FFFFFF", "#212121", R.string.reader_theme_light)
}

@Composable
fun ReaderModeScreen(
    article: ArticleData,
    onClose: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(ReaderTheme.DARK) }
    var fontScalePercent by remember { mutableIntStateOf(120) }

    val bgColor = when (selectedTheme) {
        ReaderTheme.DARK -> Color(0xFF121212)
        ReaderTheme.SEPIA -> Color(0xFFF4ECD8)
        ReaderTheme.LIGHT -> Color(0xFFFFFFFF)
    }

    val textColor = when (selectedTheme) {
        ReaderTheme.DARK -> Color(0xFFE0E0E0)
        ReaderTheme.SEPIA -> Color(0xFF3E2723)
        ReaderTheme.LIGHT -> Color(0xFF212121)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(24.dp)
    ) {
        // Control Bar Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = when (selectedTheme) {
                        ReaderTheme.DARK -> Color(0xFF1E1E1E)
                        ReaderTheme.SEPIA -> Color(0xFFE8DCC4)
                        ReaderTheme.LIGHT -> Color(0xFFF5F5F5)
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.reader_mode_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = stringResource(R.string.reader_reading_time, article.estimatedReadingTimeMinutes),
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.7f)
                )
            }

            // Theme selector buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReaderTheme.values().forEach { theme ->
                    Button(
                        onClick = { selectedTheme = theme },
                        colors = ButtonDefaults.colors(
                            containerColor = if (selectedTheme == theme) Color(0xFF00E5FF) else Color.DarkGray,
                            contentColor = if (selectedTheme == theme) Color.Black else Color.White
                        )
                    ) {
                        Text(stringResource(theme.labelRes), fontSize = 12.sp)
                    }
                }

                // Font size adjusters
                Button(
                    onClick = { if (fontScalePercent > 80) fontScalePercent -= 15 },
                    colors = ButtonDefaults.colors(containerColor = Color.Gray)
                ) {
                    Text("A-", fontSize = 12.sp)
                }

                Button(
                    onClick = { if (fontScalePercent < 200) fontScalePercent += 15 },
                    colors = ButtonDefaults.colors(containerColor = Color.Gray)
                ) {
                    Text("A+", fontSize = 12.sp)
                }

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.colors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(stringResource(R.string.download_dialog_close), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Styled article content HTML
        val htmlData = remember(article, selectedTheme, fontScalePercent) {
            """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        background-color: ${selectedTheme.bgHex};
                        color: ${selectedTheme.textHex};
                        font-family: sans-serif;
                        font-size: ${fontScalePercent}%;
                        line-height: 1.6;
                        padding: 16px 24px;
                    }
                    h1 { font-size: 1.8em; margin-bottom: 8px; }
                    .byline { color: #888; font-style: italic; margin-bottom: 24px; }
                    img { max-width: 100%; height: auto; border-radius: 8px; }
                    p { margin-bottom: 16px; }
                </style>
            </head>
            <body>
                <h1>${article.title}</h1>
                ${if (article.byline.isNotBlank()) "<div class='byline'>${article.byline}</div>" else ""}
                ${article.contentHtml}
            </body>
            </html>
            """.trimIndent()
        }

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = false
                    settings.defaultTextEncodingName = "utf-8"
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL(null, htmlData, "text/html", "utf-8", null)
            },
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        )
    }
}
