package com.masterbit.composetodo.common

import androidx.navigation.NavController

class MainActions(navController: NavController) {
    val navigateToTodoDetail: (taskId: Long) -> Unit = { taskId ->
        navController.navigate("${Destinations.DETAIL_VIEW}/${taskId}")
    }

    val navigateToAddEditScreen: (taskId: Long) -> Unit = { taskId ->
        taskId.let {
            navController.navigate("${Destinations.ADD_EDIT_TODO}/${it}")
        }
    }

    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
}