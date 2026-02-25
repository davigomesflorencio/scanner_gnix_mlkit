package com.davi.dev.scannermlkit.domain.extensions

import android.graphics.RectF

fun normalizePath(originalPath: android.graphics.Path): android.graphics.Path {
    val bounds = RectF()
    originalPath.computeBounds(bounds, true)

    val matrix = android.graphics.Matrix()
    // Move o path para que o topo-esquerdo dele seja 0,0
    matrix.setTranslate(-bounds.left, -bounds.top)

    val normalizedPath = android.graphics.Path()
    originalPath.transform(matrix, normalizedPath)
    return normalizedPath
}