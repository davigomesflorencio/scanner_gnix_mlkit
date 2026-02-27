package com.davi.dev.scannermlkit.presentation.components.bottomBar

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.presentation.navigation.Destination

@Composable
fun BottomBar(backStack: NavBackStack<NavKey>) {

    NavigationBar(
        containerColor = Color(0XFFb6407f),
        modifier = Modifier
            .navigationBarsPadding()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = backStack.last() == destination.route,
                onClick = dropUnlessResumed {
                    backStack.add(destination.route)
                },
                icon = {
                    Icon(
                        painterResource(destination.icon),
                        contentDescription = destination.contentDescription,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(destination.label, style = MaterialTheme.typography.labelMedium)
                }
            )
        }
    }
}