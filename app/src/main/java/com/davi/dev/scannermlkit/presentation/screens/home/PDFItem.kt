package com.davi.dev.scannermlkit.presentation.screens.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.R
import com.davi.dev.scannermlkit.domain.date.DateUtil.formatDate
import com.davi.dev.scannermlkit.presentation.navigation.Routes
import com.davi.dev.scannermlkit.presentation.screens.viewModel.HomeViewModel
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFItem(
    homeViewModel: HomeViewModel,
    navBackStack: NavBackStack<NavKey>,
    file: File
) {
    val context = LocalContext.current
    val authority = "${context.packageName}.provider"
    val pdfUri = FileProvider.getUriForFile(context, authority, file)
    val formattedDate = remember(file) { formatDate(file.lastModified()) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf(file.nameWithoutExtension) }

    LaunchedEffect(Unit) {
        homeViewModel.saveStatus.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .clickable {
                navBackStack.add(Routes.ViewDocument(file.path))
            }
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            ImagePdfPreview(
                file = file,
                modifier = Modifier
                    .size(75.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .fillMaxWidth(.7f)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Button to share PDF
                IconButton(onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, pdfUri)
                        type = "application/pdf"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Export PDF"))

                }) {
                    Icon(
                        painterResource(R.drawable.ic_share),
                        contentDescription = "Share doc"
                    )
                }

                // Button to open PDF options
                IconButton(onClick = {
                    showBottomSheet = true
                }) {
                    Icon(
                        painterResource(R.drawable.ic_ellipsis_vertical),
                        contentDescription = "Options doc"
                    )
                }

            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 8.dp, end = 8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        ImagePdfPreview(
                            file = file,
                            modifier = Modifier
                                .size(75.dp)
                        )

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = file.name,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }

                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("Save to device") },
                    leadingContent = { Icon(Icons.Default.Save, contentDescription = null) },
                    modifier = Modifier.clickable {
                        homeViewModel.savePdfToDownloads(context, file, file.name)
                        showBottomSheet = false
                    }
                )

                ListItem(
                    headlineContent = { Text("Export") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_share), contentDescription = null) },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, pdfUri)
                            type = "application/pdf"
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Export PDF"))
                    }
                )

                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("Split PDF") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_split), contentDescription = null) },
                    modifier = Modifier.clickable { showBottomSheet = false }
                )

                ListItem(
                    headlineContent = { Text("Merge PDF") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_combine), contentDescription = null) },
                    modifier = Modifier.clickable { showBottomSheet = false }
                )

                ListItem(
                    headlineContent = { Text("Protect PDF") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_protected), contentDescription = null) },
                    modifier = Modifier.clickable { showBottomSheet = false }
                )

                ListItem(
                    headlineContent = { Text("Compress PDF") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_wand), contentDescription = null) },
                    modifier = Modifier.clickable { showBottomSheet = false }
                )

                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("Rename") },
                    leadingContent = { Icon(painterResource(R.drawable.ic_file_pen), contentDescription = null) },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        newFileName = file.nameWithoutExtension // Initialize with current name
                        showRenameDialog = true
                    }
                )

                ListItem(
                    headlineContent = { Text("Print") },
                    leadingContent = { Icon(Icons.Default.Print, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        homeViewModel.printPdf(context, file, pdfUri)
                    }
                )

                ListItem(
                    headlineContent = {
                        Text(
                            "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        showDeleteDialog = true
                    }
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this file? ${file.name}") },
            confirmButton = {
                Button(
                    onClick = {
                        homeViewModel.deleteFile(file)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename File") },
            text = {
                OutlinedTextField(
                    value = newFileName,
                    onValueChange = { newFileName = it },
                    label = { Text("New File Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            homeViewModel.renameFile(file, newFileName)
                            showRenameDialog = false
                        }
                    }
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}