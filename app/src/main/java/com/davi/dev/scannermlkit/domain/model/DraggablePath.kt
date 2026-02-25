package com.davi.dev.scannermlkit.domain.model

import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.geometry.Offset
import java.util.UUID

data class DraggablePath(
    val id: UUID = UUID.randomUUID(),
    val path: Path,
    var offset: Offset = Offset.Zero,
    var isSelected: Boolean = false
) {
    // Verifica se o ponto tocado está dentro dos limites do Path
    fun contains(point: Offset): Boolean {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        // Ajusta o retângulo para a posição atual do arraste
        rectF.offset(offset.x, offset.y)
        return rectF.contains(point.x, point.y)
    }
}