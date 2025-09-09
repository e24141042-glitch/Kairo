package com.example.quicktasks.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quicktasks.model.TaskPriority
import com.example.quicktasks.model.RecurringType
import com.example.quicktasks.viewmodel.AddTaskViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Add/Edit Task screen with form fields for all task properties
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    taskId: Long? = null,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val taskSaved by viewModel.taskSaved.collectAsState()
    
    // Debug logging
    LaunchedEffect(Unit) {
        println("AddTaskScreen: Screen loaded")
    }
    
    // Load task for editing if taskId is provided
    LaunchedEffect(taskId) {
        taskId?.let { viewModel.loadTaskForEditing(it) }
    }
    
    // Navigate back after successful save
    LaunchedEffect(taskSaved) {
        if (taskSaved) {
            println("AddTaskScreen: Task saved, navigating back")
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Task" else "Add Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveTask() },
                        enabled = uiState.title.isNotBlank()
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Task Title *") },
                placeholder = { Text("Enter task title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.title.isBlank() && uiState.errorMessage != null
            )
            
            // Description field
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                placeholder = { Text("Enter task description (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4
            )
            
            // Due date picker
            DueDatePicker(
                dueDate = uiState.dueDate,
                onDateChange = viewModel::updateDueDate
            )
            
            // Priority selector
            PrioritySelector(
                selectedPriority = uiState.priority,
                onPriorityChange = viewModel::updatePriority
            )
            
            // Category field
            CategorySelector(
                category = uiState.category,
                onCategoryChange = viewModel::updateCategory,
                availableCategories = uiState.availableCategories
            )
            
            // Reminder time picker
            ReminderTimePicker(
                reminderTime = uiState.reminderTime,
                onTimeChange = viewModel::updateReminderTime
            )
            
            // Recurring options
            RecurringOptions(
                isRecurring = uiState.isRecurring,
                onRecurringChange = viewModel::updateRecurring,
                recurringType = uiState.recurringType,
                onRecurringTypeChange = viewModel::updateRecurringType
            )
            
            // Error message
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DueDatePicker(
    dueDate: LocalDateTime?,
    onDateChange: (LocalDateTime?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    OutlinedTextField(
        value = dueDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "",
        onValueChange = { },
        label = { Text("Due Date") },
        placeholder = { Text("Select due date (optional)") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            Row {
                if (dueDate != null) {
                    IconButton(onClick = { onDateChange(null) }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear date")
                    }
                }
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                }
            }
        }
    )
    
    // Enhanced date picker with multiple options
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Select Due Date") },
            text = { 
                Column {
                    Text("Choose when this task is due:")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Today
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onDateChange(LocalDateTime.now().withHour(23).withMinute(59))
                                showDatePicker = false 
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Today (11:59 PM)")
                    }
                    
                    // Tomorrow
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onDateChange(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0))
                                showDatePicker = false 
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomorrow (6:00 PM)")
                    }
                    
                    // This Weekend
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                val now = LocalDateTime.now()
                                val daysUntilSaturday = (6 - now.dayOfWeek.value) % 7
                                val saturday = if (daysUntilSaturday == 0) now.plusDays(7) else now.plusDays(daysUntilSaturday.toLong())
                                onDateChange(saturday.withHour(12).withMinute(0))
                                showDatePicker = false 
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Weekend, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("This Weekend")
                    }
                    
                    // Next Week
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onDateChange(LocalDateTime.now().plusDays(7).withHour(9).withMinute(0))
                                showDatePicker = false 
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Next Week (Monday 9:00 AM)")
                    }
                    
                    // Next Month
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onDateChange(LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(9).withMinute(0))
                                showDatePicker = false 
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Next Month (1st)")
                    }
                    
                    // No due date option
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onDateChange(null)
                                showDatePicker = false 
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("No Due Date")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrioritySelector(
    selectedPriority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedPriority.displayName,
            onValueChange = { },
            label = { Text("Priority") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TaskPriority.values().forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority.displayName) },
                    onClick = {
                        onPriorityChange(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelector(
    category: String,
    onCategoryChange: (String) -> Unit,
    availableCategories: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = category,
            onValueChange = onCategoryChange,
            label = { Text("Category") },
            placeholder = { Text("Enter or select category") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableCategories.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat) },
                    onClick = {
                        onCategoryChange(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ReminderTimePicker(
    reminderTime: LocalDateTime?,
    onTimeChange: (LocalDateTime?) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    
    OutlinedTextField(
        value = reminderTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
        onValueChange = { },
        label = { Text("Reminder Time") },
        placeholder = { Text("Set reminder time (optional)") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            Row {
                if (reminderTime != null) {
                    IconButton(onClick = { onTimeChange(null) }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear time")
                    }
                }
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Select time")
                }
            }
        }
    )
    
    // Enhanced time picker with multiple options
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Reminder Time") },
            text = { 
                Column {
                    Text("Choose when to be reminded:")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Morning options
                    Text("Morning", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(LocalDateTime.now().withHour(7).withMinute(0))
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("7:00 AM - Early Morning")
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(LocalDateTime.now().withHour(9).withMinute(0))
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("9:00 AM - Morning")
                    }
                    
                    // Afternoon options
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Afternoon", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(LocalDateTime.now().withHour(12).withMinute(0))
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("12:00 PM - Noon")
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(LocalDateTime.now().withHour(15).withMinute(0))
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("3:00 PM - Afternoon")
                    }
                    
                    // Evening options
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Evening", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(LocalDateTime.now().withHour(18).withMinute(0))
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WbTwilight, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("6:00 PM - Evening")
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(LocalDateTime.now().withHour(20).withMinute(0))
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WbTwilight, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("8:00 PM - Night")
                    }
                    
                    // Custom times
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Custom Times", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(LocalDateTime.now().withHour(10).withMinute(30))
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("10:30 AM")
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(LocalDateTime.now().withHour(14).withMinute(30))
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("2:30 PM")
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(LocalDateTime.now().withHour(19).withMinute(30))
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("7:30 PM")
                    }
                    
                    // No reminder option
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Other", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onTimeChange(null)
                                showTimePicker = false 
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsOff, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("No Reminder")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurringOptions(
    isRecurring: Boolean,
    onRecurringChange: (Boolean) -> Unit,
    recurringType: RecurringType?,
    onRecurringTypeChange: (RecurringType?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isRecurring,
                onCheckedChange = onRecurringChange
            )
            Text(
                text = "Recurring Task",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        if (isRecurring) {
            var expanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = recurringType?.displayName ?: "",
                    onValueChange = { },
                    label = { Text("Repeat") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    RecurringType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                onRecurringTypeChange(type)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
