package com.davi.dev.scannermlkit.presentation.components.appBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.R
import com.davi.dev.scannermlkit.presentation.navigation.Routes
import com.davi.dev.scannermlkit.presentation.screens.viewModel.AuthState
import com.davi.dev.scannermlkit.presentation.screens.viewModel.AuthViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    backStack: NavBackStack<NavKey>,
    homeViewModel: HomeViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val isSearchActive by homeViewModel.isSearchActive.collectAsState()
    val pdfFiles by homeViewModel.filteredFiles.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    val isLoggedIn = authState is AuthState.Success

    if (isSearchActive && isLoggedIn) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { homeViewModel.onSearchQueryChange(it) },
            onSearch = { homeViewModel.onSearchActiveChange(false) },
            active = isSearchActive,
            onActiveChange = { homeViewModel.onSearchActiveChange(it) },
            placeholder = { Text("Search files...") },
            leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = {
                    if (searchQuery.isNotEmpty()) homeViewModel.onSearchQueryChange("")
                    else homeViewModel.onSearchActiveChange(false)
                }) {
                    Icon(painterResource(R.drawable.icon_close), contentDescription = null)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
        ) {
            pdfFiles.take(10).forEach {
                Card(
                    elevation = CardDefaults.elevatedCardElevation(2.dp),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        it.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    } else {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                if (searchQuery.isEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.scanner),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Scanner MLKIT",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                } else {
                    Card(
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_search),
                                contentDescription = "Search doc"
                            )
                            Text(
                                text = searchQuery,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            IconButton(
                                onClick = {
                                    homeViewModel.clearFilters()
                                }) {
                                Icon(
                                    painterResource(R.drawable.icon_close),
                                    contentDescription = "Icon close"
                                )
                            }
                        }
                    }
                }
            },
            actions = {
                if (isLoggedIn && searchQuery.isEmpty() && backStack.last() == Routes.Home)
                    IconButton(
                        onClick = {
                            homeViewModel.onSearchActiveChange(true)
                        }) {
                        Icon(
                            painterResource(R.drawable.ic_search),
                            contentDescription = "Search doc"
                        )
                    }
            }
        )
    }
}
