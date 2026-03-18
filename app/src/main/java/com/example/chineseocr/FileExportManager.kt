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

        for (i in textList.indices) {
            val row: Row = sheet.createRow(i)
            val cell: Cell = row.createCell(0)
            cell.setCellValue(textList[i])
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
}
