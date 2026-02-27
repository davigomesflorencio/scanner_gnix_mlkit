package com.davi.dev.scannermlkit.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.domain.enums.UseCaseOptions

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FunctionsHomeApp(backStack: NavBackStack<NavKey>) {

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        items(UseCaseOptions.entries.toTypedArray()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Button(
                    onClick = {
                        backStack.add(it.route)
                    },
                    shape = RoundedPolygon(MaterialShapes.Cookie6Sided).toShape(0),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = it.backgroundColor
                    ),
                    modifier = Modifier.size(74.dp)
                ) {
                    Icon(
                        painterResource(it.icon),
                        contentDescription = it.title,
                        tint = it.color,
                        modifier = Modifier.size(58.dp)
                    )
                }

                Text(
                    it.title, style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 6.dp)
                )

            }
        }
    }
}
