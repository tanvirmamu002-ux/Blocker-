package com.example.ui.components

import androidx.compose.runtime.Composable
import com.example.state.FocusViewModel
import com.example.ui.screens.FocusLockScreen

@Composable
fun FocusLockSetupDialog(
    viewModel: FocusViewModel
) {
    if (!viewModel.isFocusLockSetupDialogVisible) return

    FocusLockScreen(
        viewModel = viewModel,
        onBack = { viewModel.isFocusLockSetupDialogVisible = false }
    )
}
