package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.DefaultSeedData
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderItemCodec
import com.example.data.model.OwnerBankInfo
import com.example.ui.FastBiteUiState
import com.example.ui.FastBiteViewModel
import com.example.ui.StaffTab
import com.example.ui.components.FastBiteBrandBadge
import com.example.ui.components.OrderStatusPill
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.FreshEmerald
import com.example.ui.theme.SaffronAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffPortalScreen(
    state: FastBiteUiState,
    viewModel: FastBiteViewModel,
    onSwitchToLanding: () -> Unit
) {
    BackHandler {
        if (state.ui.staffTab != StaffTab.ORDERS) {
            viewModel.selectStaffTab(StaffTab.ORDERS)
        } else {
            onSwitchToLanding()
        }
    }

    var showDishDialog by remember { mutableStateOf(false) }
    var editingDish by remember { mutableStateOf<MenuItemEntity?>(null) }
    var showWastageDialog by remember { mutableStateOf(false) }
    var showOwnerPinDialog by remember { mutableStateOf(false) }

    val navItems = listOf(
        Triple(StaffTab.ORDERS, "Queue", Icons.Filled.ReceiptLong),
        Triple(StaffTab.MENU, "Menu", Icons.Filled.RestaurantMenu),
        Triple(StaffTab.WEEKLY, "Plan", Icons.Filled.CalendarMonth),
        Triple(StaffTab.PAYMENTS, "Ledger", Icons.Filled.Payments),
        Triple(StaffTab.WASTAGE_BANK, "Vault", Icons.Filled.AccountBalance)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 700.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FastBiteBrandBadge(iconSize = 30.dp)
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "KITCHEN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.toggleSound() },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = if (state.settings.soundEnabled) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsOff,
                                contentDescription = "Toggle chimes"
                            )
                        }
                        IconButton(
                            onClick = onSwitchToLanding,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = "Switch portal")
                        }
                        IconButton(
                            onClick = { viewModel.signOutStaff() },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign out staff")
                        }
                    }
                )
            },
            bottomBar = {
                if (!isWide) {
                    NavigationBar {
                        navItems.forEach { (tab, label, icon) ->
                            NavigationBarItem(
                                selected = state.ui.staffTab == tab,
                                onClick = { viewModel.selectStaffTab(tab) },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (state.ui.staffTab == StaffTab.MENU) {
                    FloatingActionButton(
                        onClick = {
                            editingDish = null
                            showDishDialog = true
                        },
                        modifier = Modifier.testTag("staff_add_dish_fab")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add new dish")
                    }
                } else if (state.ui.staffTab == StaffTab.WASTAGE_BANK) {
                    FloatingActionButton(
                        onClick = { showWastageDialog = true },
                        modifier = Modifier.testTag("staff_log_wastage_fab")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Log wastage")
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWide) {
                    NavigationRail(modifier = Modifier.fillMaxHeight()) {
                        navItems.forEach { (tab, label, icon) ->
                            NavigationRailItem(
                                selected = state.ui.staffTab == tab,
                                onClick = { viewModel.selectStaffTab(tab) },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(modifier = Modifier.widthIn(max = 780.dp).fillMaxSize()) {
                        when (state.ui.staffTab) {
                            StaffTab.ORDERS -> StaffOrdersTab(state, viewModel)
                            StaffTab.MENU -> StaffMenuTab(
                                state = state,
                                viewModel = viewModel,
                                onEdit = { dish ->
                                    editingDish = dish
                                    showDishDialog = true
                                }
                            )
                            StaffTab.WEEKLY -> StaffWeeklyTab(state, viewModel)
                            StaffTab.PAYMENTS -> StaffPaymentsTab(state, viewModel)
                            StaffTab.WASTAGE_BANK -> StaffWastageBankTab(
                                state = state,
                                viewModel = viewModel,
                                onOpenPinDialog = { showOwnerPinDialog = true }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDishDialog) {
        DishEditorDialog(
            initialDish = editingDish,
            onDismiss = { showDishDialog = false },
            onSave = { id, name, cat, price, prep, tag, desc, pop, avail ->
                viewModel.upsertDish(id, name, cat, price, prep, tag, desc, pop, avail)
            }
        )
    }

    if (showWastageDialog) {
        LogWastageDialog(
            menuItems = state.menuItems,
            onDismiss = { showWastageDialog = false },
            onSubmit = { dishName, qty, price, reason ->
                viewModel.logWastage(dishName, qty, price, reason)
            }
        )
    }

    if (showOwnerPinDialog) {
        OwnerUnlockDialog(
            onDismiss = { showOwnerPinDialog = false },
            onUnlock = { pin -> viewModel.unlockOwnerVault(pin) }
        )
    }
}

@Composable
private fun StaffOrdersTab(state: FastBiteUiState, viewModel: FastBiteViewModel) {
    val active = state.orders.count { it.status in listOf("Order Placed", "Preparing") }
    val ready = state.orders.count { it.status == "Ready" }
    val revenue = state.orders.filter { it.status != "Cancelled" }.sumOf { it.totalAmount }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard("In Prep", "$active", SaffronAmber, Modifier.weight(1f))
                KpiCard("Ready", "$ready", FreshEmerald, Modifier.weight(1f))
                KpiCard("Revenue", "₹$revenue", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            }
        }

        item {
            Text(
                text = "Live Kitchen Queue (${state.orders.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(state.orders, key = { it.id }) { order ->
            val items = remember(order.itemsJson) { OrderItemCodec.decode(order.itemsJson) }
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${order.id} • ${order.tokenCode}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                            Text("${order.customerName} (${order.customerContact})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OrderStatusPill(status = order.status)
                    }

                    items.forEach { line ->
                        Text("• ${line.qty}× ${line.name} (₹${line.price * line.qty})", style = MaterialTheme.typography.bodySmall)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Slot ${order.pickupTime} • ₹${order.totalAmount} (${order.paymentMode})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        when (order.status) {
                            "Order Placed" -> Button(
                                onClick = { viewModel.advanceOrderStatus(order, "Preparing") },
                                modifier = Modifier.testTag("order_advance_${order.id}")
                            ) { Text("Start Prep") }
                            "Preparing" -> Button(
                                onClick = { viewModel.advanceOrderStatus(order, "Ready") },
                                colors = ButtonDefaults.buttonColors(containerColor = FreshEmerald),
                                modifier = Modifier.testTag("order_advance_${order.id}")
                            ) { Text("Mark Ready") }
                            "Ready" -> OutlinedButton(
                                onClick = { viewModel.advanceOrderStatus(order, "Completed") },
                                modifier = Modifier.testTag("order_advance_${order.id}")
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Complete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(title: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StaffMenuTab(
    state: FastBiteUiState,
    viewModel: FastBiteViewModel,
    onEdit: (MenuItemEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 84.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Menu Inventory (${state.menuItems.size} items)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(state.menuItems, key = { it.id }) { dish ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dish.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${dish.category} • ₹${dish.price} • ${dish.prepTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onEdit(dish) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit ${dish.name}")
                    }
                    IconButton(onClick = { viewModel.deleteDish(dish.id, dish.name) }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete ${dish.name}", tint = CrimsonAlert)
                    }
                    Switch(
                        checked = dish.available,
                        onCheckedChange = { viewModel.toggleDishAvailability(dish.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StaffWeeklyTab(state: FastBiteUiState, viewModel: FastBiteViewModel) {
    val selectedDay = state.ui.staffSelectedDay
    val activeIds = state.weeklyPlans.firstOrNull { it.dayOfWeek == selectedDay }?.dishIds().orEmpty()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Weekly Menu Planner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DefaultSeedData.daysOfWeek.forEach { day ->
                    FilterChip(
                        selected = selectedDay == day,
                        onClick = { viewModel.setStaffCalendarDay(day) },
                        label = { Text(day.take(3)) }
                    )
                }
            }
        }
        items(state.menuItems, key = { "plan_${selectedDay}_${it.id}" }) { dish ->
            val included = dish.id in activeIds
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dish.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("${dish.category} • ₹${dish.price}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = included,
                        onCheckedChange = { viewModel.toggleWeeklyDish(selectedDay, dish.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StaffPaymentsTab(state: FastBiteUiState, viewModel: FastBiteViewModel) {
    val filter = state.ui.paymentFilter
    val filtered = state.orders.filter {
        when (filter) {
            "Online" -> it.paymentMode == "Online" && it.status != "Cancelled"
            "COD" -> it.paymentMode == "COD" && it.status != "Cancelled"
            "Cancelled" -> it.status == "Cancelled"
            else -> true
        }
    }
    val onlineTotal = state.orders.filter { it.paymentMode == "Online" && it.status != "Cancelled" }.sumOf { it.totalAmount }
    val codTotal = state.orders.filter { it.paymentMode == "COD" && it.status != "Cancelled" }.sumOf { it.totalAmount }
    val penaltyTotal = state.orders.sumOf { it.penaltyDeducted }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("Online", "₹$onlineTotal", FreshEmerald, Modifier.weight(1f))
                KpiCard("COD", "₹$codTotal", SaffronAmber, Modifier.weight(1f))
                KpiCard("Penalties", "₹$penaltyTotal", CrimsonAlert, Modifier.weight(1f))
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "Online", "COD", "Cancelled").forEach { f ->
                    FilterChip(
                        selected = filter == f,
                        onClick = { viewModel.setPaymentFilter(f) },
                        label = { Text(f) }
                    )
                }
            }
        }
        items(filtered, key = { "pay_${it.id}" }) { order ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${order.id} • ${order.customerName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${order.paymentMethod} (${order.paymentMode}) • ${order.createdAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (order.status == "Cancelled" && order.paymentMode == "Online") {
                            Text("Refund ₹${order.refundAmount} • Fee ₹${order.penaltyDeducted}", style = MaterialTheme.typography.labelSmall, color = CrimsonAlert)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("₹${order.totalAmount}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                        OrderStatusPill(status = order.status)
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffWastageBankTab(
    state: FastBiteUiState,
    viewModel: FastBiteViewModel,
    onOpenPinDialog: () -> Unit
) {
    val bank = state.settings.ownerBank
    var holder by remember(bank.accountHolder) { mutableStateOf(bank.accountHolder) }
    var bankName by remember(bank.bankName) { mutableStateOf(bank.bankName) }
    var accNo by remember(bank.accountNumber) { mutableStateOf(bank.accountNumber) }
    var ifsc by remember(bank.ifsc) { mutableStateOf(bank.ifsc) }
    var upi by remember(bank.upiId) { mutableStateOf(bank.upiId) }
    var penaltyText by remember(state.settings.penaltyPercent) { mutableStateOf(state.settings.penaltyPercent.toString()) }

    val totalWastageCost = state.wastageLogs.sumOf { it.cost }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 84.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (state.ui.ownerUnlocked) Icons.Filled.LockOpen else Icons.Filled.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Owner Bank & Policy Vault", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        if (state.ui.ownerUnlocked) {
                            OutlinedButton(onClick = { viewModel.lockOwnerVault() }) { Text("Lock") }
                        } else {
                            Button(onClick = onOpenPinDialog) { Text("Unlock") }
                        }
                    }

                    if (!state.ui.ownerUnlocked) {
                        Text(
                            "Settlement Bank: ${bank.bankName} (••••${bank.accountNumber.takeLast(4)}) • UPI: ${bank.upiId} • Cancellation Penalty: ${state.settings.penaltyPercent}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        OutlinedTextField(value = holder, onValueChange = { holder = it }, label = { Text("Account Holder") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("Bank Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = accNo, onValueChange = { accNo = it }, label = { Text("Account No") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = ifsc, onValueChange = { ifsc = it }, label = { Text("IFSC") }, modifier = Modifier.weight(1f), singleLine = true)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = upi, onValueChange = { upi = it }, label = { Text("Settlement UPI") }, modifier = Modifier.weight(1.4f), singleLine = true)
                            OutlinedTextField(
                                value = penaltyText,
                                onValueChange = { penaltyText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Penalty %") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.8f),
                                singleLine = true
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.updateOwnerSettings(
                                    OwnerBankInfo(holder, bankName, accNo, ifsc, upi),
                                    penaltyText.toIntOrNull()?.coerceIn(0, 50) ?: state.settings.penaltyPercent
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Bank & Penalty Policy")
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kitchen Wastage Log (${state.wastageLogs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Total Loss: ₹$totalWastageCost", style = MaterialTheme.typography.labelLarge, color = CrimsonAlert, fontWeight = FontWeight.Bold)
            }
        }

        items(state.wastageLogs, key = { it.id }) { log ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${log.qty}× ${log.dishName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${log.reason} • ${log.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("-₹${log.cost}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = CrimsonAlert)
                }
            }
        }
    }
}
