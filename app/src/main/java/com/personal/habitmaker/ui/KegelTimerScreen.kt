package com.personal.habitmaker.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KegelTimerScreen(
    holdTimeSeconds: Int = 5,
    relaxTimeSeconds: Int = 5,
    totalSets: Int = 10,
    onBack: () -> Unit = {}
) {
    var isRunning by remember { mutableStateOf(false) }
    var currentSet by remember { mutableStateOf(1) }
    var timeRemaining by remember { mutableStateOf(holdTimeSeconds) }
    var isHoldPhase by remember { mutableStateOf(true) }

    // Timer logic
    LaunchedEffect(isRunning) {
        while (isRunning && currentSet <= totalSets) {
            delay(1000L)
            timeRemaining--

            if (timeRemaining <= 0) {
                if (isHoldPhase) {
                    // Switch to relax phase
                    isHoldPhase = false
                    timeRemaining = relaxTimeSeconds
                } else {
                    // Move to next set
                    currentSet++
                    isHoldPhase = true
                    timeRemaining = holdTimeSeconds
                }
            }

            if (currentSet > totalSets) {
                isRunning = false
            }
        }
    }

    val progress = if (isHoldPhase) {
        timeRemaining.toFloat() / holdTimeSeconds
    } else {
        timeRemaining.toFloat() / relaxTimeSeconds
    }

    val progressColor by animateColorAsState(
        targetValue = if (isHoldPhase) Color(0xFF4CAF50) else Color(0xFF2196F3),
        animationSpec = tween(durationMillis = 500),
        label = "progress_color"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kegel Timer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Phase indicator
            Text(
                text = if (isHoldPhase) "Hold Phase" else "Relax Phase",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isHoldPhase) Color(0xFF4CAF50) else Color(0xFF2196F3),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Circular Progress with Timer
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Background circle
                Surface(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape),
                    color = Color(0xFFEEEEEE)
                ) {}

                // Progress circle using Canvas
                Canvas(modifier = Modifier.size(250.dp)) {
                    val radius = size.width / 2
                    val strokeWidth = 8.dp.toPx()
                    
                    // Draw progress arc
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            strokeWidth / 2,
                            strokeWidth / 2
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            size.width - strokeWidth,
                            size.height - strokeWidth
                        )
                    )
                }

                // Center content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timeRemaining.toString(),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "seconds",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Set counter
            Text(
                text = "Set $currentSet / $totalSets",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)
            )

            // Control buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Start/Pause button
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) Color(0xFFFF9800) else Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isRunning) "Pause" else "Start",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Reset button
                Button(
                    onClick = {
                        isRunning = false
                        currentSet = 1
                        isHoldPhase = true
                        timeRemaining = holdTimeSeconds
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Reset",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Completion message
            if (currentSet > totalSets && !isRunning) {
                Text(
                    text = "🎉 Workout Complete!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
    }
}
