package com.example.expensesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensesapp.data.TransactionEntity
import com.example.expensesapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val totalCustody by viewModel.totalCustody.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val netBalance by viewModel.netBalance.collectAsState()

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("OUT") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("إدارة المصاريف والعهدة") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "الرصيد المتبقي: $netBalance ر.س", 
                        fontSize = 20.sp, 
                        color = if (netBalance >= 0) Color(0xFF2E7D32) else Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("إجمالي العهدة: +$totalCustody", color = Color(0xFF1565C0))
                        Text("إجمالي المصاريف: -$totalExpenses", color = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("بيان المعاملة") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("المبلغ (0 أو أكثر)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { type = "IN" },
                    colors = ButtonDefaults.buttonColors(containerColor = if (type == "IN") Color(0xFF1565C0) else Color.Gray)
                ) {
                    Text("إضافة عهدة")
                }
                Button(
                    onClick = { type = "OUT" },
                    colors = ButtonDefaults.buttonColors(containerColor = if (type == "OUT") Color.Red else Color.Gray)
                ) {
                    Text("إضافة مصروف")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (title.isNotBlank() && amt != null && amt >= 0) {
                        viewModel.addTransaction(title, amt, type)
                        title = ""
                        amount = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("حفظ المعاملة")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("السجل:", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(transactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(transaction.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = transaction.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = dateString, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(
                text = if (transaction.type == "IN") "+${transaction.amount}" else "-${transaction.amount}",
                color = if (transaction.type == "IN") Color(0xFF1565C0) else Color.Red,
                fontSize = 16.sp
            )
        }
    }
}
