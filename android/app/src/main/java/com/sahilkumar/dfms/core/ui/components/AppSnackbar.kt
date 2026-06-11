package com.sahilkumar.dfms.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sahilkumar.dfms.core.ui.theme.BrandGreen
import com.sahilkumar.dfms.core.ui.theme.BrandRed

/** Visual category of an app snackbar. */
enum class SnackbarType { SUCCESS, ERROR, INFO }

/** Carries the [SnackbarType] through [SnackbarHostState.showSnackbar]. */
class AppSnackbarVisuals(
    override val message: String,
    val type: SnackbarType,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
) : SnackbarVisuals

/** Color-coded snackbar host: green success / red error with a leading icon. */
@Composable
fun AppSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState) { data ->
        val type = (data.visuals as? AppSnackbarVisuals)?.type ?: SnackbarType.INFO
        val container: Color = when (type) {
            SnackbarType.SUCCESS -> BrandGreen
            SnackbarType.ERROR -> BrandRed
            SnackbarType.INFO -> MaterialTheme.colorScheme.inverseSurface
        }
        val content: Color = when (type) {
            SnackbarType.INFO -> MaterialTheme.colorScheme.inverseOnSurface
            else -> Color.White
        }
        val icon: ImageVector = when (type) {
            SnackbarType.SUCCESS -> Icons.Filled.CheckCircle
            SnackbarType.ERROR -> Icons.Filled.ErrorOutline
            SnackbarType.INFO -> Icons.Filled.Info
        }

        Snackbar(
            shape = RoundedCornerShape(12.dp),
            containerColor = container,
            contentColor = content,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = content)
                Text(
                    data.visuals.message,
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

suspend fun SnackbarHostState.showSuccess(message: String) =
    showSnackbar(AppSnackbarVisuals(message, SnackbarType.SUCCESS))

suspend fun SnackbarHostState.showError(message: String) =
    showSnackbar(AppSnackbarVisuals(message, SnackbarType.ERROR))

suspend fun SnackbarHostState.showInfo(message: String) =
    showSnackbar(AppSnackbarVisuals(message, SnackbarType.INFO))
