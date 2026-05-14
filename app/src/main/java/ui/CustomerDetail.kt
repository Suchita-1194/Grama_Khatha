package com.example.gramakhata.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.gramakhata.data.Customer
import com.example.gramakhata.data.Transaction
import com.example.gramakhata.viewmodel.KhataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Int,
    viewModel: KhataViewModel,
    onNavigateBack: () -> Unit
) {
    val customers = viewModel.customers.collectAsState(initial = emptyList())
    val customer = customers.value.find { it.id == customerId }

    val transactions =
        viewModel.getTransactionsForCustomer(customerId)
            .collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Customer Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    if (customer != null) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, null)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (customer != null) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
    ) { padding ->

        if (customer == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Customer not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {

                // CUSTOMER INFO
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Phone: ${customer.phoneNumber}")
                        Text("Address: ${customer.address}")
                        Spacer(Modifier.height(8.dp))
                        Text("Total Due: ₹${customer.totalDue}")
                    }
                }

                // EMPTY STATE
                if (transactions.value.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions yet")
                    }
                } else {
                    LazyColumn {
                        items(transactions.value) { txn ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("₹${txn.amount} (${txn.type})")
                                    if (txn.note.isNotBlank()) {
                                        Text(txn.note)
                                    }
                                }

                                IconButton(
                                    onClick = { transactionToDelete = txn }
                                ) {
                                    Icon(Icons.Default.Delete, null)
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }

    // ---------------- ADD TRANSACTION ----------------
    if (showAddDialog && customer != null) {

        var amount by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("CREDIT") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Transaction") },
            text = {
                Column {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") }
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ✅ PERFECTLY ALIGNED CREDIT / DEBIT
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = type == "CREDIT",
                                onClick = { type = "CREDIT" }
                            )
                            Text("Credit", modifier = Modifier.padding(start = 4.dp))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = type == "DEBIT",
                                onClick = { type = "DEBIT" }
                            )
                            Text("Debit", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amt = amount.toIntOrNull()
                    if (amt != null && amt > 0) {
                        viewModel.addTransaction(customer, amt, type, note)
                        showAddDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ---------------- EDIT CUSTOMER ----------------
    if (showEditDialog && customer != null) {

        var name by remember { mutableStateOf(customer.name) }
        var phone by remember { mutableStateOf(customer.phoneNumber) }
        var address by remember { mutableStateOf(customer.address) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Customer") },
            text = {
                Column {
                    OutlinedTextField(name, { name = it }, label = { Text("Name") })
                    OutlinedTextField(phone, { phone = it }, label = { Text("Phone") })
                    OutlinedTextField(address, { address = it }, label = { Text("Address") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank() && phone.length >= 10) {
                        viewModel.updateCustomer(
                            customer.copy(
                                name = name,
                                phoneNumber = phone,
                                address = address
                            )
                        )
                        showEditDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ---------------- DELETE CUSTOMER ----------------
    if (showDeleteDialog && customer != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Customer") },
            text = { Text("Delete ${customer.name}?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteCustomer(customer)
                    showDeleteDialog = false
                    onNavigateBack()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ---------------- DELETE TRANSACTION ----------------
    if (transactionToDelete != null && customer != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction") },
            text = { Text("Delete ₹${transactionToDelete!!.amount}?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteTransaction(transactionToDelete!!, customer)
                    transactionToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}