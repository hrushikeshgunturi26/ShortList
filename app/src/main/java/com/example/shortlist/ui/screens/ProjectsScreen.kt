package com.example.shortlist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

private data class FilmProject(
    val id: String,
    val title: String,
    val description: String,
    val totalShots: Int = 0,
    val filmedShots: Int = 0
)

@Composable
fun ProjectsScreen(
    onProjectClick: (String) -> Unit = {}
) {
    val gold = Color(0xFFFFD99A)
    val muted = Color(0xFFB9B1C8)

    val projects = remember {
        mutableStateListOf(
            FilmProject(
                id = "sample-1",
                title = "The Last Train",
                description = "A chance meeting on a quiet journey home.",
                totalShots = 12,
                filmedShots = 4
            ),
            FilmProject(
                id = "sample-2",
                title = "Sunday Morning",
                description = "Finding a little magic in everyday moments.",
                totalShots = 8,
                filmedShots = 2
            )
        )
    }

    var showNewProject by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val totalShots = projects.sumOf { it.totalShots }
    val filmedShots = projects.sumOf { it.filmedShots }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0B1020),
                        Color(0xFF24203F),
                        Color(0xFF45304C)
                    )
                )
            )
            .safeDrawingPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "SHORTLIST",
                        color = gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Your stories,\nyour next shot.",
                        color = Color.White,
                        fontSize = 34.sp,
                        lineHeight = 41.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "A little planning. A great film.",
                        color = muted,
                        fontSize = 15.sp
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.07f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 22.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProjectStatistic(
                            value = projects.size.toString(),
                            label = "Projects"
                        )
                        ProjectStatistic(
                            value = totalShots.toString(),
                            label = "Shots"
                        )
                        ProjectStatistic(
                            value = filmedShots.toString(),
                            label = "Filmed"
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        title = ""
                        description = ""
                        showNewProject = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gold,
                        contentColor = Color(0xFF171426)
                    )
                ) {
                    Text(
                        text = "+  New Project",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My projects",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${projects.size} projects",
                        color = muted,
                        fontSize = 13.sp
                    )
                }
            }

            items(
                items = projects,
                key = { it.id }
            ) { project ->
                FilmProjectCard(
                    project = project,
                    onClick = { onProjectClick(project.id) }
                )
            }

            item {
                Text(
                    text = "Every story begins with an idea.",
                    color = muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    if (showNewProject) {
        AlertDialog(
            onDismissRequest = { showNewProject = false },
            containerColor = Color(0xFF252238),
            titleContentColor = Color.White,
            title = {
                Text(
                    text = "Start a new story",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Give your film a title and a short description.",
                        color = muted
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it.take(60) },
                        label = { Text("Project title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = projectFieldColors()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it.take(250) },
                        label = { Text("Description (optional)") },
                        minLines = 3,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = projectFieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = title.isNotBlank(),
                    onClick = {
                        projects.add(
                            0,
                            FilmProject(
                                id = UUID.randomUUID().toString(),
                                title = title.trim(),
                                description = description.trim()
                            )
                        )
                        showNewProject = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = gold,
                        disabledContentColor = muted.copy(alpha = 0.4f)
                    )
                ) {
                    Text("Create project")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showNewProject = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = muted
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProjectStatistic(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color(0xFFFFD99A),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = label,
            color = Color(0xFFB9B1C8),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun FilmProjectCard(
    project: FilmProject,
    onClick: () -> Unit
) {
    val progress = if (project.totalShots > 0) {
        project.filmedShots.toFloat() / project.totalShots
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF302B45)
        )
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(20.dp)
        ) {
            Text(
                text = if (project.totalShots == 0) {
                    "READY TO BEGIN"
                } else {
                    "IN PRODUCTION"
                },
                color = Color(0xFFFFD99A),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = project.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.description.ifBlank {
                    "Your next film starts here."
                },
                color = Color(0xFFB9B1C8),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(20.dp))

            // A simple progress track without extra dependencies.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(50)
                    )
            ) {
                if (progress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(
                                Color(0xFFFFD99A),
                                RoundedCornerShape(50)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${project.filmedShots}/${project.totalShots} filmed",
                    color = Color(0xFFB9B1C8),
                    fontSize = 12.sp
                )

                Text(
                    text = "View shots →",
                    color = Color(0xFFFFD99A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun projectFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color(0xFFFFD99A),
    focusedBorderColor = Color(0xFFFFD99A),
    unfocusedBorderColor = Color(0xFF777085),
    focusedLabelColor = Color(0xFFFFD99A),
    unfocusedLabelColor = Color(0xFFB9B1C8)
)

@Preview(showBackground = true)
@Composable
private fun ProjectsScreenPreview() {
    MaterialTheme {
        ProjectsScreen()
    }
}