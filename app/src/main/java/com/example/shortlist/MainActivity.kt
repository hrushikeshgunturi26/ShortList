package com.example.shortlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shortlist.ui.screens.AddEditShotScreen
import com.example.shortlist.ui.screens.FilmProject
import com.example.shortlist.ui.screens.FilmShot
import com.example.shortlist.ui.screens.ProjectsScreen
import com.example.shortlist.ui.screens.ShotFormData
import com.example.shortlist.ui.screens.ShotListScreen
import com.example.shortlist.ui.screens.SplashScreen
import com.example.shortlist.ui.theme.ShortListTheme
import java.util.UUID

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )

        val appViewModel = ViewModelProvider(this)
            .get(ShortListViewModel::class.java)

        setContent {
            ShortListTheme {
                ShortListApp(viewModel = appViewModel)
            }
        }
    }
}

@Composable
private fun ShortListApp(
    viewModel: ShortListViewModel
) {
    var currentScreen by rememberSaveable {
        mutableStateOf("splash")
    }

    var selectedProjectId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var editingShotId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val selectedProject = viewModel.projects.firstOrNull {
        it.id == selectedProjectId
    }

    // Calculate card counts from the actual shot records.
    val projectsWithCounts = viewModel.projects.map { project ->
        val projectShots = viewModel.shots.filter {
            it.projectId == project.id
        }

        project.copy(
            totalShots = projectShots.size,
            filmedShots = projectShots.count { it.isFilmed }
        )
    }

    val returnToProjects: () -> Unit = {
        currentScreen = "projects"
        selectedProjectId = null
        editingShotId = null
    }

    // A project stored only in memory may no longer exist
    // after Android terminates and recreates the app process.
    LaunchedEffect(currentScreen, selectedProjectId, selectedProject) {
        if (
            currentScreen in listOf("shots", "shotForm") &&
            selectedProject == null
        ) {
            returnToProjects()
        }
    }

    when (currentScreen) {
        "splash" -> {
            SplashScreen(
                onSplashFinished = {
                    currentScreen = "projects"
                }
            )
        }

        "projects" -> {
            ProjectsScreen(
                projects = projectsWithCounts,
                onCreateProject = { title, description ->
                    viewModel.addProject(title, description)
                },
                onProjectClick = { projectId ->
                    selectedProjectId = projectId
                    currentScreen = "shots"
                }
            )
        }

        "shots" -> {
            selectedProject?.let { project ->
                ShotListScreen(
                    projectId = project.id,
                    projectTitle = project.title,
                    shots = viewModel.shots,
                    onBack = returnToProjects,
                    onAddShot = {
                        editingShotId = null
                        currentScreen = "shotForm"
                    },
                    onEditShot = { shotId ->
                        editingShotId = shotId
                        currentScreen = "shotForm"
                    },
                    onFilmedChange = { shotId, isFilmed ->
                        viewModel.setFilmed(
                            projectId = project.id,
                            shotId = shotId,
                            isFilmed = isFilmed
                        )
                    }
                )
            }
        }

        "shotForm" -> {
            selectedProject?.let { project ->
                val existingShot = viewModel.shots.firstOrNull {
                    it.id == editingShotId &&
                            it.projectId == project.id
                }

                var saveError by remember(
                    project.id,
                    editingShotId
                ) {
                    mutableStateOf<String?>(null)
                }

                AddEditShotScreen(
                    projectId = project.id,
                    projectTitle = project.title,
                    shotId = editingShotId,
                    initialShot = existingShot?.let { shot ->
                        ShotFormData(
                            sceneNumber = shot.sceneNumber,
                            shotOrder = shot.shotOrder,
                            description = shot.description,
                            shotType = shot.shotType,
                            notes = shot.notes
                        )
                    },
                    saveError = saveError,
                    onSave = { formData ->
                        val saved = viewModel.saveShot(
                            projectId = project.id,
                            shotId = editingShotId,
                            data = formData
                        )

                        if (saved) {
                            editingShotId = null
                            currentScreen = "shots"
                        } else {
                            saveError =
                                "Unable to save this shot. Go back and try again."
                        }
                    },
                    onBack = {
                        editingShotId = null
                        currentScreen = "shots"
                    }
                )
            }
        }
    }
}

class ShortListViewModel : ViewModel() {

    private val projectItems = mutableStateListOf(
        FilmProject(
            id = "sample-1",
            title = "The Last Train",
            description = "A chance meeting on a quiet journey home."
        ),
        FilmProject(
            id = "sample-2",
            title = "Sunday Morning",
            description = "Finding a little magic in everyday moments."
        )
    )

    val projects: List<FilmProject>
        get() = projectItems

    private val shotItems = mutableStateListOf(
        FilmShot(
            id = "shot-1",
            projectId = "sample-1",
            sceneNumber = 1,
            shotOrder = 1,
            description = "An empty platform just before sunset.",
            shotType = "Wide shot",
            notes = "Use natural evening light.",
            isFilmed = true
        ),
        FilmShot(
            id = "shot-2",
            projectId = "sample-1",
            sceneNumber = 1,
            shotOrder = 2,
            description = "A traveller checks the station clock.",
            shotType = "Close-up",
            notes = "Keep the clock visible in the background."
        ),
        FilmShot(
            id = "shot-3",
            projectId = "sample-2",
            sceneNumber = 1,
            shotOrder = 1,
            description = "Sunlight falls across the breakfast table.",
            shotType = "Medium shot",
            notes = "Film beside a window."
        )
    )

    val shots: List<FilmShot>
        get() = shotItems

    fun addProject(title: String, description: String) {
        if (title.isBlank()) return

        projectItems.add(
            0,
            FilmProject(
                id = UUID.randomUUID().toString(),
                title = title.trim().take(60),
                description = description.trim().take(250)
            )
        )
    }

    fun saveShot(
        projectId: String,
        shotId: String?,
        data: ShotFormData
    ): Boolean {
        if (projectItems.none { it.id == projectId }) return false

        if (
            data.sceneNumber <= 0 ||
            data.shotOrder <= 0 ||
            data.description.isBlank() ||
            data.shotType.isBlank()
        ) {
            return false
        }

        if (shotId == null) {
            shotItems.add(
                FilmShot(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    sceneNumber = data.sceneNumber,
                    shotOrder = data.shotOrder,
                    description = data.description.trim().take(200),
                    shotType = data.shotType,
                    notes = data.notes.trim().take(500),
                    isFilmed = false
                )
            )
        } else {
            val index = shotItems.indexOfFirst {
                it.id == shotId && it.projectId == projectId
            }

            if (index == -1) return false

            // copy preserves the existing filming status.
            shotItems[index] = shotItems[index].copy(
                sceneNumber = data.sceneNumber,
                shotOrder = data.shotOrder,
                description = data.description.trim().take(200),
                shotType = data.shotType,
                notes = data.notes.trim().take(500)
            )
        }

        return true
    }

    fun setFilmed(
        projectId: String,
        shotId: String,
        isFilmed: Boolean
    ) {
        val index = shotItems.indexOfFirst {
            it.id == shotId && it.projectId == projectId
        }

        if (index != -1) {
            shotItems[index] = shotItems[index].copy(
                isFilmed = isFilmed
            )
        }
    }
}