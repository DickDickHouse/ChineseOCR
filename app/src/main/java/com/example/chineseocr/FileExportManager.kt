package com.example.chineseocr

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class FileExportManager {

    fun saveAsTxt(text: String, filePath: String): Boolean {
        return try {
            val file = prepareFile(filePath)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(text.toByteArray(StandardCharsets.UTF_8))
                outputStream.flush()
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun saveAsXlsx(textList: List<String>, filePath: String): Boolean {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Data")
        val tableRows = textList.flatMap { parseRowsForSpreadsheet(it) }

        for (i in tableRows.indices) {
            val row: Row = sheet.createRow(i)
            val columns = tableRows[i]
            for (j in columns.indices) {
                val cell: Cell = row.createCell(j)
                cell.setCellValue(columns[j])
            }
        }

        if (tableRows.isNotEmpty()) {
            val columnCount = tableRows.maxOf { it.size }
            for (columnIndex in 0 until columnCount) {
                sheet.autoSizeColumn(columnIndex)
            }
        }

        return try {
            FileOutputStream(prepareFile(filePath)).use { outputStream ->
                workbook.write(outputStream)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            workbook.close()
        }
    }

    fun saveAsDoc(text: String, filePath: String): Boolean {
        return try {
            val file = prepareFile(filePath)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(text.toByteArray(StandardCharsets.UTF_8))
                outputStream.flush()
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun prepareFile(filePath: String): File {
        return File(filePath).apply {
            parentFile?.mkdirs()
        }
    }

    private fun parseRowsForSpreadsheet(text: String): List<List<String>> {
        val parsedRows = text
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filterNot { isOutlineSeparator(it) }
            .map { line ->
                when {
                    line.contains('\t') -> line.split('\t').map { it.trim() }
                    hasTablePipe(line) -> line.split(Regex("[|│┃║]"))
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    Regex("\\s{2,}").containsMatchIn(line) -> line.split(Regex("\\s{2,}"))
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    else -> listOf(line)
                }
            }
            .toList()

        return if (parsedRows.isEmpty()) {
            listOf(listOf(text))
        } else {
            parsedRows
        }
    }

    private fun hasTablePipe(line: String): Boolean {
        val pipeCount = line.count { it == '|' || it == '│' || it == '┃' || it == '║' }
        return pipeCount >= 2
    }

    private fun isOutlineSeparator(line: String): Boolean {
        val outlineChars = Regex("^[+\\-=_│┃║┌┐└┘├┤┬┴┼─═]+$")
        return outlineChars.matches(line)
    }
}
