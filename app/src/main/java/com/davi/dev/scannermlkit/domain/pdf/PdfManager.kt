package com.davi.dev.scannermlkit.domain.pdf

import android.util.Log
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.exceptions.BadPasswordException
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.EncryptionConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.ReaderProperties
import com.itextpdf.kernel.pdf.StampingProperties
import com.itextpdf.kernel.pdf.WriterProperties
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState
import com.itextpdf.layout.Canvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import java.io.InputStream
import java.io.OutputStream

object PdfManager {
    fun mergePdfs(inputStreams: List<InputStream>, outputStream: OutputStream) {
        val writer = PdfWriter(outputStream)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc)

        for (inputStream in inputStreams) {
            val reader = PdfReader(inputStream)
            val sourcePdf = PdfDocument(reader)
            sourcePdf.copyPagesTo(1, sourcePdf.numberOfPages, pdfDoc)
            sourcePdf.close()
            inputStream.close()
        }

        document.close()
        pdfDoc.close()
    }

    fun splitPdf(
        inputStream: InputStream,
        outputStream: OutputStream,
        startPage: Int,
        endPage: Int
    ) {
        val reader = PdfReader(inputStream)
        val sourcePdf = PdfDocument(reader)
        val writer = PdfWriter(outputStream)
        val newPdfDoc = PdfDocument(writer)

        sourcePdf.copyPagesTo(startPage, endPage, newPdfDoc)

        newPdfDoc.close()
        sourcePdf.close()
        inputStream.close()
        outputStream.close()
    }

    fun getPageCount(inputStream: InputStream): Int {
        val reader = PdfReader(inputStream)
        val pdfDoc = PdfDocument(reader)
        val count = pdfDoc.numberOfPages
        pdfDoc.close()
        inputStream.close()
        return count
    }

    fun protectPdf(
        inputStream: InputStream,
        outputStream: OutputStream, userPass: String, ownerPass: String
    ): Boolean {
        return try {
            val reader = PdfReader(inputStream)
            val props = WriterProperties()
                .setStandardEncryption(
                    userPass.toByteArray(),
                    ownerPass.toByteArray(),
                    EncryptionConstants.ALLOW_PRINTING,
                    EncryptionConstants.ENCRYPTION_AES_256
                )

            val writer = PdfWriter(outputStream, props)
            val pdfDoc = PdfDocument(reader, writer, StampingProperties())
            pdfDoc.close()
            Log.d("PdfManager", "PDF protected successfully!")
            true
        } catch (e: Exception) {
            Log.e("PdfManager", "PDF protected failed!", e)
            false
        }
    }

    fun compressPdf(
        inputStream: InputStream,
        outputStream: OutputStream,
        compressionLevel: Int
    ) {
        val reader = PdfReader(inputStream)
        val writerProperties = WriterProperties()
            .setCompressionLevel(compressionLevel)
            .setFullCompressionMode(true)

        val writer = PdfWriter(outputStream, writerProperties)
        val pdfDoc = PdfDocument(reader, writer)

        pdfDoc.close()
        reader.close()
    }

    fun isPdfEncrypted(inputStream: InputStream): Boolean {
        return try {
            val reader = PdfReader(inputStream)
            reader.use { reader ->
                PdfDocument(reader).use { pdfDoc ->
                    when {
                        reader.isEncrypted -> true
                        else -> false
                    }
                }
            }
        } catch (e: BadPasswordException) {
            Log.d("PdfManager", "isPdfEncrypted: true (BadPasswordException)")
            true
        } catch (e: Exception) {
            Log.e("PdfManager", "isPdfEncrypted error", e)
            false
        }
    }

    fun decryptPdf(
        inputStream: InputStream,
        outputStream: OutputStream,
        password: String
    ): Boolean {
        return try {
            val readerProperties = ReaderProperties().setPassword(password.toByteArray())
            val reader = PdfReader(inputStream, readerProperties)
            val writer = PdfWriter(outputStream)
            val pdfDoc = PdfDocument(reader, writer, StampingProperties())
            pdfDoc.close()
            true
        } catch (e: Exception) {
            Log.e("PdfManager", "decryptPdf error", e)
            false
        }
    }

    fun addWatermark(
        inputStream: InputStream,
        outputStream: OutputStream,
        text: String
    ) {
        val reader = PdfReader(inputStream)
        val writer = PdfWriter(outputStream)
        val pdfDoc = PdfDocument(reader, writer)

        val font = PdfFontFactory.createFont()
        val gs1 = PdfExtGState().setFillOpacity(0.5f)

        for (i in 1..pdfDoc.numberOfPages) {
            val page = pdfDoc.getPage(i)
            val pageSize = page.pageSize

            // Usar PdfCanvas diretamente para manipular o estado gráfico e as camadas do PDF
            val pdfCanvas = com.itextpdf.kernel.pdf.canvas.PdfCanvas(page.newContentStreamAfter(), page.resources, pdfDoc)

            // Canvas de layout do iText7 para facilitar o posicionamento do texto
            val canvas = Canvas(pdfCanvas, pageSize)
            canvas.setProperty(com.itextpdf.layout.properties.Property.FONT, font)
            canvas.setFontColor(ColorConstants.GRAY)
            canvas.setFontSize(60f)
            
            pdfCanvas.setExtGState(gs1)
            
            val p = Paragraph(text)
            
            // Repetição em malha cobrindo toda a área da página
            val stepX = 300f
            val stepY = 300f
            
            // Começa fora da página para garantir cobertura mesmo com a rotação de 45 graus
            var x = -pageSize.width
            while (x < pageSize.width * 3) {
                var y = -pageSize.height
                while (y < pageSize.height * 3) {
                    canvas.showTextAligned(
                        p, 
                        x, 
                        y, 
                        i, 
                        TextAlignment.CENTER, 
                        VerticalAlignment.MIDDLE,
                        Math.toRadians(315.0).toFloat()
                    )
                    y += stepY
                }
                x += stepX
            }
            canvas.close()
        }
        pdfDoc.close()
    }
}
