package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.DefaultSeedData
import com.example.data.model.MenuItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemCodec
import com.example.ui.CustomerTab
import com.example.ui.FastBiteUiState
import com.example.ui.FastBiteViewModel
import com.example.ui.components.DishPhoto
import com.example.ui.components.FastBiteBrandBadge
import com.example.ui.components.OrderProgressStepper
import com.example.ui.components.OrderStatusPill
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.FreshEmerald
import com.example.ui.theme.FreshEmeraldContainer
import com.example.ui.theme.OnFreshEmeraldContainer
import com.example.ui.theme.SaffronAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerPortalScreen(
    state: FastBiteUiState,
    viewModel: FastBiteViewModel,
    onSwitchToLanding: () -> Unit
) {
    BackHandler {
        if (state.ui.customerTab != CustomerTab.MENU) {
            viewModel.selectCustomerTab(CustomerTab.MENU)
        } else {
            onSwitchToLanding()
        }
    }

    var selectedDish by remember { mutableStateOf<MenuItemEntity?>(null) }
    var showCartSheet by remember { mutableStateOf(false) }
    var orderToCancel by remember { mutableStateOf<OrderEntity?>(null) }
    var orderToRate by remember { mutableStateOf<OrderEntity?>(null) }

    val navItems = listOf(
        Triple(CustomerTab.MENU, "Menu", Icons.Filled.RestaurantMenu),
        Triple(CustomerTab.ORDERS, "Orders", Icons.Filled.ReceiptLong),
        Triple(CustomerTab.WEEKLY, "Weekly", Icons.Filled.CalendarMonth),
        Triple(CustomerTab.INFO, "Info", Icons.Filled.Info)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 700.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    title = { FastBiteBrandBadge(iconSize = 32.dp) },
                    actions = {
                        IconButton(
                            onClick = { viewModel.toggleSound() },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = if (state.settings.soundEnabled) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsOff,
                                contentDescription = "Toggle notification chimes"
                            )
                        }
                        IconButton(
                            onClick = { showCartSheet = true },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("topbar_cart_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (state.cartCount > 0) {
                                        Badge { Text("${state.cartCount}") }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingBag,
                                    contentDescription = stringResource(R.string.cd_cart)
                                )
                            }
                        }
                        IconButton(
                            onClick = onSwitchToLanding,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SwapHoriz,
                                contentDescription = "Switch portal"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (!isWide) {
                    Column {
                        if (state.cartCount > 0 && state.ui.customerTab != CustomerTab.ORDERS) {
                            StickyCartSummaryBar(
                                count = state.cartCount,
                                total = state.cartTotal,
                                onOpenCart = { showCartSheet = true }
                            )
                        }
                        NavigationBar {
                            navItems.forEach { (tab, label, icon) ->
                                NavigationBarItem(
                                    selected = state.ui.customerTab == tab,
                                    onClick = { viewModel.selectCustomerTab(tab) },
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label) }
                                )
                            }
                        }
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
                                selected = state.ui.customerTab == tab,
                                onClick = { viewModel.selectCustomerTab(tab) },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(modifier = Modifier.widthIn(max = 760.dp).fillMaxSize()) {
                        when (state.ui.customerTab) {
                            CustomerTab.MENU -> CustomerMenuContent(
                                state = state,
                                viewModel = viewModel,
                                onDishClick = { selectedDish = it }
                            )
                            CustomerTab.ORDERS -> CustomerOrdersContent(
                                state = state,
                                onCancelClick = { orderToCancel = it },
                                onRateClick = { orderToRate = it }
                            )
                            CustomerTab.WEEKLY -> CustomerWeeklyContent(
                                state = state,
                                viewModel = viewModel
                            )
                            CustomerTab.INFO -> CustomerInfoContent(
                                state = state,
                                viewModel = viewModel,
                                onSwitchRole = onSwitchToLanding
                            )
                        }

                        if (isWide && state.cartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .navigationBarsPadding()
                                    .padding(16.dp)
                            ) {
                                StickyCartSummaryBar(
                                    count = state.cartCount,
                                    total = state.cartTotal,
                                    onOpenCart = { showCartSheet = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedDish?.let { dish ->
        DishDetailBottomSheet(
            dish = dish,
            onDismiss = { selectedDish = null },
            onAddToCart = { qty -> viewModel.addToCart(dish, qty) }
        )
    }

    if (showCartSheet) {
        CartCheckoutBottomSheet(
            state = state,
            onDismiss = { showCartSheet = false },
            onQtyChange = { id, delta -> viewModel.updateCartQty(id, delta) },
            onClearCart = { viewModel.clearCart() },
            onSelectTime = { viewModel.setScheduledTime(it) },
            onSelectPayment = { viewModel.setSelectedPayment(it) },
            onPlaceOrder = { viewModel.placeOrder() }
        )
    }

    state.ui.latestTokenOrder?.let { tokenOrder ->
        DigitalTokenDialog(
            order = tokenOrder,
            onDismiss = { viewModel.dismissLatestToken() }
        )
    }

    orderToCancel?.let { order ->
        CancelOrderConfirmDialog(
            order = order,
            penaltyPercent = state.settings.penaltyPercent,
            onDismiss = { orderToCancel = null },
            onConfirmCancel = {
                viewModel.cancelOrder(order.id)
                orderToCancel = null
            }
        )
    }

    orderToRate?.let { order ->
        RateOrderDialog(
            order = order,
            onDismiss = { orderToRate = null },
            onSubmit = { stars, comment ->
                viewModel.rateOrder(order.id, stars, comment)
                orderToRate = null
            }
        )
    }
}

@Composable
private fun StickyCartSummaryBar(
    count: Int,
    total: Int,
    onOpenCart: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onOpenCart)
            .testTag("sticky_cart_bar"),
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "$count Item${if (count > 1) "s" else ""} • ₹$total",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = "Checkout →",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun CustomerMenuContent(
    state: FastBiteUiState,
    viewModel: FastBiteViewModel,
    onDishClick: (MenuItemEntity) -> Unit
) {
    val categories = listOf("All", "Breakfast", "Lunch", "Snacks")
    val query = state.ui.searchQuery.trim()
    val filtered = remember(state.menuItems, state.ui.activeCategory, query) {
        state.menuItems.filter { item ->
            val catMatch = state.ui.activeCategory == "All" || item.category == state.ui.activeCategory
            val qMatch = query.isEmpty() || item.name.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true)
            catMatch && qMatch
        }
    }
    val popularDishes = remember(state.menuItems) { state.menuItems.filter { it.popular && it.available } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            val user = state.settings.customerUser
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hi, ${user?.name ?: "Diner"}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Order fresh meals & skip counter queues",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (user?.method == "phone") "TEMP PASS" else "PERM PASS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = state.ui.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search dosa, thali, samosa, chai...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = state.ui.activeCategory == cat,
                        onClick = { viewModel.setCategory(cat) },
                        label = { Text(cat) }
                    )
                }
            }
        }

        if (popularDishes.isNotEmpty() && query.isEmpty() && state.ui.activeCategory == "All") {
            item {
                Text(
                    text = "Popular Today",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(popularDishes, key = { "pop_${it.id}" }) { dish ->
                        Card(
                            modifier = Modifier
                                .width(205.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { onDishClick(dish) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
                        ) {
                            Column {
                                DishPhoto(
                                    imageUrl = dish.imageUrl,
                                    category = dish.category,
                                    contentDescription = dish.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                )
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = dish.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "₹${dish.price}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "★ ${dish.rating}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SaffronAmber,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "${state.ui.activeCategory} Menu (${filtered.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(filtered, key = { it.id }) { dish ->
            val inCartQty = state.ui.cart.firstOrNull { it.dishId == dish.id }?.qty ?: 0
            DishMenuCard(
                dish = dish,
                inCartQty = inCartQty,
                onClick = { onDishClick(dish) },
                onAdd = { viewModel.addToCart(dish, 1) },
                onDelta = { delta -> viewModel.updateCartQty(dish.id, delta) }
            )
        }
    }
}

@Composable
private fun DishMenuCard(
    dish: MenuItemEntity,
    inCartQty: Int,
    onClick: () -> Unit,
    onAdd: () -> Unit,
    onDelta: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("dish_card_${dish.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DishPhoto(
                imageUrl = dish.imageUrl,
                category = dish.category,
                contentDescription = dish.name,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = dish.comboTag,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "★ ${dish.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SaffronAmber
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dish.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${dish.prepTime} • ${dish.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${dish.price}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (!dish.available) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Sold Out",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    } else if (inCartQty > 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onDelta(-1) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Remove one", modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "$inCartQty",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                IconButton(
                                    onClick = { onDelta(1) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add one", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = onAdd,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerOrdersContent(
    state: FastBiteUiState,
    onCancelClick: (OrderEntity) -> Unit,
    onRateClick: (OrderEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Digital Tokens & Orders",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Show your token code at FastBite Counter #1 when Ready",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (state.orders.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No orders yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Pre-order from the menu to generate your first pickup token.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        items(state.orders, key = { it.id }) { order ->
            val lines = remember(order.itemsJson) { OrderItemCodec.decode(order.itemsJson) }
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    width = if (order.status == "Ready") 2.dp else 1.dp,
                    color = if (order.status == "Ready") FreshEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(order.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                            Text("${order.createdAt} • Pickup ${order.pickupTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OrderStatusPill(status = order.status)
                    }

                    Surface(
                        color = if (order.status == "Ready") FreshEmeraldContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = if (order.status == "Ready") OnFreshEmeraldContainer else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = order.tokenCode,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = order.tokenType,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OrderProgressStepper(status = order.status)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        lines.forEach { line ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${line.qty}× ${line.name}", style = MaterialTheme.typography.bodySmall)
                                Text("₹${line.price * line.qty}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total ₹${order.totalAmount} (${order.paymentMethod})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (order.status in listOf("Order Placed", "Preparing")) {
                            OutlinedButton(
                                onClick = { onCancelClick(order) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert)
                            ) {
                                Text("Cancel")
                            }
                        } else if (order.status == "Completed" && !order.rated) {
                            Button(onClick = { onRateClick(order) }) {
                                Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rate Meal")
                            }
                        } else if (order.rated) {
                            Text(
                                text = "Rated ${order.ratingGiven}★",
                                style = MaterialTheme.typography.labelMedium,
                                color = SaffronAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (order.status == "Cancelled" && order.paymentMode == "Online") {
                        Text(
                            text = "Refund: ₹${order.refundAmount} • Cancellation Penalty: ₹${order.penaltyDeducted}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CrimsonAlert
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerWeeklyContent(
    state: FastBiteUiState,
    viewModel: FastBiteViewModel
) {
    val selectedDay = state.ui.selectedCalendarDay
    val planIds = state.weeklyPlans.firstOrNull { it.dayOfWeek == selectedDay }?.dishIds().orEmpty()
    val dayDishes = state.menuItems.filter { it.id in planIds }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Weekly Canteen Planner",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Explore curated chef specials across the week",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DefaultSeedData.daysOfWeek.forEach { day ->
                    FilterChip(
                        selected = selectedDay == day,
                        onClick = { viewModel.setCustomerCalendarDay(day) },
                        label = { Text(day.take(3)) }
                    )
                }
            }
        }

        item {
            Text(
                text = "$selectedDay Specials (${dayDishes.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(dayDishes, key = { "wk_${selectedDay}_${it.id}" }) { dish ->
            DishMenuCard(
                dish = dish,
                inCartQty = state.ui.cart.firstOrNull { it.dishId == dish.id }?.qty ?: 0,
                onClick = { viewModel.addToCart(dish, 1) },
                onAdd = { viewModel.addToCart(dish, 1) },
                onDelta = { delta -> viewModel.updateCartQty(dish.id, delta) }
            )
        }
    }
}

@Composable
private fun CustomerInfoContent(
    state: FastBiteUiState,
    viewModel: FastBiteViewModel,
    onSwitchRole: () -> Unit
) {
    var feedbackType by remember { mutableStateOf("Compliment") }
    var feedbackText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Canteen Info & Feedback",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Order & Ready Chimes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Play synthesized alert chimes on token updates", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = state.settings.soundEnabled,
                            onCheckedChange = { viewModel.toggleSound() }
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Canteen Guidelines & Policy", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("• Present your PERM-FB or TEMP-FB token at Counter #1 when status turns READY.", style = MaterialTheme.typography.bodySmall)
                    Text("• Online order cancellations incur a ${state.settings.penaltyPercent}% prep fee; remaining amount refunds instantly.", style = MaterialTheme.typography.bodySmall)
                    Text("• Cash on Delivery orders can be cancelled with ₹0 fee prior to completion.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(18.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Send Feedback to Kitchen", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Compliment", "Suggestion", "Issue").forEach { t ->
                            FilterChip(
                                selected = feedbackType == t,
                                onClick = { feedbackType = t },
                                label = { Text(t) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        label = { Text("Share your thoughts") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.submitFeedback(feedbackType, feedbackText)
                            feedbackText = ""
                        },
                        enabled = feedbackText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Feedback")
                    }
                }
            }
        }

        items(state.feedbacks, key = { it.id }) { fb ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${fb.userName} • ${fb.type}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(fb.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(fb.message, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onSwitchRole,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Switch Portal")
                }
                TextButton(
                    onClick = { viewModel.signOutCustomer() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign Out")
                }
            }
        }
    }
}
