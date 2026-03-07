package com.davi.dev.scannermlkit.domain.pdf

import android.content.Context
import android.graphics.PathMeasure
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asAndroidPath
import com.davi.dev.scannermlkit.domain.model.SignatureData
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import java.io.File

sealed class PdfSaveResult {
    data class Success(val file: File) : PdfSaveResult()
    data class Error(val message: String, val exception: Throwable? = null) : PdfSaveResult()
}

object PdfBoxManager {

    fun saveWithPdfBox(
        context: Context,
        originalFile: File,
        paths: List<SignatureData>,
        viewSize: Size
    ): PdfSaveResult {
        if (viewSize.width <= 0f || viewSize.height <= 0f) {
            return PdfSaveResult.Error("Invalid view size")
        }

        if (!originalFile.exists()) {
            return PdfSaveResult.Error("Original file not found")
        }

        PDFBoxResourceLoader.init(context)

        var document: PDDocument? = null
        return try {
            document = PDDocument.load(originalFile)
            val page = document.getPage(0)
            val pdfWidth = page.mediaBox.width
            val pdfHeight = page.mediaBox.height

            // Scale factors to convert View coordinates to PDF coordinates
            val scaleX = pdfWidth / viewSize.width
            val scaleY = pdfHeight / viewSize.height

            PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true
            ).use { contentStream ->

                paths.forEach { item ->
                    contentStream.setStrokingColor(item.color.red, item.color.green, item.color.blue)

                    contentStream.setLineWidth(5f * scaleY * item.scale)

                    renderPathOnContentStream(
                        contentStream, item,
                        page.mediaBox, scaleX, scaleY
                    )
                }
            }

            val outputFile = File(context.filesDir, "output_${System.currentTimeMillis()}.pdf")
            document.save(outputFile)
            PdfSaveResult.Success(outputFile)

        } catch (e: Exception) {
            e.printStackTrace()
            PdfSaveResult.Error("Error processing PDF", e)
        } finally {
            document?.close()
        }
    }

    private fun renderPathOnContentStream(
        contentStream: PDPageContentStream,
        sigData: SignatureData,
        pageSize: PDRectangle,
        scaleX: Float,
        scaleY: Float
    ) {
        val pathMeasure = PathMeasure(sigData.path.asAndroidPath(), false)
        val pos = FloatArray(2) // Stores [x, y]
        val precision = 1f      // Defines the "smoothness" (1 pixel interval)

        do {
            val length = pathMeasure.length
            var distance = 0f
            var isFirstPoint = true

            while (distance <= length) {
                // Gets the (x, y) position at point 'distance' of the path
                pathMeasure.getPosTan(distance, pos, null)

                val rawX = pos[0]
                val rawY = pos[1]

                // 1. Applies user Offset and Scale (SignatureData)
                // 2. Applies conversion Scale for PDF (View -> PDF)
                val pdfX = (rawX * sigData.scale + sigData.offsetX - (sigData.width / 7f * sigData.scale)) * scaleX

                // 3. Inverts the Y-axis (PDF starts from bottom)
                val pdfY = pageSize.height - ((rawY * sigData.scale + sigData.offsetY - (sigData.height / 6.2f * sigData.scale)) * scaleY)

                if (isFirstPoint) {
                    contentStream.moveTo(pdfX, pdfY)
                    isFirstPoint = false
                } else {
                    contentStream.lineTo(pdfX, pdfY)
                }

                distance += precision
            }

            // Ensures the last point of the contour is drawn
            pathMeasure.getPosTan(length, pos, null)
            val finalX = (pos[0] * sigData.scale + sigData.offsetX - (sigData.width / 7f * sigData.scale)) * scaleX
            val finalY = pageSize.height - ((pos[1] * sigData.scale + sigData.offsetY - (sigData.height / 6.5f * sigData.scale)) * scaleY)
//            contentStream.lineTo(finalX, finalY)

        } while (pathMeasure.nextContour()) // Goes to the next contour if any

        contentStream.stroke()
    }
}