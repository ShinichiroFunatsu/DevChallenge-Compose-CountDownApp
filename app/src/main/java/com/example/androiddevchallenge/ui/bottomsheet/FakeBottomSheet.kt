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
package com.example.androiddevchallenge.ui.bottomsheet

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

enum class FakeBottomSheetValue {
    Hidden, Show,
}

@Composable
fun FakeBottomSheet(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    sheetValue: MutableState<FakeBottomSheetValue>,
    sheetShape: Shape = MaterialTheme.shapes.large,
    scrimColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.32f),
    content: @Composable () -> Unit
) {
    val weight by animateFloatAsState(
        targetValue = if (sheetValue.value == FakeBottomSheetValue.Hidden) 1f else 1000f,
        animationSpec = TweenSpec()
    )
    Box(modifier = modifier) {
        // content
        Box(Modifier.fillMaxSize()) {
            content()
            Scrim(
                color = scrimColor,
                onDismiss = {
                    sheetValue.value = FakeBottomSheetValue.Hidden
                },
                visible = sheetValue.value != FakeBottomSheetValue.Hidden
            )
        }
        // sheet content
        Column(Modifier.fillMaxSize()) {
            // FIXME use offset instead of weight cheating.
            Spacer(modifier = Modifier.weight(2000 - weight))
            Surface(
                Modifier
                    .fillMaxWidth()
                    .weight(weight),
                shape = sheetShape,
                elevation = 16.dp,
                color = MaterialTheme.colors.surface,
                contentColor = contentColorFor(MaterialTheme.colors.surface)
            ) {
                Column(content = sheetContent)
            }
        }
    }
}

// ModalBottomSheet's Scrim
@Composable
private fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    if (color != Color.Transparent) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec()
        )
        val dismissModifier = if (visible) {
            Modifier.pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
        } else {
            Modifier
        }
        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}
