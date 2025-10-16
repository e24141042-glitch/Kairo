package com.kairo.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kairo.app.data.Task
import com.kairo.app.viewmodel.AddEditTaskViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: Long = 0L,
    onNavigateBack: () -> Unit,
    viewModel: AddEditTaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val isEditing = taskId > 0L
    
    LaunchedEffect(taskId) {
        if (isEditing) {
            viewModel.loadTask(taskId)
        }
    }
    
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
    
    val currentTask = uiState.task ?: Task(
        title = "",
        description = "",
        priority = Task.Priority.MEDIUM,
        category = "General",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Task" else "Add Task",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveTask() }
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Title
            OutlinedTextField(
                value = currentTask.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Task Title") },
                placeholder = { Text("What needs to be done?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                isError = uiState.errorMessage != null && currentTask.title.isBlank()
            )
            
            // Task Description
            OutlinedTextField(
                value = currentTask.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                placeholder = { Text("Add details...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
            
            // Priority Selection
            PrioritySelector(
                selectedPriority = currentTask.priority,
                onPrioritySelected = viewModel::updatePriority
            )
            
            // Category Input
            CategoryInput(
                category = currentTask.category,
                onCategoryChange = viewModel::updateCategory
            )
            
            // Due Date Selection
            DueDateSelector(
                dueDate = currentTask.dueDate,
                onDueDateChange = viewModel::updateDueDate
            )
            
            // Error Message
            uiState.errorMessage?.let { errorMessage ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrioritySelector(
    selectedPriority: Task.Priority,
    onPrioritySelected: (Task.Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PriorityHigh,
                contentDescription = "Priority",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Priority",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Task.Priority.values().forEach { priority ->
                FilterChip(
                    onClick = { onPrioritySelected(priority) },
                    label = { Text(priority.displayName) },
                    selected = selectedPriority == priority,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CategoryInput(
    category: String,
    onCategoryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = "Category",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = category,
            onValueChange = onCategoryChange,
            label = { Text("Category") },
            placeholder = { Text("e.g., Work, Personal, Shopping") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueDateSelector(
    dueDate: LocalDateTime?,
    onDueDateChange: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
    }
    val initialDateTime = remember(dueDate) {
        (dueDate ?: LocalDateTime.now()).withSecond(0).withNano(0)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Due Date",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Due Date",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dueDate?.format(formatter) ?: "",
            onValueChange = { },
            label = { Text("Due Date") },
            placeholder = { Text("Optional") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            readOnly = true,
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (dueDate != null) {
                        IconButton(onClick = { onDueDateChange(null) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Due Date"
                            )
                        }
                    }
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Select Date"
                        )
                    }
                }
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                pendingDate = null
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            pendingDate = selectedDate
                            showDatePicker = false
                            showTimePicker = true
                        } else {
                            showDatePicker = false
                            pendingDate = null
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    pendingDate = null
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val targetDate = pendingDate ?: initialDateTime.toLocalDate()
        val timePickerState = rememberTimePickerState(
            initialHour = initialDateTime.hour,
            initialMinute = initialDateTime.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
                pendingDate = null
            },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateTime = LocalDateTime.of(
                        targetDate,
                        LocalTime.of(timePickerState.hour, timePickerState.minute)
                    ).withSecond(0).withNano(0)
                    onDueDateChange(selectedDateTime)
                    showTimePicker = false
                    pendingDate = null
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    pendingDate = null
                }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            },
            title = {
                Text("Select time")
            }
        )
    }
}
