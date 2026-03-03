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
                    Log.d("xing", "item scaleX multliply -> ${scaleX * item.scale}")
                    Log.d("xing", "item scaleY multliply -> ${scaleY * item.scale}")

                    contentStream.setStrokingColor(0, 0, 0)
                    // A espessura da linha também precisa ser escalada
                    contentStream.setLineWidth(5f * scaleY * item.scale)

                    renderPathOnContentStream(
                        contentStream, item,
                        page.mediaBox, scaleX, scaleY
                    )

//                    val it = item.path.iterator()
//
//                    while (it.hasNext()) {
//                        val segment = it.next()
//
//                        // Função para converter uma coordenada X da Path para a coordenada do PDF
//                        fun toPdfX(x: Float): Float {
//                            val viewX = x * item.scale + item.offsetX - (item.width * item.scale)/2f
//                            return viewX * scaleX
//                        }
//
//                        // Função para converter uma coordenada Y da Path para a coordenada do PDF
//                        // Inclui a inversão do eixo Y (origem no canto inferior esquerdo)
//                        fun toPdfY(y: Float): Float {
//                            val viewY = y * item.scale + item.offsetY - (item.height * item.scale)/2f
//                            return pdfHeight - (viewY * scaleY)
//                        }
//
//                        when (segment.type) {
//                            PathSegment.Type.Move -> {
//                                val x = toPdfX(segment.points[0])
//                                val y = toPdfY(segment.points[1])
//                                contentStream.moveTo(x, y)
//                            }
//
//                            PathSegment.Type.Line -> {
//                                val x = toPdfX(segment.points[0])
//                                val y = toPdfY(segment.points[1])
//                                contentStream.lineTo(x, y)
//                            }
//
//                            PathSegment.Type.Quadratic -> {
//                                val controlX = toPdfX(segment.points[0])
//                                val controlY = toPdfY(segment.points[1])
//                                val endX = toPdfX(segment.points[2])
//                                val endY = toPdfY(segment.points[3])
////                                contentStream.curveTo1(controlX, controlY, endX, endY)
//                            }
//
//                            PathSegment.Type.Cubic -> {
//                                val control1X = toPdfX(segment.points[0])
//                                val control1Y = toPdfY(segment.points[1])
//                                val control2X = toPdfX(segment.points[2])
//                                val control2Y = toPdfY(segment.points[3])
//                                val endX = toPdfX(segment.points[4])
//                                val endY = toPdfY(segment.points[5])
//                                contentStream.curveTo(control1X, control1Y, control2X, control2Y, endX, endY)
//                            }
//
//                            PathSegment.Type.Close -> {
//                                contentStream.closePath()
//                            }
//
//                            PathSegment.Type.Done -> {
//                                // Fim do caminho, não faz nada
//                            }
//
//                            PathSegment.Type.Conic -> {
//
//                            }
//                        }
//                    }
//                    contentStream.stroke()
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

        Log.d("xing","render wight /2-> ${(sigData.width * sigData.scale)/2}")
        Log.d("xing","render wight /3-> ${(sigData.width * sigData.scale)/3}")
        Log.d("xing","render heitgh /2-> ${(sigData.height * sigData.scale)/2}")
        Log.d("xing","render heitgh /3-> ${(sigData.height * sigData.scale)/3}")


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
                val pdfX = (rawX * sigData.scale + sigData.offsetX - (sigData.width/2 * sigData.scale)) * scaleX

                // 3. Inversão do eixo Y (PDF começa de baixo)
                val pdfY = pageSize.height - ((rawY * sigData.scale + sigData.offsetY - (sigData.height/2 * sigData.scale)) * scaleY)

                if (isFirstPoint) {
                    contentStream.moveTo(pdfX, pdfY)
                    isFirstPoint = false
                } else {
                    contentStream.lineTo(pdfX, pdfY)
                }

                distance += precision
            }

            // Garante que o último ponto do contorno seja desenhado
            pathMeasure.getPosTan(length, pos, null)
            val finalX = (pos[0] * sigData.scale + sigData.offsetX - (sigData.width/3 * sigData.scale)) * scaleX
            val finalY = pageSize.height - ((pos[1] * sigData.scale + sigData.offsetY -  (sigData.height * sigData.scale)) * scaleY)
            contentStream.lineTo(finalX, finalY)

        } while (pathMeasure.nextContour()) // Vai para o próximo traço se houver

        contentStream.stroke()
    }
}