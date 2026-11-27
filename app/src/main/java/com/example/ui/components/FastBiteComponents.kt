package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.ui.ToastBanner
import com.example.ui.ToastTone
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonAlertContainer
import com.example.ui.theme.FastBiteOrange
import com.example.ui.theme.FastBiteOrangeContainer
import com.example.ui.theme.FastBiteOrangeDeep
import com.example.ui.theme.FreshEmerald
import com.example.ui.theme.FreshEmeraldContainer
import com.example.ui.theme.OnCrimsonAlertContainer
import com.example.ui.theme.OnFreshEmeraldContainer
import com.example.ui.theme.OnSaffronAmberContainer
import com.example.ui.theme.SaffronAmber
import com.example.ui.theme.SaffronAmberContainer

@Composable
fun FastBiteBrandBadge(
    modifier: Modifier = Modifier,
    iconSize: Dp = 38.dp,
    showSubtitle: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(FastBiteOrange, SaffronAmber, FastBiteOrangeDeep)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Bolt,
                contentDescription = stringResource(R.string.cd_brand_logo),
                tint = Color.White,
                modifier = Modifier.size(iconSize * 0.62f)
            )
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Fast",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Bite",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (showSubtitle) {
                Text(
                    text = "SMART CANTEEN",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun categoryFallbackDrawable(category: String): Int = when (category) {
    "Breakfast" -> R.drawable.img_breakfast_dish
    "Lunch" -> R.drawable.img_lunch_dish
    else -> R.drawable.img_snacks_dish
}

@Composable
fun DishPhoto(
    imageUrl: String,
    category: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val fallbackRes = categoryFallbackDrawable(category)
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = {
            Image(
                painter = painterResource(id = fallbackRes),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        },
        error = {
            Image(
                painter = painterResource(id = fallbackRes),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}

@Composable
fun OrderStatusPill(status: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        "Ready" -> FreshEmeraldContainer to OnFreshEmeraldContainer
        "Preparing" -> SaffronAmberContainer to OnSaffronAmberContainer
        "Order Placed" -> FastBiteOrangeContainer to FastBiteOrangeDeep
        "Cancelled" -> CrimsonAlertContainer to OnCrimsonAlertContainer
        "Completed" -> Color(0xFFE2E8F0) to Color(0xFF1E293B)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        modifier = modifier,
        color = bg,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun OrderProgressStepper(status: String, modifier: Modifier = Modifier) {
    val steps = listOf("Order Placed", "Preparing", "Ready", "Completed")
    val activeIdx = steps.indexOf(status)
    if (status == "Cancelled") {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = CrimsonAlertContainer,
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Order Cancelled",
                style = MaterialTheme.typography.labelMedium,
                color = OnCrimsonAlertContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
        return
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val reached = index <= activeIdx
            val barColor = when {
                !reached -> MaterialTheme.colorScheme.surfaceVariant
                status == "Ready" -> FreshEmerald
                else -> MaterialTheme.colorScheme.primary
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(barColor)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.replace("Order ", ""),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (reached) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FloatingToastHost(
    toast: ToastBanner?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = toast != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (toast != null) {
            val (container, accent, icon) = when (toast.tone) {
                ToastTone.EMERALD -> Triple(FreshEmeraldContainer, FreshEmerald, Icons.Filled.CheckCircle)
                ToastTone.AMBER -> Triple(SaffronAmberContainer, SaffronAmber, Icons.Filled.Warning)
                ToastTone.SLATE -> Triple(MaterialTheme.colorScheme.inverseSurface, MaterialTheme.colorScheme.primary, Icons.Filled.NotificationsActive)
                ToastTone.ORANGE -> Triple(FastBiteOrangeContainer, FastBiteOrange, Icons.Filled.NotificationsActive)
            }
            val textColor = if (toast.tone == ToastTone.SLATE) {
                MaterialTheme.colorScheme.inverseOnSurface
            } else {
                Color(0xFF0F172A)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = container,
                tonalElevation = 6.dp,
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = toast.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = toast.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.85f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss notification",
                            tint = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
