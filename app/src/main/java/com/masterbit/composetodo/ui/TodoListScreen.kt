package com.masterbit.composetodo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.masterbit.composetodo.common.TaskType
import com.masterbit.composetodo.common.UiState
import com.masterbit.composetodo.common.produceUiStateWithFlow
import com.masterbit.composetodo.viewmodel.TodoListViewModel
import com.masterbit.composetodo.db.Task


@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel = hiltViewModel(),
    navigateToDetailView: (taskId: Long) -> Unit,
    navigateToAddEdit: () -> Unit,
    openDrawer: () -> Unit
) {

    val (tasks, refreshCallback) = produceUiStateWithFlow(producer = viewModel) {
        refresh()
    }

    TodoListScreen(
        tasks = tasks.value,
        refreshCallback,
        navigateToDetailView = navigateToDetailView,
        navigateToAddEdit = navigateToAddEdit,
        openDrawer = openDrawer)

}

@Composable
fun TodoListScreen(
    tasks: UiState<List<Task>>,
    refresh: () -> Unit,
    navigateToDetailView: (taskId: Long) -> Unit,
    navigateToAddEdit: () -> Unit,
    openDrawer: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    if (tasks.hasError) {
        val errorMsg = LocalContext.current.getString(tasks.error!!)
        LaunchedEffect(key1 = scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar(errorMsg)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            InsetAwareTopBar(
                title = "Todo",
                navigationIcon = Icons.Filled.Menu,
                openDrawer = openDrawer,
                action = { TodoListMenu(refresh) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(modifier = Modifier.navigationBarsPadding(bottom = true), onClick = { navigateToAddEdit() }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colors.onSurface)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        val modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        LoadingContent(
            initialLoad = tasks.initialLoad,
            loadingScreen = { FullScreenLoading() },
            loading = tasks.loading,
            refresh = refresh,
            content = {
                TodoListErrorAndContent(
                    tasks,
                    modifier = modifier,
                    navigateToDetailView
                )
            }
        )
    }
}

@Composable
fun TodoListErrorAndContent(
    tasks: UiState<List<Task>>,
    modifier: Modifier = Modifier,
    navigateToDetailView: (taskId: Long) -> Unit
) {
    if (tasks.data?.isNotEmpty() == true) {
        ShowTasks(modifier, tasks.data, navigateToDetailView)
    } else if (tasks.data.isNullOrEmpty()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)) {
            Text( text = "No tasks found", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun ShowTasks(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    navigateToDetailView: (taskId: Long) -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    Surface(modifier = modifier.padding(top = 12.dp, bottom = 12.dp), color = MaterialTheme.colors.surface) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(modifier = Modifier.padding(start = 12.dp, top = 10.dp), text = LocalContext.current.getString(viewModel.filterTypeState.value), style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.onSurface.copy(alpha = .6f))
            Spacer(modifier = Modifier.height(18.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars, applyTop = false)
            ) {
                items(tasks) { task ->
                    CompositionLocalProvider(LocalContentAlpha provides 0.8f) {
                        TaskRowItem(
                            task = task,
                            navigateToDetailView = navigateToDetailView
                        )
                    }
                }
            }
        }
    }


}

@Composable
fun TaskRowItem(
    viewModel: TodoListViewModel = viewModel(),
    task: Task,
    navigateToDetailView: (taskId: Long) -> Unit
) {
    Surface(modifier = Modifier, shape = MaterialTheme.shapes.small) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { navigateToDetailView(task.id!!) }
            .padding(10.dp)
            , horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.completed, onCheckedChange = {checked -> viewModel.completeTask(task.copy(completed = checked))})
            Spacer(modifier = Modifier.size(14.dp))
            Text(text = task.title, style = MaterialTheme.typography.body1, textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingContent(
    initialLoad: Boolean,
    loadingScreen: @Composable () -> Unit = {},
    loading: Boolean,
    refresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (initialLoad) {
        loadingScreen()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = loading),
            onRefresh = { refresh() }) {
            content()
        }
    }
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun InsetAwareTopBar(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: ImageVector,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor = backgroundColor),
    openDrawer: () -> Unit,
    action: @Composable RowScope.() -> Unit,
    elevation: Dp = 4.dp
    ) {
    Surface(modifier = modifier, color = backgroundColor, elevation = elevation) {
        TopAppBar(
            title = { Text(title, style = MaterialTheme.typography.h6, color = contentColor) },
            navigationIcon = {
                IconButton(onClick = { openDrawer() }) {
                    Icon(imageVector = navigationIcon, contentDescription = null)
                }
            },
            actions = { action() },
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false)
        )
    }
}

@Composable
fun RowScope.TodoListMenu(
    refresh: () -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    this.apply {
        var showFilters by remember { mutableStateOf(false)}
        IconButton(onClick = { showFilters = !showFilters }) {
            Icon(imageVector = Icons.Filled.FilterList, contentDescription = null, tint = Color.White)
        }

        DropdownMenu(expanded = showFilters, onDismissRequest = { showFilters = false }) {
            TextButton(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.setFilterType(TaskType.ALL_TASKS); showFilters = false; refresh() }) {
                Text(text = "All Tasks", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
            }

            TextButton(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.setFilterType(TaskType.COMPLETED_TASKS); showFilters = false; refresh() }) {
                Text(text = "Completed tasks", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
            }

            TextButton(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.setFilterType(TaskType.ACTIVE_TASKS); showFilters = false; refresh() }) {
                Text(text = "Active tasks", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
            }
        }

        var showOverflowMenu by remember { mutableStateOf(false)}

        IconButton(onClick = { showOverflowMenu = !showOverflowMenu }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null, tint = Color.White)
        }

        DropdownMenu(expanded = showOverflowMenu, onDismissRequest = { showOverflowMenu = false }) {
            TextButton(modifier = Modifier.fillMaxWidth(), onClick = { refresh(); showOverflowMenu = false }) {
                Text(text = "Refresh", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
            }

            TextButton(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.deleteCompletedTasks(); showOverflowMenu = false }) {
                Text(text = "Clear completed", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}
