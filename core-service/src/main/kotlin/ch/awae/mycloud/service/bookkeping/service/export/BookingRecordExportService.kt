package ch.awae.mycloud.service.bookkeping.service.export

import ch.awae.mycloud.service.bookkeping.model.*
import ch.awae.mycloud.service.bookkeping.service.*
import jakarta.transaction.*
import org.apache.poi.ss.util.*
import org.apache.poi.xssf.usermodel.*
import org.springframework.data.domain.*
import org.springframework.stereotype.*
import java.io.*
import java.math.*


@Service
@Transactional
class BookingRecordExportService(
    private val bookService: BookService,
    private val bookingRecordRepository: BookingRecordRepository,
    private val accountTransactionRepository: AccountTransactionRepository,
) {

    fun createExport(bookId: Long): ByteArray {
        val book = bookService.getBook(bookId)
        val accounts =
            book.accountGroups.sortedBy { it.groupNumber }.flatMap { it.accounts.sortedBy { a -> a.accountNumber } }

        val workbook = XSSFWorkbook()

        createAccountIndexSheet(workbook, book)
        createFullPage(workbook, book, accounts)

        for (account in accounts) {
            createAccountPage(workbook, account)
        }

        return ByteArrayOutputStream().also {
            workbook.write(it)
        }.toByteArray()
    }

    fun createAccountIndexSheet(workbook: XSSFWorkbook, book: Book) {
        val sheet = workbook.createSheet("Index")
        var idx = 0
        for (group in book.accountGroups.sortedWith(Comparator.comparing { it.groupNumber })) {
            val row = sheet.createRow(idx++)
            row.createCell(0).setCellValue(group.groupNumber.toString())
            row.createCell(1).setCellValue(group.title)

            for (account in group.accounts.sortedWith(Comparator.comparing { it.accountNumber })) {
                val accountRow = sheet.createRow(idx++)
                accountRow.createCell(1).setCellValue(account.toShortString())
                accountRow.createCell(2).setCellValue(account.title)
                accountRow.createCell(3).setCellValue(account.description)
            }
        }
        for (i in 0..3) {
            sheet.autoSizeColumn(i)
        }
    }

    fun createFullPage(workbook: XSSFWorkbook, book: Book, accounts: List<BookAccount>) {
        val sheet = workbook.createSheet("Journal")
        val headerRow = sheet.createRow(0)

        headerRow.createCell(0).setCellValue("ID")
        headerRow.createCell(1).setCellValue("Datum")
        headerRow.createCell(2).setCellValue("Buchungstext")
        headerRow.createCell(3).setCellValue("Tag")
        accounts.forEachIndexed { idx, account ->
            headerRow.createCell(4 + idx).setCellValue(account.toShortString())
        }
        headerRow.createCell(4 + accounts.size).setCellValue("Beschreibung")

        val records = bookingRecordRepository.listAllInBook(book, Pageable.unpaged()).reversed()

        records.forEachIndexed { rowIndex, record ->
            val row = sheet.createRow(rowIndex + 1)
            row.createCell(0).setCellValue(record.localId.toString())
            row.createCell(1).setCellValue(record.bookingDate.toString())
            row.createCell(2).setCellValue(record.bookingText)
            record.tag?.let { row.createCell(3).setCellValue(it) }
            accounts.forEachIndexed { idx, account ->
                record.movements[account]?.let {
                    row.createCell(4 + idx).setCellValue(it.toString())
                }
            }
            row.createCell(4 + accounts.size).setCellValue(record.description)
        }

        val table = sheet.createTable(
            AreaReference(
                CellReference(0, 0),
                CellReference(records.size, 4 + accounts.size),
                workbook.spreadsheetVersion
            )
        )

        table.ctTable.addNewTableStyleInfo()
        table.ctTable.tableStyleInfo.name = "TableStyleMedium2"

        for (i in 0..(4 + accounts.size)) {
            sheet.autoSizeColumn(i)
        }

    }

    fun createAccountPage(workbook: XSSFWorkbook, account: BookAccount) {
        val records = accountTransactionRepository.findByAccount(account)

        if (records.isEmpty()) {
            return
        }

        val sheet = workbook.createSheet(account.toShortString())

        val inverted = account.accountType.invertedPresentation

        sheet.createRow(0).also { row ->
            row.createCell(0).setCellValue("ID")
            row.createCell(1).setCellValue("Datum")
            row.createCell(2).setCellValue("Buchungstext")
            row.createCell(3).setCellValue("Tag")
            if (inverted) {
                row.createCell(4).setCellValue("Haben")
                row.createCell(5).setCellValue("Soll")
            } else {
                row.createCell(4).setCellValue("Soll")
                row.createCell(5).setCellValue("Haben")
            }
            row.createCell(6).setCellValue("Saldo")
            row.createCell(7).setCellValue("Beschreibung")
        }

        var index = 1
        var runningBalance = BigDecimal.ZERO

        for (record in records) {
            sheet.createRow(index++).also { row ->
                row.createCell(0).setCellValue(record.recordId.toString())
                row.createCell(1).setCellValue(record.bookingDate.toString())
                row.createCell(2).setCellValue(record.bookingText)
                row.createCell(3).setCellValue(record.tag)

                runningBalance += record.amount
                val soll = record.amount.takeIf { it > BigDecimal.ZERO }
                val haben = record.amount.takeIf { it < BigDecimal.ZERO }?.negate()

                if (inverted) {
                    row.createCell(4).setCellValue(haben?.toString())
                    row.createCell(5).setCellValue(soll?.toString())
                    row.createCell(6).setCellValue(runningBalance.negate().toString())
                } else {
                    row.createCell(4).setCellValue(soll?.toString())
                    row.createCell(5).setCellValue(haben?.toString())
                    row.createCell(6).setCellValue(runningBalance.toString())
                }

                row.createCell(7).setCellValue(record.description)
            }
        }


        val table = sheet.createTable(
            AreaReference(
                CellReference(0, 0),
                CellReference(records.size, 7),
                workbook.spreadsheetVersion
            )
        )

        table.ctTable.addNewTableStyleInfo()
        table.ctTable.tableStyleInfo.name = "TableStyleMedium2"

        for (i in 0..7) {
            sheet.autoSizeColumn(i)
        }

    }

}