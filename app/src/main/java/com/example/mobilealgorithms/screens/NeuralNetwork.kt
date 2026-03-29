package com.example.mobilealgorithms.screens

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

private fun distToLine(x: Int, y: Int, x1: Int, y1: Int, x2: Int, y2: Int) : Double {
    val line = (y2 - y1) * (x - x1) - (x2 - x1) * (y - y1)
    val denominatorSquared = (y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)
    return abs(line.toDouble()) / sqrt(denominatorSquared.toDouble())
}

@Composable
private fun TilePlot(
    modifier: Modifier = Modifier,
    rows: Int,
    cols: Int,
    trigger: Any,
    onChanged: (List<List<Int>>) -> Unit = {}
) {
    val matrix = remember(trigger) {
        mutableStateListOf<List<Int>>().apply {
            repeat(rows) { add(List(cols) { 0 }) }
        }
    }
    LaunchedEffect(trigger) {
        onChanged(matrix)
    }
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize().pointerInput(matrix) {
            var oldTileCol: Int? = null
            var oldTileRow: Int? = null
            detectDragGestures(
                onDragEnd = {
                    oldTileCol = null
                    oldTileRow = null
                }
            ) { change, offset ->
                change.consume()
                val position = change.position
                val tileCol = (cols * position.x / size.width).toInt().coerceIn(0, cols - 1)
                val tileRow = (rows * position.y / size.height).toInt().coerceIn(0, rows - 1)
                if (oldTileRow != null && oldTileCol != null &&
                    (oldTileRow != tileRow || oldTileCol != tileCol))
                {
                    val minRow = min(oldTileRow!!, tileRow)
                    val maxRow = max(oldTileRow!!, tileRow)
                    val minCol = min(oldTileCol!!, tileCol)
                    val maxCol = max(oldTileCol!!, tileCol)
                    for (row in minRow until maxRow + 1) {
                        for (col in minCol until maxCol + 1) {
                            if (distToLine(row, col, oldTileRow!!, oldTileCol!!, tileRow, tileCol) < 0.75) {
                                if (matrix[row][col] == 0) {
                                    val newRow = matrix[row].toMutableList()
                                    newRow[col] = 1
                                    matrix[row] = newRow
                                    onChanged(matrix)
                                }
                            }
                        }
                    }
                } else {
                    if (matrix[tileRow][tileCol] == 0) {
                        val newRow = matrix[tileRow].toMutableList()
                        newRow[tileCol] = 1
                        matrix[tileRow] = newRow
                        onChanged(matrix)
                    }
                }
                oldTileCol = tileCol
                oldTileRow = tileRow
            }
        }) {
            val tileWidth = size.width / cols
            val tileHeight = size.height / rows
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val color = if (matrix[row][col] == 1) Color.Black else Color.White
                    drawRect(
                        color = color,
                        topLeft = Offset(col * tileWidth, row * tileHeight),
                        size = Size(tileWidth, tileHeight)
                    )
                    drawRect(
                        color = Color.DarkGray,
                        topLeft = Offset(col * tileWidth, row * tileHeight),
                        size = Size(tileWidth, tileHeight),
                        style = Stroke(width = 1f)
                    )
                }
            }
        }
    }
}
@Composable
fun NeuralNetwork() {
    var gridMatrix by remember { mutableStateOf<List<List<Int>>>(emptyList()) }
    var number by remember { mutableStateOf("") }
    var matrixTrigger by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column {
        TilePlot(modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(16.dp),
            rows = 50,
            cols = 50,
            trigger = matrixTrigger,
            onChanged = { matrix -> gridMatrix = matrix }
        )
        Row(modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = number,
                onValueChange = {
                    if (it.length <= 1 && it.all { char -> char.isDigit() }) number = it
                },
                label = { Text("Label:") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (number.isNotEmpty()) {
                    scope.launch {
                        saveMatrixToDownloads(context, gridMatrix, number)
                        matrixTrigger++
                        number = ""
                    }
                }
            }) {
                Text("Save")
            }
        }
    }
}

fun saveMatrixToDownloads(context: Context, matrix: List<List<Int>>, number: String) {
    val csv = matrix.joinToString("\n") { row ->
        row.joinToString(",")
    }
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "${number}_${System.currentTimeMillis()}.csv")
        put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }
    val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
    uri?.let { resolver.openOutputStream(it)?.use { it.write(csv.toByteArray()) } }
}
