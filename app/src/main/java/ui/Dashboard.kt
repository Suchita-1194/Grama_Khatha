package com.example.gramakhata.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.gramakhata.data.Customer
import com.example.gramakhata.data.Transaction
import com.example.gramakhata.viewmodel.KhataViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: KhataViewModel,
    onNavigateToCustomerDetail: (Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val customers by viewModel.customers.collectAsState(initial = emptyList())
    val recentTransactions by viewModel.recentTransactions.collectAsState(initial = emptyList())

    val context = LocalContext.current
    var showAddCustomerDialog by remember { mutableStateOf(false) }

    val totalDue = remember(customers) {
        customers.sumOf { it.totalDue }
    }

    //  SORTED CUSTOMERS
    val sortedCustomers = remember(customers) {
        viewModel.getSortedCustomers(customers)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grama Khata", fontWeight = FontWeight.Bold) },
                actions = {

                    // DAILY REPORT
                    IconButton(onClick = {
                        val report = viewModel.generateDailyReport(recentTransactions)

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, report)
                        }

                        context.startActivity(Intent.createChooser(intent, "Share Report"))
                    }) {
                        Icon(Icons.Default.Description, contentDescription = "Report")
                    }

                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }

                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                SummaryCard(totalDue)
            }

            item {
                Text(
                    "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionItem(
                        icon = Icons.Default.PersonAdd,
                        label = "Add Customer",
                        modifier = Modifier.weight(1f),
                        onClick = { showAddCustomerDialog = true }
                    )

                    QuickActionItem(
                        icon = Icons.Default.Info,
                        label = "About",
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                }
            }

            item {
                Text(
                    "Recent Customers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // SORTED LIST
            items(sortedCustomers.take(5)) { customer ->
                CustomerDashboardItem(
                    customer = customer,
                    viewModel = viewModel,
                    onClick = { onNavigateToCustomerDetail(customer.id) }
                )
            }

            item {
                Text(
                    "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(recentTransactions.take(5)) { transaction ->
                TransactionDashboardItem(transaction)
            }
        }
    }

    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, phone, address ->
                viewModel.addCustomer(name, phone, address)
                showAddCustomerDialog = false
            }
        )
    }
}

@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(name, phone, address)
                    }
                }
            ) {
                Text("Add")
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
fun SummaryCard(totalDue: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start //  FIX
        ) {

            Text(
                text = "Total Receivables",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "₹ $totalDue",
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
@Composable
fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label)
            Spacer(Modifier.height(8.dp))
            Text(label)
        }
    }
}

@Composable
fun CustomerDashboardItem(
    customer: Customer,
    viewModel: KhataViewModel,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(Modifier.weight(1f)) {
                Text(customer.name, fontWeight = FontWeight.Bold)
                Text(customer.phoneNumber)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    "₹ ${customer.totalDue}",
                    fontWeight = FontWeight.Bold,
                    color = if (customer.totalDue > 0)
                        Color(0xFFD32F2F)
                    else
                        Color(0xFF388E3C)
                )

                Spacer(Modifier.width(8.dp))

                // REMINDER BUTTON
                IconButton(onClick = {
                    val message = viewModel.generateReminderMessage(
                        "Grama Khata",
                        customer.totalDue
                    )

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }

                    context.startActivity(Intent.createChooser(intent, "Send via"))
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun TransactionDashboardItem(transaction: Transaction) {
    val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        .format(Date(transaction.timestamp))

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(if (transaction.type == "CREDIT") "Credit" else "Debit")
            Text(date, style = MaterialTheme.typography.bodySmall)
        }

        Text(
            "${if (transaction.type == "CREDIT") "+" else "-"} ₹${transaction.amount}",
            fontWeight = FontWeight.Bold,
            color = if (transaction.type == "CREDIT")
                Color(0xFFD32F2F)
            else
                Color(0xFF388E3C)
        )
    }
}
