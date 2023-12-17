package com.example.notes.view

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.notes.R
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.viewmodel.MainViewModel
import com.example.notes.model.DBHelper
import com.example.notes.model.Note
import com.example.notes.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesTheme {
                val dbHelper = DBHelper(this)
                val mvm = ViewModelProvider(
                    this,
                    MainViewModelFactory(dbHelper)
                )[MainViewModel::class.java]
                MainUI(
                    modifier = Modifier.fillMaxSize(),
                    onAdd = mvm::addNote,
                )
                {
                    LazyNotes(
                        notesList = mvm.notesList.reversed(),
                        onAdd = mvm::addNote,
                        onEdit = mvm::editNote,
                        onDelete = mvm::deleteNote
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI(
    modifier: Modifier = Modifier,
    onAdd: (String, Int) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
//                navigationIcon = (),
                title = {
                    Text(
                        text = "Notes",
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_circle_24),
                    contentDescription = "Add",
                    tint = Color.LightGray
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            content()
        }
    }

    if (showDialog) {
        NoteInput(
            onDismiss = { showDialog = false },
            isEditMode = false,
            note = null,
            onAdd = onAdd,
            onEdit = null,
            onDelete = null,
        )
    }
}

@Preview
@Composable
fun LazyNotesPreview() {
    val sampleNotes = listOf(
        Note(
            1,
            "Заметка 1 2131231231231231231231231231231232312312312312312312312321",
            System.currentTimeMillis(),
            0
        ),
        Note(2, "Заметка 2", System.currentTimeMillis(), 0),
        Note(3, "Заметка 3", System.currentTimeMillis(), 0),
        Note(4, "Заметка 4", System.currentTimeMillis(), 0),
        Note(5, "Заметка 5", System.currentTimeMillis(), 0),
        Note(6, "Заметка 6", System.currentTimeMillis(), 0),
        Note(7, "Заметка 7", System.currentTimeMillis(), 0)
    )
    LazyNotes(
        notesList = sampleNotes,
        onEdit = null,
        onAdd = null,
        onDelete = null
    )
}

@Preview
@Composable
fun EditNoteInputPreview() {
    val sampleNote = Note(1, "Заметка 1 213123123123123123", System.currentTimeMillis(), 1)
    NoteInput(
        onDismiss = { /*TODO*/ },
        isEditMode = true,
        onEdit = null,
        onAdd = null,
        onDelete = null,
        note = sampleNote
    )
}

@Preview
@Composable
fun AddNoteInputPreview() {
    val sampleNote = Note(1, "Заметка 1 213123123123123123", System.currentTimeMillis(), 2)
    NoteInput(
        onDismiss = { /*TODO*/ },
        isEditMode = false,
        onEdit = null,
        onAdd = null,
        onDelete = null,
        note = sampleNote
    )
}

@Composable
fun LazyNotes(
    notesList: List<Note>,
    onEdit: ((Int, String, Int, Long) -> Unit)?,
    onAdd: ((String, Int) -> Unit)?,
    onDelete: ((Int) -> Unit)?
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notesList.size) { index ->
            NoteCard(
                note = notesList[index],
                onEdit = onEdit,
                onAdd = onAdd,
                onDelete = onDelete
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    note: Note,
    onEdit: ((Int, String, Int, Long) -> Unit)?,
    onAdd: ((String, Int) -> Unit)?,
    onDelete: ((Int) -> Unit)?
) {
    var showDialog by remember { mutableStateOf(false) }
    ElevatedCard(
        onClick = { showDialog = true },
        modifier = Modifier
            .size(width = 130.dp, height = 110.dp)
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = note.content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = SimpleDateFormat("dd/MM/yyyy HH:mm").format(note.timestamp),
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    if (showDialog) {
        NoteInput(
            onDismiss = { showDialog = false },
            isEditMode = true,
            note = note,
            onEdit = onEdit,
            onAdd = onAdd,
            onDelete = onDelete
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteInput(
    onDismiss: () -> Unit,
    isEditMode: Boolean,
    onEdit: ((Int, String, Int, Long) -> Unit)?,
    onAdd: ((String, Int) -> Unit)?,
    onDelete: ((Int) -> Unit)?,
    note: Note?
) {
    var content by remember { mutableStateOf(note?.content ?: "") }
    var priority by remember { mutableStateOf(note?.priority ?: 0) }

    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf("Low", "Medium", "High")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isEditMode) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "Edit note"
                    )
                    IconButton(onClick = {
                        onDelete?.let { it(note!!.id!!) }
                        onDismiss()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_delete_24),
                            contentDescription = "Delete"
                        )
                    }
                } else {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "Add note"
                    )
                }
            }
        },
        text = {
            Column {
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        readOnly = true,
                        value = priorities[priority],
                        onValueChange = { },
                        label = { Text("Priority") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        priorities.forEachIndexed { index, s ->
                            DropdownMenuItem(
                                onClick = {
                                    priority = index
                                    expanded = false
                                },
                                text = { Text(text = s) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isEditMode) {
                    onEdit?.let { it(note!!.id!!, content, priority, note.timestamp) }
                } else {
                    onAdd?.let { it(content, priority) }
                }
                onDismiss()
            }) {
                Text(text = if (isEditMode) "Save" else "Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}