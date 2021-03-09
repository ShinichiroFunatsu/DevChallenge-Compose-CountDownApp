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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

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
    // 3000f means out of bottom on screen to avoid initializing animation.
    var fullHeight: Float by remember { mutableStateOf(3000f) }
    var sheetHeight: Float by remember { mutableStateOf(3000f) }
    val offset by animateFloatAsState(
        targetValue = if (sheetValue.value == FakeBottomSheetValue.Hidden) fullHeight else sheetHeight,
        animationSpec = TweenSpec()
    )
    FakeBottomSheetStack(
        modifier = modifier,
        sheetContent = {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, offset.roundToInt()) },
                shape = sheetShape,
                elevation = 16.dp,
                color = MaterialTheme.colors.surface,
                contentColor = contentColorFor(MaterialTheme.colors.surface)
            ) {
                Column(content = sheetContent)
            }
        },
        content = { constraints, sheetH ->
            fullHeight = constraints.maxHeight.toFloat()
            sheetHeight = sheetH
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
        }
    )
}

@Composable
private fun FakeBottomSheetStack(
    modifier: Modifier,
    sheetContent: @Composable () -> Unit,
    content: @Composable (constraints: Constraints, sheetHeight: Float) -> Unit
) {
    SubcomposeLayout(modifier) { constraints ->
        val sheetPlaceable =
            subcompose(FakeBottomSheetStackSlot.SheetContent, sheetContent)
                .first().measure(constraints.copy(minWidth = 0, minHeight = 0))

        val sheetHeight = sheetPlaceable.height.toFloat()

        val placeable =
            subcompose(FakeBottomSheetStackSlot.Content) { content(constraints, sheetHeight) }
                .first().measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
            sheetPlaceable.placeRelative(0, 0)
        }
    }
}

private enum class FakeBottomSheetStackSlot { SheetContent, Content }

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
