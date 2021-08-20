package com.masterbit.composetodo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.masterbit.composetodo.ui.theme.Gray800
import com.masterbit.composetodo.viewmodel.AddEditTodoViewModel

@Composable
fun AddEditScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    taskId: Long,
    viewModel: AddEditTodoViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val title = remember(taskId) { if (taskId == -1L) "Add Task" else "Edit Task"}
    val scope = rememberCoroutineScope()

    var task by viewModel.task
    LaunchedEffect(key1 = taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier,
        topBar = {
            InsetAwareTopBar(
                title = title,
                navigationIcon = Icons.Filled.ArrowBack,
                openDrawer = { onBack() },
                action = {  }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                modifier = Modifier.navigationBarsPadding(bottom = true),
                backgroundColor = if (task.title.isNotEmpty()) MaterialTheme.colors.secondary else Color.LightGray,
                contentColor = MaterialTheme.colors.onSecondary
            ) {
                IconButton(onClick = { viewModel.saveTask(); onBack() }, enabled = task.title.isNotEmpty()) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colors.onSecondary.copy(alpha = if (task.title.isNotEmpty()) 1f else 0.12f))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
        ) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = task.title,
                onValueChange = {
                    viewModel.initialLoad.value = false
                    task = task.copy(title = it)
                },
                isError = if (viewModel.initialLoad.value) false else task.title.isNullOrEmpty(),
                label = {
                    Text("Title")
                },
                singleLine = true,
                maxLines = 1,
                trailingIcon = {
                    IconButton(onClick = { task = task.copy(title = "") }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                    }
                },
                textStyle = MaterialTheme.typography.body1.copy(color = Gray800),
                colors = TextFieldDefaults.outlinedTextFieldColors(unfocusedLabelColor = Color.LightGray)
            )

            if (!viewModel.initialLoad.value && task.title.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Title should not be empty", color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                value = task.description ?: "",
                onValueChange = {
                    viewModel.initialLoad.value = false
                    task = task.copy(description = it)
                },
                label = {
                    Text("Description")
                },
                singleLine = false,
                maxLines = 10,
                trailingIcon = {
                    IconButton(onClick = { task = task.copy(description = "") }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                    }
                },
                textStyle = MaterialTheme.typography.body1.copy(color = Gray800),
                colors = TextFieldDefaults.outlinedTextFieldColors(unfocusedLabelColor = Color.LightGray)
            )
        }

    }

}