/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.countdown

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.model.RawTime
import com.example.androiddevchallenge.model.Time
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CountDown(
    defaultRawTime: RawTime,
    modifier: Modifier = Modifier,
    onSetTimeClick: () -> Unit = {}
) {
    var totalTime by remember { mutableStateOf(defaultRawTime.totalSeconds) }
    var startTime by remember { mutableStateOf(0L) }
    var timeLeft by remember { mutableStateOf(totalTime) }
    val seconds = remember { mutableStateOf(0) }
    val minutes = remember { mutableStateOf(0) }
    val hours = remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isActive && timeLeft > 0 && isRunning) {
            withFrameMillis {
                if (!isRunning) return@withFrameMillis
                if (startTime == 0L) {
                    startTime = it / 1000
                }
                timeLeft = totalTime - (it / 1000 - startTime)
                if (timeLeft < 0) {
                    isRunning = false
                } else {
                    seconds.value = timeLeft.toInt() % 60
                    minutes.value = (timeLeft - seconds.value).toInt() / 60 % 60
                    hours.value = (timeLeft - seconds.value - minutes.value * 60).toInt() / 3600
                }
            }
        }
    }

    val time by remember { mutableStateOf(Time(hours, minutes, seconds)) }

    CountDown(
        time = time,
        modifier = modifier,
        isRunning = isRunning,
        onStartClick = {
            isRunning = true
        },
        onStopClick = {
            isRunning = false
            startTime = 0L
            totalTime = timeLeft
        },
        onSetTimeClick = {
            isRunning = false
            onSetTimeClick()
        }
    )
}

@Composable
private fun CountDown(
    time: Time,
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onSetTimeClick: () -> Unit
) {
    Column(modifier = modifier) {
        ClockDial(
            time = time,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            SetButton(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                onClick = onSetTimeClick
            )
            StartOrStopFab(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterVertically),
                isRunning = isRunning,
                onStopClick = onStopClick,
                onStartClick = onStartClick
            )
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun SetButton(modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(46.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = "SET",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
private fun StartOrStopFab(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    onStopClick: () -> Unit,
    onStartClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier.padding(12.dp),
        onClick = {
            if (isRunning) {
                onStopClick()
            } else {
                onStartClick()
            }
        }
    ) {
        Icon(
            imageVector = if (isRunning) Icons.Outlined.Stop else Icons.Outlined.PlayArrow,
            contentDescription = if (isRunning) "stop fab" else "start fab"
        )
    }
}

@Composable
private fun ClockDial(time: Time, modifier: Modifier) {
    Box(modifier = modifier) {
        ArcNumberList(9, time.seconds % 10, 310f, 10f)
        ArcNumberList(5, time.seconds / 10, 280f, 10f)
        ArcNumberList(9, time.minutes % 10, 240f, 10f)
        ArcNumberList(5, time.minutes / 10, 210f, 10f)
        ArcNumberList(9, time.hours % 10, 170f, 10f)
        ArcNumberList(9, time.hours / 10, 140f, 10f)
    }
}

private const val moveDuration = 700

private data class DigitItem(val value: Int, val x: Float, val y: Float, val angle: Float)

@Composable
private fun ArcNumberList(
    maxDigit: Int,
    digit: Int,
    radius: Float,
    intervalAngle: Float,
    startOffsetAngle: Float = -9 * intervalAngle,
    offsetY: Float = 200f
) {
    require(intervalAngle * maxDigit < 360)

    val offsetAngle: Float = animateFloatAsState(
        targetValue = ((9 - digit) * intervalAngle),
        animationSpec = tween(moveDuration),
    ).value

    var items: List<DigitItem> by remember { mutableStateOf(listOf()) }
    var digitItem: DigitItem by remember { mutableStateOf(DigitItem(0, 0f, 0f, 0f)) }

    LaunchedEffect(offsetAngle) {
        val intervalRadian = (intervalAngle * PI / 180.0f)
        val offsetRadian = (offsetAngle * PI / 180.0f)
        val startOffsetRadian = (startOffsetAngle * PI / 180.0f)
        items = (0..maxDigit).mapIndexed { index, i ->
            val x = cos(startOffsetRadian + offsetRadian + intervalRadian * index) * radius
            val y = offsetY + sin(startOffsetRadian + offsetRadian + intervalRadian * index) * radius
            DigitItem(i, x.toFloat(), y.toFloat(), startOffsetAngle + offsetAngle + intervalAngle * i).also {
                if (index == digit) digitItem = it
            }
        }
    }
    Box {
        val c = MaterialTheme.colors.secondary.copy(alpha = 0.6f)
        Canvas(modifier = Modifier.size(24.dp, 24.dp)) {
            drawRoundRect(
                color = c,
                size = Size(24.dp.toPx(), 24.dp.toPx()),
                topLeft = Offset((digitItem.x - 7.5f).dp.toPx(), (digitItem.y - 1f).dp.toPx()),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
            )
        }
        items.forEach {
            Text(
                text = "${it.value}",
                modifier = Modifier
                    .offset { IntOffset((it.x * density).toInt(), (it.y * density).toInt()) }
                    .rotate(it.angle),
                color = if (it.value == digit) contentColorFor(MaterialTheme.colors.secondary) else Color.Unspecified
            )
        }
    }
}
