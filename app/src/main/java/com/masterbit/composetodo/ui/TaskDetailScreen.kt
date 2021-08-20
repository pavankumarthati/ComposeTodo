package com.masterbit.composetodo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.masterbit.composetodo.viewmodel.TaskDetailViewModel

@Composable
fun TaskDetailScreen(
    taskId: Long,
    navigateToAddEditScreen: (taskId: Long) -> Unit,
    onBack: () -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()

) {

    val task by viewModel.task
    val backCallback by rememberUpdatedState(newValue = onBack)

    LaunchedEffect(key1 = taskId) {
        viewModel.loadTask(taskId)
    }


    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            InsetAwareTopBar(
                title = "Task Detail",
                navigationIcon = Icons.Filled.ArrowBack,
                openDrawer = { backCallback() },
                action = {
                    TaskDetailActionMenu {
                        viewModel.deleteTask()
                        backCallback()
                    }
                })
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(modifier = Modifier.navigationBarsPadding(bottom = true), onClick = { task?.id?.let { navigateToAddEditScreen(it) } }) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
            }
        }
    ) {

        Surface {
            if (task == null) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)) {
                    Text(text = "No task found", style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
                }
            } else {
                Row {
                    Checkbox(modifier = Modifier.defaultMinSize(48.dp, 48.dp).padding(start = 10.dp, top = 12.dp, bottom = 10.dp), checked = task!!.completed, onCheckedChange = { checked -> viewModel.completeTask(checked)})
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.padding(top = 14.dp), horizontalAlignment = Alignment.Start) {
                        Text(modifier = Modifier
                            .fillMaxWidth()
                            , text = task!!.title, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f), style = MaterialTheme.typography.subtitle1, textDecoration = if (task!!.completed) TextDecoration.LineThrough else TextDecoration.None)
                        if (!task!!.description.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = task!!.description ?: "",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                textDecoration = if (task!!.completed) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }   
                }
            }
        }

    }
}

@Composable
fun RowScope.TaskDetailActionMenu(
    onMenuItemClick: () -> Unit,

) {
    IconButton(onClick = { onMenuItemClick() }) {
        Icon(imageVector = Icons.Filled.DeleteForever, contentDescription = null)
    }
}