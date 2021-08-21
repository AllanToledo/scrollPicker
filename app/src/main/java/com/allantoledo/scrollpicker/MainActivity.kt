package com.allantoledo.scrollpicker

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allantoledo.scrollpicker.ui.theme.ScrollPickerTheme
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScrollPickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        scrollPicker(applicationContext){
                            Log.v("SCROLLPICKER", it.toString())
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun scrollPicker(context: Context, onSelect: (Int) -> Unit) {
        var offset by remember { mutableStateOf(0f) }
        var position by remember { mutableStateOf(0) }
        val scrollState = rememberScrollState()

        val heightInDp = 23
        val heightInPx = convertDpToPixel(23f, context)

        val itemsCount = 10

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.KeyboardArrowUp, "")

            Column(
                modifier = Modifier
                    .size(width = 50.dp, height = (heightInDp * 3).dp)
                    .verticalScroll(scrollState)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                position = (offset / heightInPx).roundToInt()
                                onSelect(position)
                                offset = position * heightInPx
                                runBlocking { scrollState.scrollTo(offset.toInt()) }

                            },
                            onDrag = { _, dragAmount ->
                                offset -= dragAmount.y
                                if (offset < 0)
                                    offset = 0f
                                if (offset > scrollState.maxValue)
                                    offset = scrollState.maxValue.toFloat()

                                position = (offset / heightInPx).roundToInt()


                                runBlocking {
                                    scrollState.scrollTo(offset.toInt())
                                }
                            }
                        )
                    }
            ) {
                Spacer(modifier = Modifier.size(height = heightInDp.dp, width = 50.dp))
                repeat(itemsCount) { item ->
                    Text(
                        "$item",
                        modifier = Modifier
                            .size(height = heightInDp.dp, width = 50.dp)
                            .alpha(if (item == position) 1f else 0.5f),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.size(height = heightInDp.dp, width = 50.dp))
            }

            Icon(Icons.Filled.KeyboardArrowDown, "")
        }
    }
}

fun convertDpToPixel(dp: Float, context: Context): Float {
    return dp * context.resources.displayMetrics.density
}

