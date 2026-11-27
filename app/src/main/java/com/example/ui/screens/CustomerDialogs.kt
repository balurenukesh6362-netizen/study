package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.DefaultSeedData
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemCodec
import com.example.ui.FastBiteUiState
import com.example.ui.components.DishPhoto
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.FreshEmerald
import com.example.ui.theme.FreshEmeraldContainer
import com.example.ui.theme.OnFreshEmeraldContainer
import com.example.ui.theme.SaffronAmber
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishDetailBottomSheet(
    dish: MenuItemEntity,
    onDismiss: () -> Unit,
    onAddToCart: (Int) -> Unit
) {
    var qty by remember(dish.id) { mutableIntStateOf(1) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DishPhoto(
                imageUrl = dish.imageUrl,
                category = dish.category,
                contentDescription = dish.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = dish.comboTag,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = SaffronAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${dish.rating} (${dish.ratingCount})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dish.prepTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dish.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "₹${dish.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = dish.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (qty > 1) qty-- }) {
                            Icon(Icons.Filled.Remove, contentDescription = "Decrease quantity")
                        }
                        Text(
                            text = "$qty",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = { if (qty < 20) qty++ }) {
                            Icon(Icons.Filled.Add, contentDescription = "Increase quantity")
                        }
                    }
                }
                Button(
                    onClick = {
                        onAddToCart(qty)
                        onDismiss()
                    },
                    enabled = dish.available,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("sheet_add_to_cart"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (dish.available) "Add ₹${dish.price * qty} to Tray" else "Currently Sold Out",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutBottomSheet(
    state: FastBiteUiState,
    onDismiss: () -> Unit,
    onQtyChange: (String, Int) -> Unit,
    onClearCart: () -> Unit,
    onSelectTime: (String) -> Unit,
    onSelectPayment: (String) -> Unit,
    onPlaceOrder: () -> Unit
) {
    val paymentMethods = listOf("Google Pay", "PhonePe", "UPI", "Cash on Delivery")
    val tokenPreview = if (state.settings.customerUser?.method == "phone") {
        "Temporary Token (TEMP-FB)"
    } else {
        "Permanent Token (PERM-FB)"
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Meal Tray (${state.cartCount})",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                if (state.ui.cart.isNotEmpty()) {
                    TextButton(onClick = onClearCart) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear")
                    }
                }
            }

            if (state.ui.cart.isEmpty()) {
                Text(
                    text = "Your tray is empty. Add breakfast, lunch, or snack items to schedule a pickup!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                state.ui.cart.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "₹${item.price} each",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onQtyChange(item.dishId, -1) }) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Decrease ${item.name}")
                                }
                                Text(
                                    text = "${item.qty}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { onQtyChange(item.dishId, 1) }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Increase ${item.name}")
                                }
                            }
                            Text(
                                text = "₹${item.price * item.qty}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Schedule Counter Pickup Slot",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DefaultSeedData.pickupSlots.forEach { slot ->
                        FilterChip(
                            selected = state.ui.scheduledTime == slot,
                            onClick = { onSelectTime(slot) },
                            label = { Text(slot) }
                        )
                    }
                }

                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    paymentMethods.forEach { method ->
                        FilterChip(
                            selected = state.ui.selectedPayment == method,
                            onClick = { onSelectPayment(method) },
                            label = { Text(method) }
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Digital Pass: $tokenPreview",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Note: Online cancellations carry a ${state.settings.penaltyPercent}% prep fee. COD orders cancel free.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }

                Button(
                    onClick = {
                        onPlaceOrder()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("checkout_place_order_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Pay ₹${state.cartTotal} & Generate Token",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DigitalTokenDialog(
    order: OrderEntity,
    onDismiss: () -> Unit
) {
    val items = remember(order.itemsJson) { OrderItemCodec.decode(order.itemsJson) }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.ConfirmationNumber,
                contentDescription = null,
                tint = FreshEmerald,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Pickup Token Ready!",
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = FreshEmeraldContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${order.tokenType.uppercase()} TOKEN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnFreshEmeraldContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = order.tokenCode,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = OnFreshEmeraldContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pickup Slot: ${order.pickupTime} • ${order.paymentMethod}",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnFreshEmeraldContainer.copy(alpha = 0.85f)
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items.forEach { line ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${line.qty}× ${line.name}", style = MaterialTheme.typography.bodySmall)
                            Text("₹${line.price * line.qty}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Track Live Order")
            }
        }
    )
}

@Composable
fun CancelOrderConfirmDialog(
    order: OrderEntity,
    penaltyPercent: Int,
    onDismiss: () -> Unit,
    onConfirmCancel: () -> Unit
) {
    val penalty = if (order.paymentMode == "Online") {
        ((order.totalAmount * penaltyPercent) / 100.0).roundToInt()
    } else 0
    val refund = (order.totalAmount - penalty).coerceAtLeast(0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel Order ${order.id}?", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (order.paymentMode == "Online") {
                    Text(
                        "Because kitchen prep may already be underway, cancelling an Online order deducts a $penaltyPercent% penalty fee.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Order Total: ₹${order.totalAmount}", style = MaterialTheme.typography.bodySmall)
                            Text("Penalty Deducted ($penaltyPercent%): -₹$penalty", style = MaterialTheme.typography.bodySmall, color = CrimsonAlert, fontWeight = FontWeight.Bold)
                            Text("Instant Refund Amount: ₹$refund", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                } else {
                    Text(
                        "Your Cash on Delivery order will be cancelled immediately with ₹0 penalty.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmCancel,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert)
            ) {
                Text("Confirm Cancel")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Keep Order")
            }
        }
    )
}

@Composable
fun RateOrderDialog(
    order: OrderEntity,
    onDismiss: () -> Unit,
    onSubmit: (stars: Int, comment: String) -> Unit
) {
    var stars by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate Order ${order.id}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("How was your meal from FastBite Kitchen?", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { value ->
                        IconButton(onClick = { stars = value }) {
                            Icon(
                                imageVector = if (value <= stars) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = "$value stars",
                                tint = if (value <= stars) SaffronAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Optional review") },
                    placeholder = { Text("Freshness, taste, pickup speed...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(stars, comment) }) {
                Text("Submit Rating")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
