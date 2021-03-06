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
package com.example.androiddevchallenge.model

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import com.example.androiddevchallenge.ui.util.getOrZero

data class RawTime(val hours: Int, val minutes: Int, val seconds: Int) {
    val totalSeconds: Long
        get() {
            return (hours * 60 + minutes) * 60 + seconds.toLong()
        }

    companion object {
        fun create(stack: List<Int>): RawTime {
            return RawTime(
                stack.getOrZero(5) * 10 + stack.getOrZero(4),
                stack.getOrZero(3) * 10 + stack.getOrZero(2),
                stack.getOrZero(1) * 10 + stack.getOrZero(0),
            )
        }
    }
}

class Time(hours: State<Int>, minutes: State<Int>, seconds: State<Int>) {
    companion object {
        const val TIME_FORMAT = "%d:%02d:%02d"
    }
    val hours by hours
    val minutes by minutes
    val seconds by seconds
    val totalSeconds: Long
        get() {
            Log.d("abab", "h=$hours, m=$minutes, s=$seconds")
            return (hours * 60 + minutes) * 60 + seconds.toLong()
        }

    override fun toString(): String = String.format(TIME_FORMAT, hours, minutes, seconds)
}
