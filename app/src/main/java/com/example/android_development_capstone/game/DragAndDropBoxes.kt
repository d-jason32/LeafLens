@file:OptIn(ExperimentalFoundationApi::class)

package com.example.android_development_capstone.game

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.ui.res.painterResource
import com.example.android_development_capstone.R


@Composable
fun Game1(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(true) }
    var position by remember { mutableStateOf(IntOffset(300, 300)) }
    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = modifier
                .fillMaxWidth()
                .weight(0.2f)
        ) {
            val boxCount = 4
            var dragBoxIndex by remember { mutableIntStateOf(0) }

            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(10.dp)
                        .border(1.dp, Color.Black)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event.mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        isPlaying = !isPlaying
                                        position = if (isPlaying) {
                                            IntOffset(300, 300)
                                        } else {
                                            IntOffset(130, 100)
                                        }
                                        dragBoxIndex = index
                                        return true
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    this@Row.AnimatedVisibility(
                        visible = index == dragBoxIndex,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            modifier = Modifier
                                .fillMaxSize()
                                .dragAndDropSource { _ ->
                                    DragAndDropTransferData(
                                        clipData = ClipData.newPlainText("text", "")
                                    )
                                }
                        )
                    }
                }
            }
        }

        val pOffset by animateIntOffsetAsState(
            targetValue = position,
            animationSpec = tween(3000, easing = LinearEasing)
        )

        val rtatView by animateFloatAsState(
            targetValue = if (isPlaying) 360f else 0f,
            animationSpec = repeatable(
                iterations = if (isPlaying) 10 else 1,
                tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plant),
                contentDescription = "Plant",
                modifier = Modifier
                    .padding(10.dp)
                    .offset { pOffset }
                    .rotate(rtatView)
                    .size(100.dp)
            )

            MyButton(
                onReset = {
                    position = IntOffset(400, 100)
                    isPlaying = !isPlaying
                }
            )
        }
    }
}

@Composable
fun MyButton(onReset: () -> Unit) {
    Button(
        onClick = onReset,
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Reset object")
    }
}