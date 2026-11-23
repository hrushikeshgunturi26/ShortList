package com.example.shortlist.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShotFormData(
    val sceneNumber: Int,
    val shotOrder: Int,
    val description: String,
    val shotType: String,
    val notes: String
)

@Composable
fun AddEditShotScreen(
    projectId: String,
    projectTitle: String,
    onSave: (ShotFormData) -> Unit,
    onBack: () -> Unit,
    shotId: String? = null,
    initialShot: ShotFormData? = null,
    isSaving: Boolean = false,
    saveError: String? = null
) {
    val gold = Color(0xFFFFD99A)
    val muted = Color(0xFFB9B1C8)
    val isEditing = shotId != null

    var sceneNumber by rememberSaveable(projectId, shotId) {
        mutableStateOf(initialShot?.sceneNumber?.toString() ?: "")
    }
    var shotOrder by rememberSaveable(projectId, shotId) {
        mutableStateOf(initialShot?.shotOrder?.toString() ?: "")
    }
    var description by rememberSaveable(projectId, shotId) {
        mutableStateOf(initialShot?.description ?: "")
    }
    var shotType by rememberSaveable(projectId, shotId) {
        mutableStateOf(initialShot?.shotType ?: "")
    }
    var notes by rememberSaveable(projectId, shotId) {
        mutableStateOf(initialShot?.notes ?: "")
    }
    var attemptedSave by rememberSaveable(projectId, shotId) {
        mutableStateOf(false)
    }
    var menuExpanded by remember { mutableStateOf(false) }

    val shotTypes = listOf(
        "Wide shot",
        "Medium shot",
        "Close-up",
        "Extreme close-up",
        "Over-the-shoulder",
        "Point of view",
        "Tracking shot"
    )

    val sceneValue = sceneNumber.toIntOrNull()
    val orderValue = shotOrder.toIntOrNull()

    val sceneInvalid = sceneValue == null || sceneValue <= 0
    val orderInvalid = orderValue == null || orderValue <= 0
    val descriptionInvalid = description.isBlank()
    val typeInvalid = shotType !in shotTypes

    val canSubmit = !sceneInvalid &&
            !orderInvalid &&
            !descriptionInvalid &&
            !typeInvalid

    BackHandler {
        if (!isSaving) onBack()
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        disabledTextColor = muted,
        cursorColor = gold,
        focusedBorderColor = gold,
        unfocusedBorderColor = Color(0xFF777085),
        focusedLabelColor = gold,
        unfocusedLabelColor = muted,
        errorTextColor = Color.White,
        errorBorderColor = Color(0xFFFFB4AB),
        errorLabelColor = Color(0xFFFFB4AB),
        errorSupportingTextColor = Color(0xFFFFB4AB)
    )

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
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            TextButton(
                onClick = onBack,
                enabled = !isSaving,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = gold
                )
            ) {
                Text("← Back to shots")
            }

            Column {
                Text(
                    text = "SHORTLIST",
                    color = gold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isEditing) "Refine your shot." else "Plan your next shot.",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = projectTitle,
                    color = gold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Capture the details before the camera rolls.",
                    color = muted,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = sceneNumber,
                    onValueChange = { sceneNumber = it.take(6) },
                    label = { Text("Scene number") },
                    placeholder = { Text("1") },
                    enabled = !isSaving,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = attemptedSave && sceneInvalid,
                    supportingText = {
                        if (attemptedSave && sceneInvalid) {
                            Text("Enter a positive number")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors
                )

                OutlinedTextField(
                    value = shotOrder,
                    onValueChange = { shotOrder = it.take(6) },
                    label = { Text("Shot order") },
                    placeholder = { Text("1") },
                    enabled = !isSaving,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = attemptedSave && orderInvalid,
                    supportingText = {
                        if (attemptedSave && orderInvalid) {
                            Text("Enter a positive number")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it.take(200) },
                label = { Text("Shot description") },
                placeholder = {
                    Text("A character waits on an empty platform")
                },
                enabled = !isSaving,
                minLines = 3,
                maxLines = 5,
                isError = attemptedSave && descriptionInvalid,
                supportingText = {
                    Text(
                        text = if (attemptedSave && descriptionInvalid) {
                            "Enter a description"
                        } else {
                            "${description.length}/200"
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Shot type",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { menuExpanded = true },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = gold
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = shotType.ifBlank {
                                    "Choose a shot type"
                                }
                            )
                            Text("▾")
                        }
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color(0xFF302B45))
                    ) {
                        shotTypes.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = type,
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    shotType = type
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }

                if (attemptedSave && typeInvalid) {
                    Text(
                        text = "Choose a shot type",
                        color = Color(0xFFFFB4AB),
                        fontSize = 12.sp
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it.take(500) },
                label = { Text("Filming notes (optional)") },
                placeholder = {
                    Text("Lighting, camera movement, props or dialogue")
                },
                enabled = !isSaving,
                minLines = 3,
                maxLines = 6,
                supportingText = {
                    Text("${notes.length}/500")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors
            )

            if (!saveError.isNullOrBlank()) {
                Text(
                    text = saveError,
                    color = Color(0xFFFFB4AB),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    attemptedSave = true

                    if (canSubmit) {
                        onSave(
                            ShotFormData(
                                sceneNumber = requireNotNull(sceneValue),
                                shotOrder = requireNotNull(orderValue),
                                description = description.trim(),
                                shotType = shotType,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 17.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = gold,
                    contentColor = Color(0xFF171426)
                )
            ) {
                Text(
                    text = when {
                        isSaving -> "Saving..."
                        isEditing -> "Save changes"
                        else -> "Add shot"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = onBack,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = muted
                )
            ) {
                Text("Cancel")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditShotScreenPreview() {
    MaterialTheme {
        AddEditShotScreen(
            projectId = "sample-1",
            projectTitle = "The Last Train",
            onSave = {},
            onBack = {}
        )
    }
}