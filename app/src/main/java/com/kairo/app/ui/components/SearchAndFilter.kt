package com.kairo.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kairo.app.data.Priority
import com.kairo.app.data.Task
import com.kairo.app.viewmodel.CompletionFilter
import com.kairo.app.viewmodel.FilterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterState: FilterState,
    onFilterStateChange: (FilterState) -> Unit,
    availableCategories: List<String>,
    modifier: Modifier = Modifier
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search tasks...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { showFilterDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = if (hasActiveFilters(filterState)) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        
        // Active filters display
        if (hasActiveFilters(filterState)) {
            Spacer(modifier = Modifier.height(8.dp))
            ActiveFiltersDisplay(
                filterState = filterState,
                onFilterStateChange = onFilterStateChange
            )
        }
    }
    
    // Filter dialog
    if (showFilterDialog) {
        FilterDialog(
            filterState = filterState,
            onFilterStateChange = onFilterStateChange,
            availableCategories = availableCategories,
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
private fun ActiveFiltersDisplay(
    filterState: FilterState,
    onFilterStateChange: (FilterState) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Category filter
        if (filterState.selectedCategory != "All") {
            FilterChip(
                onClick = {
                    onFilterStateChange(filterState.copy(selectedCategory = "All"))
                },
                label = { Text(filterState.selectedCategory) },
                selected = true
            )
        }
        
        // Priority filter
        if (filterState.selectedPriority != null) {
            FilterChip(
                onClick = {
                    onFilterStateChange(filterState.copy(selectedPriority = null))
                },
                label = { Text(filterState.selectedPriority.displayName) },
                selected = true
            )
        }
        
        // Completion filter
        if (filterState.completionFilter != CompletionFilter.ALL) {
            FilterChip(
                onClick = {
                    onFilterStateChange(filterState.copy(completionFilter = CompletionFilter.ALL))
                },
                label = { Text(filterState.completionFilter.name.lowercase().replaceFirstChar { it.uppercaseChar() }) },
                selected = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDialog(
    filterState: FilterState,
    onFilterStateChange: (FilterState) -> Unit,
    availableCategories: List<String>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Tasks") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category filter
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                
                var expandedCategory by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = filterState.selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        (listOf("All") + availableCategories).forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    onFilterStateChange(filterState.copy(selectedCategory = category))
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }
                
                // Priority filter
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                
                var expandedPriority by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedPriority,
                    onExpandedChange = { expandedPriority = !expandedPriority }
                ) {
                    OutlinedTextField(
                        value = filterState.selectedPriority?.displayName ?: "All",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Priority") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPriority,
                        onDismissRequest = { expandedPriority = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = {
                                onFilterStateChange(filterState.copy(selectedPriority = null))
                                expandedPriority = false
                            }
                        )
                        Priority.values().forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority.displayName) },
                                onClick = {
                                    onFilterStateChange(filterState.copy(selectedPriority = priority))
                                    expandedPriority = false
                                }
                            )
                        }
                    }
                }
                
                // Completion filter
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                
                var expandedCompletion by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCompletion,
                    onExpandedChange = { expandedCompletion = !expandedCompletion }
                ) {
                    OutlinedTextField(
                        value = filterState.completionFilter.name.lowercase().replaceFirstChar { it.uppercaseChar() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCompletion) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCompletion,
                        onDismissRequest = { expandedCompletion = false }
                    ) {
                        CompletionFilter.values().forEach { completion ->
                            DropdownMenuItem(
                                text = { Text(completion.name.lowercase().replaceFirstChar { it.uppercaseChar() }) },
                                onClick = {
                                    onFilterStateChange(filterState.copy(completionFilter = completion))
                                    expandedCompletion = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onFilterStateChange(FilterState()) // Reset all filters
                }
            ) {
                Text("Clear All")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

private fun hasActiveFilters(filterState: FilterState): Boolean {
    return filterState.selectedCategory != "All" ||
            filterState.selectedPriority != null ||
            filterState.completionFilter != CompletionFilter.ALL
}

