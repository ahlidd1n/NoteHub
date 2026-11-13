package com.example.notehub.screens

import android.R.attr.checked
import android.R.attr.description
import android.R.attr.text
import android.content.Context
import android.text.Layout
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.String
import kotlin.collections.emptyList
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.compose.ui.input.key.Key.Companion.Ro
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.util.TableInfo
import com.example.notehub.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.nio.file.WatchEvent
import kotlin.jvm.java
import kotlin.text.isBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {

    var text by rememberSaveable() { mutableStateOf("") }
    var description by rememberSaveable() { mutableStateOf("") }
    var isComplete by rememberSaveable() { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var search by rememberSaveable() { mutableStateOf("") }
    var showBottomSheet by rememberSaveable() { mutableStateOf(false) }
    var dialogOk by rememberSaveable() { mutableStateOf(false) }
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    var selectedTask by rememberSaveable() {
        mutableStateOf<Task?>(null)
    }
    val bungee = FontFamily(
        Font(R.font.bungee_shade)
    )

    val lemon = FontFamily(
        Font(R.font.lemon)
    )


    val database = remember {
        Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "database1"
        ).build()
    }

    val tasks by database.dao
        .getAllTask()
        .collectAsState(initial = emptyList())



    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {

        val isInitialized = prefs.getBoolean("tasks_initialized", false)

        if (!isInitialized) {

            database.dao.insertTask(
                Task(
                    title = "Welcome to NoteHub",
                    description = "Create your first task",
                    isComplete = false,
                    important = Important.LOW
                )
            )

            database.dao.insertTask(
                Task(
                    title = "Explore NoteHub",
                    description = "Try filters and search",
                    isComplete = false,
                    important = Important.MEDIUM
                )
            )

            database.dao.insertTask(
                Task(
                    title = "Bugs",
                    description = "Write to Ahliddin if you find any bugs pleas :) ahliddinrajabov18@gmail.com",
                    isComplete = false,
                    important = Important.HIGH
                )
            )

            prefs.edit()
                .putBoolean("tasks_initialized", true)
                .apply()
        }
    }


    val task by database.dao.getAllTask()
        .collectAsState(initial = emptyList())
    val totalTask = task.size
    val completedTask = task.count {it.isComplete}
    val completeTask = task.count {!it.isComplete}
    var important by rememberSaveable {
        mutableStateOf(Important.LOW)
    }

    var selectedFilter by rememberSaveable() { mutableStateOf("All") }
    var updateSheet by rememberSaveable() { mutableStateOf(false) }
    var updateTitle by rememberSaveable() { mutableStateOf("") }
    var updateDescription by rememberSaveable() { mutableStateOf("") }
    var settingDialog by rememberSaveable() { mutableStateOf(false) }
    var aboutDialog by rememberSaveable() { mutableStateOf(false) }


    Scaffold(
        containerColor = Color(0xFFF3F6FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = Color(0xff5B4FCF),
            ) {
                Row(
                    modifier = Modifier
                        .padding(end = 13.dp, start = 13.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        "Add Note",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(end = 0.dp)
                    )
                }
            }
        }
    ) { paddingValues ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.notelogo),
                    contentDescription = null
                )
                Spacer(Modifier.width(17.dp))
                Column() {
                    Row() {
                        Text(
                            "Note",
                            fontSize = 23.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontFamily = bungee
                        )
                        Text(
                            "Hub",
                            fontSize = 23.sp,
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Bold,
                            fontFamily = bungee
                        )
                    }
                    Text(
                        "Plan smarter, do more",
                        fontSize = 14.sp,
                        color = Color(0xFF000000),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(17.dp))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            settingDialog = true
                        }
                    ){
                        Icon(
                            painter = painterResource(R.drawable.settings),
                            contentDescription = null,
                            tint = Color(0xFFB7B7C5)
                        )
                    }
                }
            }



            if (settingDialog){
                Dialog(
                    onDismissRequest = {settingDialog = false}
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row() {
                                Text(
                                    "Note",
                                    fontSize = 23.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = bungee
                                )
                                Text(
                                    "Hub",
                                    fontSize = 23.sp,
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = bungee
                                )
                            }

                            Spacer(Modifier.height(10.dp))
                            Text("Settings", fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(15.dp))

                            Button(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Coming Soon",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1D1B4B)
                                )

                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ColorLens,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Text("Appearance", color = Color.White)
                                }
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Coming Soon",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1D1B4B)
                                )

                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Text("Notifications", color = Color.White)
                                }
                            }

                            Button(
                                onClick = {
                                    aboutDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1D1B4B)
                                )

                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Android,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Text("About", color = Color.White)
                                }
                            }

                            if (aboutDialog){
                                Dialog(
                                    onDismissRequest = {aboutDialog = false}
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        )
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp)
                                        ) {
                                            Text(
                                                "Note",
                                                fontSize = 23.sp,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = bungee
                                            )
                                            Text(
                                                "Hub",
                                                fontSize = 23.sp,
                                                color = Color(0xFFFF9800),
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = bungee
                                            )

                                            Spacer(Modifier.height(10.dp))

                                            Text(
                                                text = buildAnnotatedString {
                                                    append("Was created by ")

                                                    withStyle(
                                                        style = SpanStyle(fontWeight = FontWeight.Bold)
                                                    ) {
                                                        append("Rajab team")
                                                    }                                                }
                                            )

                                            Text("v.1.0")
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }


            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(color = Color(0xFFBDBEFC), shape = RoundedCornerShape(16.dp))
                ) {
                    Row() {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = Color(0xFF8162FF),
                            modifier = Modifier
                                .padding(start = 4.dp, top = 8.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)

                    ) {
                        Text("$totalTask", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Text("Total", color = Color(0xFF8162FF), fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(color = Color(0xFFFCF4BD), shape = RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFFD39700),
                        modifier = Modifier
                            .padding(start = 4.dp, top = 8.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)
                    ) {
                        Text("$completeTask", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Text("Pending", color = Color(0xFFD39700), fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .background(color = Color(0xFFBDFCD8), shape = RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.AddTask,
                        contentDescription = null,
                        tint = Color(0xFF00D033),
                        modifier = Modifier
                            .padding(start = 4.dp, top = 8.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)
                    ) {
                        Text("$completedTask", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Text("Done", color = Color(0xFF00D033), fontWeight = FontWeight.Bold)
                    }
                }

            }

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .width(330.dp),
                placeholder = { Text("Search tasks...", color = Color.Black) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFDEE0EE),
                    focusedContainerColor = Color(0xFFDEE0EE),
                    focusedBorderColor = Color(0xFFDEE0EE),
                    unfocusedBorderColor = Color(0xFFDEE0EE),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Black)
                }
            )
            Spacer(Modifier.height(7.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                val items = listOf("All", "Low", "Medium", "High", "Pending", "Done")
                    items.forEachIndexed { index, text ->

                        Button(
                            onClick = {
                                selectedFilter = text
                                selectedIndex = index
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedIndex == index)
                                        Color(0xFF1D1B4B)
                                    else
                                        Color(0xFFEAEAF0)
                            )
                        ) {
                            Text(
                                text = text,
                                color = if (selectedIndex == index)
                                    Color.White
                                else
                                    Color.Gray
                            )
                        }
                    }


            }

            Spacer(Modifier.height(7.dp))

            val filteredTasks = tasks.filter { task ->

                val matchesSearch =
                    task.title.contains(search, ignoreCase = true) ||
                            task.description.contains(search, ignoreCase = true)

                val matchesPriority = when (selectedFilter) {
                    "All" -> true
                    "Low" -> task.important == Important.LOW
                    "Medium" -> task.important == Important.MEDIUM
                    "High" -> task.important == Important.HIGH
                    "Pending" -> !task.isComplete
                    "Done" -> task.isComplete
                    else -> true
                }

                matchesSearch && matchesPriority
            }

            if (tasks.isEmpty()) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(
                        modifier = Modifier
                            .background(color = Color(0xFFBDBEFC), shape = RoundedCornerShape(25.dp))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.no_task),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(10.dp),
                            tint = Color(0xFF8162FF)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Create Your First Note",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8162FF)
                    )
                }

            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(bottom = 0.dp)
            ) {
                items(
                    items = filteredTasks.reversed(),
                    key = { it.id }
                ){ notes ->

                    val Boxcolors = when(notes.important){
                        Important.LOW -> Color(0xFFBDFCD8)
                        Important.MEDIUM -> Color(0xFFFCF4BD)
                        Important.HIGH -> Color(0xFFFCBDBD)
                    }

                    val Textcolors = when(notes.important){
                        Important.LOW -> Color(0xFF00D033)
                        Important.MEDIUM -> Color(0xFFD39700)
                        Important.HIGH -> Color(0xFFD30000)
                    }

                    if (selectedIndex == notes.id){
                        if (updateSheet){
                            ModalBottomSheet(
                                onDismissRequest = {
                                    updateSheet = false
                                    selectedTask = null},
                                shape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp),
                                containerColor = Color.White
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp, top = 10.dp)
                                ) {

                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = Color(0xFFFCF4BD),
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .padding(16.dp)
                                        ){
                                            Icon(
                                                painter = painterResource(R.drawable.task_edit),
                                                contentDescription = null,
                                                tint = Color(0xFFD39700),
                                                modifier = Modifier
                                                    .size(30.dp)
                                            )
                                        }
                                        Text("Edit Task", fontSize = 25.sp, fontFamily = lemon, fontWeight = FontWeight.Bold, color = Color(0xff111827))
                                    }

                                    Spacer(Modifier.height(40.dp))

                                    Text("Task title", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    OutlinedTextField(
                                        value = updateTitle,
                                        onValueChange = { updateTitle = it },
                                        placeholder = { Text("Enter task title...", color = Color.Black) },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = Color(0xFFDEE0EE),
                                            focusedContainerColor = Color(0xFFDEE0EE),
                                            focusedBorderColor = Color(0xFFDEE0EE),
                                            unfocusedBorderColor = Color(0xFFDEE0EE),
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                    )

                                    Spacer(Modifier.height(20.dp))

                                    Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    OutlinedTextField(
                                        value = updateDescription,
                                        onValueChange = { updateDescription = it },
                                        placeholder = { Text("Optional description...", color = Color.Black) },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = Color(0xFFDEE0EE),
                                            focusedContainerColor = Color(0xFFDEE0EE),
                                            focusedBorderColor = Color(0xFFDEE0EE),
                                            unfocusedBorderColor = Color(0xFFDEE0EE),
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Text("Priority",fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    database.dao.updateTask(
                                                        notes.copy(
                                                            important = Important.LOW
                                                        )
                                                    )
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFBDFCD8)
                                            )

                                        ) {
                                            Text("Low", color = Color(0xFF00D033), fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    database.dao.updateTask(
                                                        notes.copy(
                                                            important = Important.MEDIUM
                                                        )
                                                    )
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFCF4BD)
                                            )
                                        ) {
                                            Text("Medium", color = Color(0xFFD39700), fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    database.dao.updateTask(
                                                        notes.copy(
                                                            important = Important.HIGH
                                                        )
                                                    )
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFCBDBD)
                                            )
                                        ) {
                                            Text("High", color = Color(0xFFD30000), fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {

                                        Button(
                                            onClick = {updateSheet = false},
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red
                                            ),
                                            modifier = Modifier
                                                .width(130.dp)
                                                .height(45.dp),
                                            shape = RoundedCornerShape(16.dp)
                                        ){
                                            Text("Cancel", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    database.dao.updateTask(
                                                        notes.copy(
                                                            title = updateTitle,
                                                            description = updateDescription,
                                                            isComplete = if (updateTitle != notes.title || updateDescription != notes.description)false else notes.isComplete
                                                        )
                                                    )
                                                    updateSheet = false
                                                }
                                            },
                                            modifier = Modifier
                                                .width(130.dp)
                                                .height(45.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xff111827)
                                            )
                                        ) {
                                            Text("Save", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        }

                                    }
                                }
                            }
                        }
                    }


                    Card(
                        modifier = Modifier
                            .width(340.dp)
                            .padding(2.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        ),
                        shape = RoundedCornerShape(16.dp),
                    ) {

                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                        ) {

                            Checkbox(
                                checked = notes.isComplete,
                                onCheckedChange = { checked ->
                                    scope.launch {
                                        database.dao.updateTask(notes.copy(isComplete = checked))
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Green
                                ),
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(50.dp))
                            )

                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    notes.title,
                                    fontSize = 23.sp,
                                    textDecoration = if (notes.isComplete) {
                                        TextDecoration.LineThrough
                                    } else {
                                        TextDecoration.None
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = if (notes.isComplete){
                                        Color.Gray
                                    }else{Color.Black}
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    notes.description,
                                    fontSize = 19.sp,
                                    textDecoration = if (notes.isComplete) {
                                        TextDecoration.LineThrough
                                    } else {
                                        TextDecoration.None
                                    },
                                    color = Color.DarkGray
                                )
                                Spacer(Modifier.height(7.dp))
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .background(color = Boxcolors, shape = RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center,
                                ){
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .background(color = Textcolors, shape = RoundedCornerShape(25.dp))
                                        ){}
                                        Spacer(Modifier.width(4.dp))
                                        Text(notes.important.name, fontSize = 13.sp, color = Textcolors, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }


                            var showDialog by rememberSaveable() {mutableStateOf(false) }
                            Column(
                                horizontalAlignment = Alignment.End,
                            ) {
                                IconButton(
                                    onClick = {
                                        showDialog = true
                                    },

                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.delete),
                                        contentDescription = null
                                    )
                                }

                                if(showDialog){
                                    Dialog(
                                        onDismissRequest = {showDialog = false}
                                    ){

                                        Card(
                                            shape = RoundedCornerShape(20.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White
                                            )
                                        ) {
                                            Column(
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .padding(20.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(color = Color(0xFFFCECBD), shape = RoundedCornerShape(25.dp))
                                                        .size(55.dp),
                                                    contentAlignment = Alignment.Center
                                                ){
                                                    Icon(
                                                        painter = painterResource(R.drawable.alert),
                                                        contentDescription = null,
                                                        tint = Color(0xFFFF6617),
                                                        modifier = Modifier.size(37.dp)
                                                    )
                                                }
                                                Text(
                                                    text = "Delete task?",
                                                    fontSize = 23.sp,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Spacer(modifier = Modifier.height(10.dp))

                                               Text(
                                                   text = buildAnnotatedString {
                                                       append("Are you sure you want to delete ")

                                                       withStyle(
                                                           style = SpanStyle(fontWeight = FontWeight.Bold)
                                                       ) {
                                                           append(notes.title)
                                                       }

                                                       append(" task?")
                                                   }
                                               )

                                                Spacer(modifier = Modifier.height(20.dp))

                                                Row (
                                                    horizontalArrangement = Arrangement.SpaceAround,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                ){
                                                    Button(
                                                        onClick = {
                                                            showDialog = false
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color(0xFF1D1B4B)
                                                        ),
                                                    ) {
                                                        Text("Cancel",color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                                    }

                                                    Spacer(modifier = Modifier.width(10.dp))

                                                    Button(
                                                        onClick = {
                                                            scope.launch {
                                                                database.dao.deleteTask(notes)
                                                            }
                                                            showDialog = false
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = Color(0xFFE70000)
                                                        )
                                                    ) {
                                                        Text("Delete", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }

                                    }

                                    }
                                }



                                IconButton(
                                    onClick = {
                                        selectedIndex = notes.id
                                        updateSheet = true
                                        updateTitle = notes.title
                                        updateDescription = notes.description
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.pen),
                                        contentDescription = null
                                    )
                                }
                            }

                        }

                    }

                }
            }



            if (dialogOk){
                Dialog(
                    onDismissRequest = {dialogOk = false}
                ) {
                    Card(
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(color = Color(0xFFBDFCD8), shape = RoundedCornerShape(25.dp))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ){
                                Icon(
                                    painter = painterResource(R.drawable.add),
                                    contentDescription = null,
                                    tint = Color(0xFF00D033),
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Text("Task Created", fontSize = 25.sp, fontWeight = FontWeight.Bold)

                            Spacer(Modifier.height(10.dp))

                            Text("Your new task has been added to the board.", fontSize = 19.sp, color = Color.Gray, textAlign = TextAlign.Center)

                            Spacer(Modifier.height(10.dp))

                            Button(
                                onClick = {dialogOk = false},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xff5B4FCF)
                                )
                            ) {
                                Text("Done", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }


            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                ) {


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 10.dp),
                    ) {


                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFBDFCD8),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(16.dp)
                            ){
                                Icon(
                                    painter = painterResource(R.drawable.create_icon),
                                    contentDescription = null,
                                    tint = Color(0xFF00D033),
                                    modifier = Modifier
                                        .size(30.dp)
                                )
                            }
                            Text("Create New Task", fontSize = 25.sp, fontFamily = lemon, fontWeight = FontWeight.Bold, color = Color(0xff111827))
                        }

                        Spacer(Modifier.height(40.dp))

                        Text("Task title", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = { Text("Enter task title...", color = Color.Black) },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFDEE0EE),
                                focusedContainerColor = Color(0xFFDEE0EE),
                                focusedBorderColor = Color(0xFFDEE0EE),
                                unfocusedBorderColor = Color(0xFFDEE0EE),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp),
                        )

                        Spacer(Modifier.height(20.dp))

                        Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Optional description...", color = Color.Black) },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFDEE0EE),
                                focusedContainerColor = Color(0xFFDEE0EE),
                                focusedBorderColor = Color(0xFFDEE0EE),
                                unfocusedBorderColor = Color(0xFFDEE0EE),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp),
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Priority",fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Button(
                                onClick = {
                                    important = Important.LOW
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFBDFCD8)
                                )

                            ) {
                                Text("Low", color = Color(0xFF00D033), fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    important = Important.MEDIUM
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFCF4BD)
                                )
                            ) {
                                Text("Medium", color = Color(0xFFD39700), fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    important = Important.HIGH
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFCBDBD)
                                )
                            ) {
                                Text("High", color = Color(0xFFD30000), fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            Button(
                                onClick = {showBottomSheet = false},
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red
                                ),
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(45.dp),
                                shape = RoundedCornerShape(16.dp)
                            ){
                                Text("Cancel", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (text.isNotBlank() || description.isNotBlank()) {
                                        scope.launch {
                                            database.dao.insertTask(
                                                Task(
                                                    title = text,
                                                    description = description,
                                                    isComplete = isComplete,
                                                    important = important
                                                )
                                            )
                                            showBottomSheet = false
                                            text = ""
                                            description = ""
                                            dialogOk = true
                                        }
                                    } else {
                                        text = ""
                                        description = ""
                                    }
                                },
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(45.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xff111827)
                                )
                            ) {
                                Text("Create", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }




                        }


                    }

                }




            }

        }
    }

}


@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String = "Nothing was written",
    var description: String,
    val isComplete: Boolean,
    val important: Important = Important.LOW
)


@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM Task")
    fun getAllTask(): Flow<List<Task>>
}

@TypeConverters(Converters::class)
@Database(
    entities = [Task::class],
    version = 2
)
abstract class TaskDatabase : RoomDatabase() {
    abstract val dao: TaskDao
}


class TaskRepository(
    private val dao: TaskDao
) {

    val notes = dao.getAllTask()

    suspend fun insert(task: Task) {
        dao.insertTask(task)
    }

    suspend fun delete(task: Task) {
        dao.deleteTask(task)
    }

    suspend fun update(task: Task) {
        dao.updateTask(task)
    }
}


enum class Important{
    LOW,
    MEDIUM,
    HIGH
}

class Converters {

    @TypeConverter
    fun fromPriority(priority: Important): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Important {
        return Important.valueOf(value)
    }
}