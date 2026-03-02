package com.davi.dev.scannermlkit.presentation.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import com.davi.dev.scannermlkit.presentation.screens.viewModel.AccountViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ThemeMode

@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    onSignOut: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Account & Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Theme Mode Section
        item {
            SectionTitle(title = "Theme")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ThemeOption(
                    title = "Light",
                    icon = Icons.Default.Brightness7,
                    selected = viewModel.themeMode == ThemeMode.LIGHT,
                    onClick = { viewModel.setTheme(ThemeMode.LIGHT) }
                )
                ThemeOption(
                    title = "Dark",
                    icon = Icons.Default.Brightness4,
                    selected = viewModel.themeMode == ThemeMode.DARK,
                    onClick = { viewModel.setTheme(ThemeMode.DARK) }
                )
                ThemeOption(
                    title = "System Default",
                    icon = Icons.Default.BrightnessAuto,
                    selected = viewModel.themeMode == ThemeMode.SYSTEM,
                    onClick = { viewModel.setTheme(ThemeMode.SYSTEM) }
                )
            }
        }

        item { HorizontalDivider() }

        // Theme Color Section
        item {
            SectionTitle(title = "Primary Color")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                LazyRow(
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    viewModel.colorOptions.forEach { colorOption ->
                        item {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ColorCircle(
                                    color = colorOption.color,
                                    selected = viewModel.selectedColor == colorOption,
                                    onClick = { viewModel.setColor(colorOption) }
                                )
                                Text(
                                    colorOption.name,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        item { HorizontalDivider() }

        // Info Section
        item {
            SectionTitle(title = "App Info")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ListItem(
                    headlineContent = { Text("Version") },
                    supportingContent = { Text(viewModel.appVersion) },
                    leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
                )
                ListItem(
                    headlineContent = { Text("Support") },
                    supportingContent = { Text(viewModel.supportEmail) },
                    leadingContent = { Icon(Icons.Default.ContactSupport, contentDescription = null) },
                    modifier = Modifier.clickable { /* Handle support click */ }
                )
            }
        }

        item { HorizontalDivider() }

        // Sign Out Section
        item {
            ListItem(
                headlineContent = {
                    Text(
                        "Sair da conta",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier.clickable { onSignOut() }
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ThemeOption(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = {
            RadioButton(selected = selected, onClick = null)
        },
        modifier = Modifier.clickable { onClick() }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColorCircle(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(
                if (selected) {
                    RoundedPolygon(MaterialShapes.Sunny).toShape(0)
                } else {
                    CircleShape
                }
            )
            .background(color)
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Check",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
            )
        }
    }
}
