package com.davi.dev.scannermlkit.domain.pdf

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import java.io.FileInputStream

object PdfManager {
    fun mergePdfs(inputPaths: List<String>, outputPath: String) {
        val writer = PdfWriter(outputPath)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc)

        for (path in inputPaths) {
            val reader = PdfReader(FileInputStream(path))
            val sourcePdf = PdfDocument(reader)
            sourcePdf.copyPagesTo(1, sourcePdf.numberOfPages, pdfDoc)
            sourcePdf.close()
        }

        document.close()
        pdfDoc.close()
    }

    fun splitPdfByPage(inputPath: String, outputDirectory: String, pageNumber: Int) {
        val reader = PdfReader(FileInputStream(inputPath))
        val sourcePdf = PdfDocument(reader)

        val writer = PdfWriter("$outputDirectory/page_$pageNumber.pdf")
        val newPdfDoc = PdfDocument(writer)

        sourcePdf.copyPagesTo(pageNumber, pageNumber, newPdfDoc)

        newPdfDoc.close()
        sourcePdf.close()
    }
}