package com.davi.dev.scannermlkit.domain.pdf

import android.content.Context
import android.graphics.PathMeasure
import android.util.Log
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
            return PdfSaveResult.Error("Tamanho de visualização inválido")
        }

        if (!originalFile.exists()) {
            return PdfSaveResult.Error("Arquivo original não encontrado")
        }

        PDFBoxResourceLoader.init(context)

        var document: PDDocument? = null
        return try {
            document = PDDocument.load(originalFile)
            val page = document.getPage(0)
            val pdfWidth = page.mediaBox.width
            val pdfHeight = page.mediaBox.height

            // Fatores de escala para converter as coordenadas da View para as coordenadas do PDF
            val scaleX = pdfWidth / viewSize.width
            val scaleY = pdfHeight / viewSize.height

            Log.d("xing", "pdf Width -> $pdfWidth , Heitgh -> $pdfHeight")
            Log.d("xing", "viewsize width -> ${viewSize.width} , heighth -> ${viewSize.height}")
            Log.d("xing", "scaleX -> $scaleX , scaleY -> $scaleY")


            PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true
            ).use { contentStream ->

                paths.forEach { item ->
                    Log.d("xing", "item offset -> ${item.offsetX} - ${item.offsetY}")
                    Log.d("xing", "item scale -> ${item.scale}")
//                    Log.d("xing", "item scaleX multliply -> ${scaleX * item.scale}")
//                    Log.d("xing", "item scaleY multliply -> ${scaleY * item.scale}")

                    contentStream.setStrokingColor(0f, 0f, 0f)
//                    contentStream.setNonStrokingColor(item.color.red, item.color.green, item.color.blue)

                    // A espessura da linha também precisa ser escalada
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
            Log.d("xing", e.stackTraceToString())
            PdfSaveResult.Error("Erro ao processar PDF", e)
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
        val pos = FloatArray(2) // Armazena [x, y]
        val precision = 1f      // Define a "suavidade" (1 pixel de intervalo)

        Log.d("xing", "page width ${pageSize.width} = render wight /2-> ${(sigData.width * sigData.scale) / 2}")
        Log.d("xing", "page width ${pageSize.width} = render wight /3-> ${(sigData.width * sigData.scale) / 3}")
        Log.d("xing", "page height ${pageSize.height} = render heitgh /2-> ${(sigData.height * sigData.scale) / 2}")
        Log.d("xing", "page height ${pageSize.height} = render heitgh /3-> ${(sigData.height * sigData.scale) / 3}")

        Log.d("xing", "========================================================")

        // O PathMeasure pode conter múltiplos contornos (traços separados)
        do {
            val length = pathMeasure.length
            var distance = 0f
            var isFirstPoint = true

            while (distance <= length) {
                // Obtém a posição (x, y) no ponto 'distance' do path
                pathMeasure.getPosTan(distance, pos, null)

                val rawX = pos[0]
                val rawY = pos[1]

                // 1. Aplica o Offset e Escala do usuário (SignatureData)
                // 2. Aplica a Escala de conversão para o PDF (View -> PDF)
                val pdfX = (rawX * sigData.scale + sigData.offsetX - (sigData.width/7f * sigData.scale)) * scaleX

                // 3. Inversão do eixo Y (PDF começa de baixo)
                val pdfY = pageSize.height - ((rawY * sigData.scale + sigData.offsetY - (sigData.height/6.2f * sigData.scale)) * scaleY)

                if (isFirstPoint) {
                    contentStream.moveTo(pdfX, pdfY)
                    isFirstPoint = false
                } else {
                    contentStream.lineTo(pdfX, pdfY)
                }

                distance += precision
            }

//            contentStream.setStrokingColor(sigData.color.red, sigData.color.green, sigData.color.blue)


            // Garante que o último ponto do contorno seja desenhado
            pathMeasure.getPosTan(length, pos, null)
            val finalX = (pos[0] * sigData.scale + sigData.offsetX - (sigData.width/7f * sigData.scale)) * scaleX
            val finalY = pageSize.height - ((pos[1] * sigData.scale + sigData.offsetY - (sigData.height/6.5f * sigData.scale)) * scaleY)
//            contentStream.lineTo(finalX, finalY)

        } while (pathMeasure.nextContour()) // Vai para o próximo traço se houver

        contentStream.stroke()
    }
}