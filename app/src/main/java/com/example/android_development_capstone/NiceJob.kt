package com.example.android_development_capstone

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun NiceJob() {
    Image(painter = painterResource(id = R.drawable.nice_job),
        contentDescription = null,
        Modifier.size(200.dp)
    )
}