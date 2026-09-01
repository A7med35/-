package com.example.expensesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensesapp.data.AppDatabase
import com.example.expensesapp.data.TransactionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).transactionDao()

    val transactions: StateFlow<List<TransactionEntity>> = dao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalCustody: StateFlow<Double> = dao.getTotalCustody()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalExpenses: StateFlow<Double> = dao.getTotalExpenses()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val netBalance: StateFlow<Double> = combine(totalCustody, totalExpenses) { custody, expenses ->
        custody - expenses
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun addTransaction(title: String, amount: Double, type: String) {
        viewModelScope.launch {
            if (amount >= 0) {
                dao.insertTransaction(TransactionEntity(title = title, amount = amount, type = type))
            }
        }
    }
}
