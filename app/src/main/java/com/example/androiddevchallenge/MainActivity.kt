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
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.model.RawTime
import com.example.androiddevchallenge.ui.bottomsheet.FakeBottomSheet
import com.example.androiddevchallenge.ui.bottomsheet.FakeBottomSheetValue
import com.example.androiddevchallenge.ui.countdown.CountDown
import com.example.androiddevchallenge.ui.keypad.TimeKeyPad
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        var timeSetting by remember { mutableStateOf(RawTime(0, 0, 0)) }
        var isStartCountDown by remember { mutableStateOf(false) }
        val sheetValue = remember { mutableStateOf(FakeBottomSheetValue.Hidden) }
        FakeBottomSheet(
            sheetContent = {
                TimeKeyPad(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxSize(),
                    onSettingTimeChanged = { time ->
                        timeSetting = time
                    },
                    onStartClick = {
                        sheetValue.value = FakeBottomSheetValue.Hidden
                        isStartCountDown = true
                    }
                )
            },
            sheetValue = sheetValue,
        ) {
            CountDown(
                defaultRawTime = timeSetting,
                modifier = Modifier
                    .fillMaxSize(),
                startCountDown = isStartCountDown,
                onSetTimeClick = {
                    isStartCountDown = false
                    sheetValue.value = FakeBottomSheetValue.Show
                    timeSetting = RawTime(0, 0, 0)
                }
            )
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
