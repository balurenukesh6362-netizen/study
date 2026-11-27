package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.model.MenuItemEntity

@Composable
fun DishEditorDialog(
    initialDish: MenuItemEntity?,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        name: String,
        category: String,
        price: Int,
        prepTime: String,
        comboTag: String,
        description: String,
        popular: Boolean,
        available: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialDish?.name ?: "") }
    var category by remember { mutableStateOf(initialDish?.category ?: "Lunch") }
    var priceText by remember { mutableStateOf(initialDish?.price?.toString() ?: "75") }
    var prepTime by remember { mutableStateOf(initialDish?.prepTime ?: "8 mins") }
    var comboTag by remember { mutableStateOf(initialDish?.comboTag ?: "Chef Special") }
    var description by remember { mutableStateOf(initialDish?.description ?: "") }
    var popular by remember { mutableStateOf(initialDish?.popular ?: false) }
    var available by remember { mutableStateOf(initialDish?.available ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialDish == null) "Add New Dish" else "Edit ${initialDish.name}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dish Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Breakfast", "Lunch", "Snacks").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = prepTime,
                        onValueChange = { prepTime = it },
                        label = { Text("Prep Time") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = comboTag,
                    onValueChange = { comboTag = it },
                    label = { Text("Highlight Tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mark as Popular", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = popular, onCheckedChange = { popular = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Available Now", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = available, onCheckedChange = { available = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toIntOrNull() ?: 50
                    onSave(initialDish?.id, name, category, price, prepTime, comboTag, description, popular, available)
                    onDismiss()
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save Dish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LogWastageDialog(
    menuItems: List<MenuItemEntity>,
    onDismiss: () -> Unit,
    onSubmit: (dishName: String, qty: Int, unitPrice: Int, reason: String) -> Unit
) {
    var selectedDish by remember { mutableStateOf(menuItems.firstOrNull()) }
    var qtyText by remember { mutableStateOf("2") }
    var reason by remember { mutableStateOf("End-of-shift surplus") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Food Surplus / Wastage", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Select Dish", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    menuItems.forEach { dish ->
                        FilterChip(
                            selected = selectedDish?.id == dish.id,
                            onClick = { selectedDish = dish },
                            label = { Text(dish.name) }
                        )
                    }
                }
                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Surplus Portions") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dish = selectedDish ?: return@Button
                    val qty = qtyText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    onSubmit(dish.name, qty, dish.price, reason)
                    onDismiss()
                },
                enabled = selectedDish != null
            ) {
                Text("Record Wastage")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun OwnerUnlockDialog(
    onDismiss: () -> Unit,
    onUnlock: (String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unlock Owner Vault", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Enter the Owner PIN (demo: owner123 or 1234) to edit settlement bank details and cancellation fee policy.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("Owner PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (onUnlock(pin)) onDismiss()
                }
            ) {
                Text("Unlock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
