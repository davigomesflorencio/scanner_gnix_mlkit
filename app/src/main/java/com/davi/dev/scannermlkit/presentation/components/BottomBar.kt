package com.davi.dev.scannermlkit.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.presentation.navigation.Destination

@Composable
fun BottomBar(backStack: SnapshotStateList<Any>, selectedDestination: MutableState<Int>) {

    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets,
        modifier = Modifier
            .padding(30.dp)
            .clip(RoundedCornerShape(30.dp))
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestination.value == index,
                onClick = {
                    backStack.add(destination.route)
                    selectedDestination.value = index
                },
                icon = {
                    Icon(
                        destination.icon,
                        contentDescription = destination.contentDescription
                    )
                },
                label = {
                    Text(
                        destination.label, style = MaterialTheme.typography
                            .headlineMedium
                    )
                }
            )
        }
    }
}