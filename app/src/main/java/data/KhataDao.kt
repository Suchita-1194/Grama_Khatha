package com.example.gramakhata.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KhataDao {

    @Insert
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM Customer ORDER BY lastUpdated DESC")
    fun getCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM `Transaction` WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: Int): Flow<List<Transaction>>

    @Query("UPDATE Customer SET totalDue = :due, lastUpdated = :time WHERE id = :customerId")
    suspend fun updateDue(customerId: Int, due: Int, time: Long)

    @Query("SELECT * FROM `Transaction` ORDER BY timestamp DESC")
    fun getAllRecentTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM customer ORDER BY totalDue DESC")
    fun getCustomersSorted(): Flow<List<Customer>>
}
