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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.model.RawTime
import com.example.androiddevchallenge.model.Time
import kotlinx.coroutines.isActive

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
        Box(
            modifier = modifier
                .weight(1f)
        ) {
            Text(
                text = "$time",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.h2
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .height(46.dp)
                    .padding(horizontal = 12.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = onSetTimeClick)
            ) {
                Text(
                    text = "SET",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.h6
                )
            }
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
