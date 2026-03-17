import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import java.nio.file.Files
import java.nio.file.Paths

class FileExportManager {

    fun saveAsTxt(text: String, filePath: String) {
        try {
            val file = File(filePath)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(text.toByteArray(StandardCharsets.UTF_8))
                outputStream.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveAsXlsx(textList: List<String>, filePath: String) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Data")

        for (i in textList.indices) {
            val row: Row = sheet.createRow(i)
            val cell: Cell = row.createCell(0)
            cell.setCellValue(textList[i])
        }

        try {
            FileOutputStream(filePath).use { outputStream ->
                workbook.write(outputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            workbook.close()
        }
    }

    fun saveAsDoc(text: String, filePath: String) {
        try {
            val docFile = File(filePath)
            Files.write(Paths.get(docFile.toURI()), text.toByteArray(StandardCharsets.UTF_8))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}