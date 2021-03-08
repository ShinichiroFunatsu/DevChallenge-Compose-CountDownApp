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
package com.example.androiddevchallenge.ui.keypad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.model.RawTime
import com.example.androiddevchallenge.ui.util.getOrZero
import com.example.androiddevchallenge.ui.util.pop
import com.example.androiddevchallenge.ui.util.push

@Composable
fun TimeKeyPad(modifier: Modifier = Modifier, onKeyClick: (RawTime) -> Unit = {}, onStartClick: () -> Unit) {
    val timeStack = remember { mutableStateListOf<Int>() }
    Column(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            TimeStack(
                modifier = Modifier,
                stack = timeStack,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.Backspace,
                contentDescription = "back icon",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = { timeStack.pop() })
                    .padding(4.dp)

            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.weight(1f))
        Divider()
        Spacer(modifier = Modifier.weight(1f))
        KeyPad(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            onClickKey = {
                if (timeStack.size < 6 && (timeStack.size != 0 || it != 0)) {
                    timeStack.push(it)
                    onKeyClick(RawTime.create(timeStack))
                }
            },
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            FloatingActionButton(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    onStartClick()
                    timeStack.clear()
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = "stat icon"
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun KeyPad(modifier: Modifier = Modifier, onClickKey: (Int) -> Unit) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            (1..3).forEach {
                CenterText(
                    text = "$it",
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable { onClickKey(it) },
                    style = MaterialTheme.typography.h5
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            (4..6).forEach {
                CenterText(
                    text = "$it",
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable { onClickKey(it) },
                    style = MaterialTheme.typography.h5
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            (7..9).forEach {
                CenterText(
                    text = "$it",
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable { onClickKey(it) },
                    style = MaterialTheme.typography.h5
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(Modifier.weight(1f))
            CenterText(
                text = "0",
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .clickable { onClickKey(0) },
                style = MaterialTheme.typography.h5
            )
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun CenterText(text: String, modifier: Modifier, style: TextStyle) {
    Box(modifier = modifier) {
        Text(text = text, modifier = Modifier.align(Alignment.Center), style = style)
    }
}

@Composable
private fun TimeStack(modifier: Modifier = Modifier, stack: List<Int>) {
    Row(modifier = modifier) {
        Text(
            text = "${stack.getOrZero(5)}${stack.getOrZero(4)}",
            modifier = Modifier.align(Alignment.Bottom),
            style = MaterialTheme.typography.h2
        )
        Text(
            text = "h",
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.Bottom),
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "${stack.getOrZero(3)}${stack.getOrZero(2)}",
            modifier = Modifier.align(Alignment.Bottom),
            style = MaterialTheme.typography.h2
        )
        Text(
            text = "m",
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.Bottom),
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "${stack.getOrZero(1)}${stack.getOrZero(0)}",
            modifier = Modifier.align(Alignment.Bottom),
            style = MaterialTheme.typography.h2
        )
        Text(
            text = "s",
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.Bottom),
            style = MaterialTheme.typography.h5
        )
    }
}
