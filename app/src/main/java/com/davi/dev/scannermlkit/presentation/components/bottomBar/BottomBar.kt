package com.davi.dev.scannermlkit.presentation.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.presentation.navigation.Destination

@Composable
fun BottomBar(backStack: NavBackStack<NavKey>) {

    BottomNavigation(
        backgroundColor = Color(0XFFb6407f),
        modifier = Modifier
            .padding(30.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(30.dp))
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            BottomNavigationItem(
                selected = backStack.first() == destination.route,
                onClick = dropUnlessResumed {
                    backStack.add(destination.route)
                },
                icon = {
                    Icon(
                        destination.icon,
                        contentDescription = destination.contentDescription
                    )
                }
            )
        }
    }
}