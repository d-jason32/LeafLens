package com.example.android_development_capstone.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.example.android_development_capstone.BottomNavBar
import com.example.android_development_capstone.R


@Composable
fun Cards(nav: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.secondary,
        bottomBar = { BottomNavBar(nav) }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(paddingValues)
        ) {
        var isExpanded by remember { mutableStateOf(false) }
        val transition = updateTransition(targetState = isExpanded, label = "Stack Expand")
        val scope = rememberCoroutineScope()
        val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }


        val blurRadius by transition.animateDp(label = "Title blur") { expanded ->
            if (expanded) 8.dp else 0.dp
        }
        Text(
            text = "Trading Cards",
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.nunito_bold)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .align(Alignment.TopCenter)
                .blur(blurRadius, BlurredEdgeTreatment.Unbounded)
        )


        val dragOffsetBottom = animateOffsetAsState(
            targetValue = offset.targetValue,
            spring(
                visibilityThreshold = Offset.VisibilityThreshold,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        BottomCard(
            transition = transition,
            modifier = Modifier
                .offset {
                    IntOffset(
                        dragOffsetBottom.value.x.roundToInt(),
                        dragOffsetBottom.value.y.roundToInt()
                    )
                }
                .align(Alignment.Center)
        )


        val dragOffsetMiddle = animateOffsetAsState(targetValue = offset.targetValue)

        MiddleCard(
            transition = transition,
            modifier = Modifier
                .offset {
                    IntOffset(
                        dragOffsetMiddle.value.x.roundToInt(),
                        dragOffsetMiddle.value.y.roundToInt()
                    )
                }
                .align(Alignment.Center)
        )


        val rotationXTop by transition.animateFloat(label = "RotationX top") { expanded ->
            if (expanded) 3f else 0f
        }

        TopCard(Modifier
            .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
            .align(Alignment.Center)
            .graphicsLayer(
                rotationX = rotationXTop,
                transformOrigin = TransformOriginTopCenter
            )
            .pointerInput(Unit) {
                scope.launch {
                    detectTapGestures {
                        isExpanded = !isExpanded
                    }
                }
                scope.launch {
                    detectDragGestures(
                        onDragStart = {
                            isExpanded = true
                        },
                        onDragEnd = {
                            scope.launch {
                                offset.animateTo(Offset.Zero, tween())
                            }
                            isExpanded = false
                        },
                        onDrag = { _, dragAmount ->
                            val original = offset.value
                            val summed = original + dragAmount
                            scope.launch {
                                offset.snapTo(summed)
                            }
                        }
                    )
                }
            })
    }
    }
}

@Composable
fun BottomCard(transition: Transition<Boolean>, modifier: Modifier = Modifier) {
    val rotation by transition.animateFloat(label = "Rotation bottom") { expanded ->
        if (expanded) 0f else 10f
    }
    val rotationX by transition.animateFloat(label = "RotationX bottom") { expanded ->
        if (expanded) 10f else 0f
    }
    val expandOffset by transition.animateDp(label = "Offset bottom") { expanded ->
        if (expanded) (-460).dp else (-40).dp
    }
    val scale by transition.animateFloat(label = "Scale bottom") { expanded ->
        if (expanded) 0.8f else 0.9f
    }

    Box(
        modifier = modifier
            .offset(y = expandOffset)
            .graphicsLayer(
                rotationX = rotationX,
                transformOrigin = TransformOriginTopCenter
            )
            .graphicsLayer(rotationZ = rotation, scaleX = scale, scaleY = scale)
            .requiredSize(340.dp, 220.dp)
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        PlantCardContent(
            commonName = "Water lily",
            scientificName = "Nymphaea spp.",
            imageRes = R.drawable.lily
        )
    }
}

@Composable
fun MiddleCard(transition: Transition<Boolean>, modifier: Modifier = Modifier) {
    val rotation by transition.animateFloat(label = "Rotation middle") { expanded ->
        if (expanded) 0f else 5f
    }
    val rotationX by transition.animateFloat(label = "RotationX middle") { expanded ->
        if (expanded) 5f else 0f
    }
    val expandOffset by transition.animateDp(label = "Offset middle") { expanded ->
        if (expanded) (-220).dp else (-20).dp
    }
    val scale by transition.animateFloat(label = "Scale middle") { expanded ->
        if (expanded) 0.9f else 0.95f
    }

    Box(
        modifier = modifier
            .offset(y = expandOffset)
            .graphicsLayer(
                rotationX = rotationX,
                transformOrigin = TransformOriginTopCenter
            )
            .graphicsLayer(rotationZ = rotation, scaleX = scale, scaleY = scale)
            .requiredSize(340.dp, 220.dp)
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        PlantCardContent(
            commonName = "Daisy",
            scientificName = "Bellis perennis",
            imageRes = R.drawable.daisy
        )
    }
}

@Composable
fun TopCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .requiredSize(340.dp, 220.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primary , RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        PlantCardContent(
            commonName = "Sunflower",
            scientificName = "Helianthus annuus",
            imageRes = R.drawable.sun
        )
    }
}

@Composable
fun PlantCardContent(
    commonName: String,
    scientificName: String,
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    val painter = rememberScaledPainter(imageRes)
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = commonName,
                modifier = Modifier.requiredSize(100.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = commonName,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = scientificName,
            color = Color.Black,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

val TransformOriginTopCenter: TransformOrigin = TransformOrigin(0.5f, 0f)

@Composable
private fun rememberScaledPainter(
    @DrawableRes imageRes: Int,
    maxSizePx: Int = 1024
): BitmapPainter {
    val context = LocalContext.current
    val bitmap = remember(imageRes) {
        // First decode bounds
        val optsBounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(context.resources, imageRes, optsBounds)
        val (outW, outH) = optsBounds.outWidth to optsBounds.outHeight
        var sample = 1
        if (outW > maxSizePx || outH > maxSizePx) {
            val halfW = outW / 2
            val halfH = outH / 2
            while ((halfW / sample) >= maxSizePx && (halfH / sample) >= maxSizePx) {
                sample *= 2
            }
        }
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        BitmapFactory.decodeResource(context.resources, imageRes, opts)
    }
    return BitmapPainter(bitmap.asImageBitmap())
}
