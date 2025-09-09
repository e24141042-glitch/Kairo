
package com.example.quicktasks.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.quicktasks.model.Task
import kotlin.math.roundToInt

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextDecoration

/**
 * Swipeable task card with swipe gestures for completion and deletion
 */
@Composable
fun TaskPriorityIndicator(priority: com.example.quicktasks.model.TaskPriority) {
    Text(text = "Priority: ${priority.name}")
}

@Preview
@Composable
fun TaskCard(
    task: com.example.quicktasks.model.Task = com.example.quicktasks.model.Task(
        id = 1,
        title = "Test Task",
        description = "Test Description",
        dueDate = java.time.LocalDateTime.now(),
        priority = com.example.quicktasks.model.TaskPriority.HIGH,
        category = "Work",
        completedStatus = false
    ),
    onClick: () -> Unit = { },
    onToggleComplete: () -> Unit = { },
    onDelete: () -> Unit = { },
    modifier: Modifier = Modifier,
    isOverdue: Boolean = false,
    isCompleted: Boolean = false
) {
    Card(modifier = modifier) {
        Column {
            Text(text = task.title)
            Text(text = task.description)
        }
    }
}

@Composable
fun SwipeableTaskCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    val swipeThreshold = 100f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        when {
                            offsetX > swipeThreshold -> {
                                // Swipe right - complete task
                                onToggleComplete()
                                offsetX = 0f
                            }

                            offsetX < -swipeThreshold -> {
                                // Swipe left - delete task
                                onDelete()
                                offsetX = 0f
                            }

                            else -> {
                                // Reset position
                                offsetX = 0f
                            }
                        }
                    }
                ) { _, dragAmount ->
                    offsetX = (offsetX + dragAmount.x).coerceIn(-200f, 200f)
                }
            }
    ) {
        // Background actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.CenterStart)
        ) {
            // Complete action (right side)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        Color.Green.copy(alpha = 0.8f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Delete action (left side)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        Color.Red.copy(alpha = 0.8f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Task card
        TaskCard(
            task = task,
            onClick = onClick,
            onToggleComplete = onToggleComplete,
            onDelete = onDelete,
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .animateContentSize()
        )
    }
}

/**
 * Swipeable task list item with haptic feedback
 */
@Composable
fun SwipeableTaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSwipeInProgress by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }

    val swipeThreshold = 120f
    val maxSwipeDistance = 200f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        // Background actions
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Delete action (left)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
                    .background(
                        Color.Red.copy(alpha = 0.9f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Complete action (right)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
                    .background(
                        Color.Green.copy(alpha = 0.9f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Task content
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isSwipeInProgress = true },
                        onDragEnd = {
                            isSwipeInProgress = false
                            when {
                                offsetX > swipeThreshold -> {
                                    onToggleComplete()
                                }

                                offsetX < -swipeThreshold -> {
                                    onDelete()
                                }
                            }
                            offsetX = 0f
                        }
                    ) { _, dragAmount ->
                        offsetX = (offsetX + dragAmount.x).coerceIn(-maxSwipeDistance, maxSwipeDistance)
                    }
                },
            onClick = onClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.completedStatus,
                    onCheckedChange = { onToggleComplete() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                    }
                }

                TaskPriorityIndicator(priority = task.priority)
            }
        }
    }
}
