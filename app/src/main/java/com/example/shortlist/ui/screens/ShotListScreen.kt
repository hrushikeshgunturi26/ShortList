package com.example.shortlist.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FilmShot(
    val id: String,
    val projectId: String,
    val sceneNumber: Int,
    val shotOrder: Int,
    val description: String,
    val shotType: String,
    val notes: String = "",
    val isFilmed: Boolean = false
)

@Composable
fun ShotListScreen(
    projectId: String,
    projectTitle: String,
    shots: List<FilmShot>,
    onBack: () -> Unit,
    onAddShot: () -> Unit,
    onEditShot: (String) -> Unit,
    onFilmedChange: (String, Boolean) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    isUpdating: Boolean = false
) {
    val gold = Color(0xFFFFD99A)
    val muted = Color(0xFFB9B1C8)

    var selectedFilter by rememberSaveable(projectId) {
        mutableStateOf("All")
    }

    val projectShots = shots
        .filter { it.projectId == projectId }
        .sortedWith(
            compareBy<FilmShot> { it.sceneNumber }
                .thenBy { it.shotOrder }
                .thenBy { it.id }
        )

    val filmedCount = projectShots.count { it.isFilmed }

    val visibleShots = when (selectedFilter) {
        "Pending" -> projectShots.filter { !it.isFilmed }
        "Filmed" -> projectShots.filter { it.isFilmed }
        else -> projectShots
    }

    val progress = if (projectShots.isEmpty()) {
        0f
    } else {
        filmedCount.toFloat() / projectShots.size
    }

    BackHandler(onBack = onBack)

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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = gold
                    )
                ) {
                    Text("← Back to projects")
                }
            }

            item {
                Column {
                    Text(
                        text = "SHOT LIST",
                        color = gold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = projectTitle,
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "One shot closer to your story.",
                        color = muted,
                        fontSize = 14.sp
                    )
                }
            }

            if (!isLoading) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.07f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Filming progress",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "$filmedCount of ${projectShots.size} shots filmed",
                                color = muted,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.1f),
                                        RoundedCornerShape(50)
                                    )
                            ) {
                                if (progress > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(progress)
                                            .fillMaxHeight()
                                            .background(
                                                gold,
                                                RoundedCornerShape(50)
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${projectShots.size - filmedCount} shots remaining",
                                color = gold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onAddShot,
                    enabled = !isLoading && !isUpdating,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gold,
                        contentColor = Color(0xFF171426)
                    )
                ) {
                    Text(
                        text = "+  Add Shot",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("All", "Pending", "Filmed").forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.Transparent,
                                labelColor = muted,
                                selectedContainerColor = gold,
                                selectedLabelColor = Color(0xFF171426)
                            )
                        )
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                item {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFFB4AB),
                        fontSize = 14.sp
                    )
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = gold,
                            modifier = Modifier
                                .padding(24.dp)
                                .size(32.dp)
                        )
                    }
                }
            } else if (visibleShots.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {
                        Text(
                            text = when {
                                !errorMessage.isNullOrBlank() ->
                                    "No shots to display"

                                projectShots.isEmpty() ->
                                    "Your first shot starts here"

                                selectedFilter == "Filmed" ->
                                    "No filmed shots yet"

                                else ->
                                    "No pending shots"
                            },
                            color = Color.White,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = when {
                                !errorMessage.isNullOrBlank() ->
                                    "Resolve the error above to check your shots."

                                projectShots.isEmpty() ->
                                    "Tap Add Shot to start planning this project."

                                selectedFilter == "Filmed" ->
                                    "Mark a shot as filmed when it is complete."

                                else ->
                                    "All your current shots have been filmed."
                            },
                            color = muted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(
                    items = visibleShots,
                    key = { it.id }
                ) { shot ->
                    ShotListCard(
                        shot = shot,
                        enabled = !isUpdating,
                        onEdit = { onEditShot(shot.id) },
                        onToggleFilmed = {
                            onFilmedChange(shot.id, !shot.isFilmed)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShotListCard(
    shot: FilmShot,
    enabled: Boolean,
    onEdit: () -> Unit,
    onToggleFilmed: () -> Unit
) {
    val gold = Color(0xFFFFD99A)
    val muted = Color(0xFFB9B1C8)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF302B45)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SCENE ${shot.sceneNumber}  ·  SHOT ${shot.shotOrder}",
                color = gold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = shot.description,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = shot.shotType,
                color = muted,
                fontSize = 13.sp
            )

            if (shot.notes.isNotBlank()) {
                Text(
                    text = shot.notes,
                    color = muted,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }

            Text(
                text = if (shot.isFilmed) "FILMED" else "PENDING",
                color = if (shot.isFilmed) {
                    Color(0xFFA8DEBB)
                } else {
                    gold
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            OutlinedButton(
                onClick = onToggleFilmed,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = gold
                )
            ) {
                Text(
                    text = if (shot.isFilmed) {
                        "Mark as pending"
                    } else {
                        "Mark as filmed"
                    }
                )
            }

            TextButton(
                onClick = onEdit,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = muted
                )
            ) {
                Text("Edit shot")
            }
        }
    }
}