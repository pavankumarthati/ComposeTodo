package com.masterbit.composetodo

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.masterbit.composetodo.common.Destinations
import com.masterbit.composetodo.common.Destinations.ADD_EDIT_TODO
import com.masterbit.composetodo.common.Destinations.DETAIL_VIEW
import com.masterbit.composetodo.common.Destinations.TODO_LIST
import com.masterbit.composetodo.common.MainActions
import com.masterbit.composetodo.ui.AddEditScreen
import com.masterbit.composetodo.ui.TaskDetailScreen
import com.masterbit.composetodo.ui.TodoListScreen
import com.masterbit.composetodo.ui.theme.ComposeTodoTheme
import com.masterbit.composetodo.viewmodel.TodoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    val todoListViewModel by viewModels<TodoListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ComposeTodoTheme {
                ProvideWindowInsets {
                    val systemUiController = rememberSystemUiController()
                    SideEffect {
                        systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
                    }
                    val navController: NavHostController = rememberNavController()
                    val scope = rememberCoroutineScope()
                    val backstackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = backstackEntry?.destination?.route ?: TODO_LIST
                    val scaffoldState = rememberScaffoldState()

                    Scaffold(
                        scaffoldState = scaffoldState,
                        modifier = Modifier.fillMaxSize(),
                        drawerContent = {
                            AppDrawer(
                                Modifier.fillMaxSize(),
                                currentRoute,
                                navigateToTodoList = { navController.navigate(TODO_LIST) {
                                    popUpTo(TODO_LIST) {
                                        inclusive = true
                                    }
                                } },
                                closeDrawer = { scope.launch { scaffoldState.drawerState.close() } }
                            )
                        }
                    ) {
                        TodoNavGraph(
                            scaffoldState = scaffoldState,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoNavGraph(
    viewModel: TodoListViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController,
    startDestination: String = Destinations.TODO_LIST
) {
    val mainActions = remember(navController) {
        MainActions(navController = navController)
    }

    val scope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.open() } }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(TODO_LIST) {
            TodoListScreen(
                navigateToDetailView = {taskId: Long -> mainActions.navigateToTodoDetail(taskId) },
                navigateToAddEdit = {mainActions.navigateToAddEditScreen(-1L)},
                openDrawer = openDrawer
            )
        }

        composable("${DETAIL_VIEW}/{taskId}", arguments = listOf( navArgument("taskId") { type = NavType.LongType })) { backstackEntry ->
            TaskDetailScreen(
                taskId = backstackEntry.arguments?.getLong("taskId") ?: throw IllegalArgumentException("no taskId is provided."),
                navigateToAddEditScreen = {taskId: Long -> mainActions.navigateToAddEditScreen(taskId)},
                onBack = mainActions.navigateUp
            )
        }

        composable("${ADD_EDIT_TODO}/{taskId}", arguments = listOf(navArgument("taskId") { type = NavType.LongType })) { backstackEntry ->
            AddEditScreen(
                onBack = mainActions.navigateUp,
                taskId = backstackEntry.arguments?.getLong("taskId") ?: -1L
            )
        }

    }
}

@Composable
fun AppDrawer(
    modifier: Modifier,
    currentRoute: String = TODO_LIST,
    navigateToTodoList: () -> Unit,
    closeDrawer: () -> Unit
) {

    Column(modifier = modifier) {
        DrawerHeader(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(180.dp, 240.dp)
                .background(color = MaterialTheme.colors.primaryVariant)
        )

        DrawerButton(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Filled.List,
            title = "Todo List",
            isSelected = currentRoute == TODO_LIST,
            action = { navigateToTodoList(); closeDrawer() }
        )
    }
}

@Composable
fun DrawerButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    isSelected: Boolean = false,
    action: () -> Unit
    ) {

    val colors = MaterialTheme.colors

    val iconAlpha = if (isSelected) {
        1f
    } else {
        0.4f
    }

    val titleTextColor = if (isSelected) {
        colors.primary
    } else {
        colors.onSurface.copy(alpha = .6f)
    }

    val backgroundColor = if(isSelected) {
        colors.primary.copy(alpha = .12f)
    } else {
        Color.Transparent
    }

    Surface(modifier = modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp), color = backgroundColor, shape = MaterialTheme.shapes.small) {
        TextButton(modifier = Modifier.fillMaxWidth(), onClick = { action() }) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = titleTextColor.copy(alpha = iconAlpha))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, style = MaterialTheme.typography.body1, color = titleTextColor)
            }
        }
    }
}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .padding(bottom = 12.dp), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painter = painterResource(id = R.drawable.logo_no_fill), modifier = Modifier.size(60.dp, 60.dp), contentDescription = null, tint = MaterialTheme.colors.onPrimary)
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "Todo", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onPrimary)
    }
}

@Preview
@Composable
fun AppDrawerPreview() {
    ComposeTodoTheme {
        Surface {
            AppDrawer(modifier = Modifier.fillMaxSize(), navigateToTodoList = { }, closeDrawer = {})
        }
    }
}