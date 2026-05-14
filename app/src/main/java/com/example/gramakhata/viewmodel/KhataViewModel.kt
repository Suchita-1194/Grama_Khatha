package com.example.gramakhata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room
import com.example.gramakhata.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class KhataViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        KhataDatabase::class.java,
        "khata_db"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val dao = db.dao()

    val customers = dao.getCustomersSorted()
    val recentTransactions: Flow<List<Transaction>> = dao.getAllRecentTransactions()

    // ---------------- LOGIN STATE ----------------
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // ---------------- PIN STORAGE ----------------
    private val _storedPin = MutableStateFlow("1234") // default PIN
    val storedPin: StateFlow<String> = _storedPin

    // ---------------- LOGIN FUNCTIONS ----------------
    fun loginWithPin(inputPin: String): Boolean {
        return if (inputPin == _storedPin.value) {
            _isLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun loginWithBiometric() {
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun resetPin(newPin: String) {
        _storedPin.value = newPin
    }

    // ---------------- CUSTOMER ----------------
    fun addCustomer(name: String, phoneNumber: String, address: String) {
        viewModelScope.launch {
            dao.insertCustomer(
                Customer(name = name, phoneNumber = phoneNumber, address = address)
            )
        }
    }
    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            dao.updateCustomer(customer)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            dao.deleteCustomer(customer)
        }
    }

    fun addTransaction(customer: Customer, amount: Int, type: String, note: String) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()

            dao.insertTransaction(
                Transaction(
                    customerId = customer.id,
                    amount = amount,
                    type = type,
                    note = note,
                    timestamp = timestamp
                )
            )

            val newDue = if (type == "CREDIT") {
                customer.totalDue + amount
            } else {
                customer.totalDue - amount
            }

            dao.updateDue(customer.id, newDue, timestamp)
        }
    }
    fun deleteTransaction(transaction: Transaction, customer: Customer) {
        viewModelScope.launch {
            dao.deleteTransaction(transaction)

            val updatedDue = if (transaction.type == "CREDIT")
                customer.totalDue - transaction.amount
            else
                customer.totalDue + transaction.amount

            dao.updateDue(customer.id, updatedDue, System.currentTimeMillis())
        }
    }
    fun getTransactionsForCustomer(customerId: Int): Flow<List<Transaction>> {
        return dao.getTransactionsForCustomer(customerId)
    }
    // ADD THESE inside your existing KhataViewModel

    // DARK MODE
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // DAILY REPORT
    fun generateDailyReport(transactions: List<Transaction>): String {
        val totalCredit = transactions.filter { it.type == "CREDIT" }.sumOf { it.amount }
        val totalDebit = transactions.filter { it.type == "DEBIT" }.sumOf { it.amount }

        return """
        Daily Collection Report

        Total Credit: ₹$totalCredit
        Total Debit: ₹$totalDebit
        Net: ₹${totalCredit - totalDebit}
        
    """.trimIndent()
    }

    // SORTED DUE
    fun getSortedCustomers(customers: List<Customer>): List<Customer> {
        return customers.sortedByDescending { it.totalDue }
    }

    // SMS MESSAGE
    fun generateReminderMessage(shop: String, amount: Int): String {
        return "Namaskara, your due at $shop is ₹$amount."
    }
}