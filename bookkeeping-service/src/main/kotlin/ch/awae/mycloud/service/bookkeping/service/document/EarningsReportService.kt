package ch.awae.mycloud.service.bookkeping.service.document

import ch.awae.mycloud.*
import ch.awae.mycloud.pdf.*
import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.model.*
import ch.awae.mycloud.service.bookkeping.service.*
import jakarta.transaction.*
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.*
import org.springframework.stereotype.*
import java.math.*
import java.time.*
import java.time.format.*

@Service
@Transactional
class EarningsReportService(
    private val bookService: BookService,
    private val accountTagBalanceRepository: AccountTagBalanceRepository,
) {

    fun generateEarningsReport(bookId: Long, groupNumbers: List<Int>): PdfDocument {
        val book = bookService.getBook(bookId)

        val balanceGroups = book.accountGroups.filter { it.accounts.any { a -> !a.accountType.earningsAccount } }

        val groups = book.accountGroups.filter { groupNumbers.isEmpty() || it.groupNumber in groupNumbers }.filter {
            // account must contain earnings accounts
            it.accounts.any { a -> a.accountType.earningsAccount }
        }.sortedBy { it.groupNumber }

        if (groups.isEmpty()) {
            throw InvalidRequestException("cannot generate earnings report without any account groups")
        }

        return PdfDocument {
            val titleFont = loadFont(TIMES_BOLD, 18f)
            val groupFont = loadFont(TIMES_BOLD, 12f)
            val accountFont = loadFont(TIMES_ROMAN, 12f)
            val tagFont = loadFont(TIMES_ITALIC, 12f)

            val groupNumberFont = loadFont(COURIER_BOLD, 12f)
            val accountNumberFont = loadFont(COURIER, 12f)
            val tagNumberFont = loadFont(COURIER_OBLIQUE, 12f)


            val stepSize = accountFont.height * 1.2f
            val tagsColumn = 100f
            val expenseColumn = 130f
            val incomeColumn = 160f

            val footer = "Stand " + LocalDateTime.now(ZoneId.of("Europe/Zurich")).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            val footerX = ((170f - accountFont.getStringWidth(footer)) / 2) - 10

            addPage {
                writeText(0f, titleFont.height, book.title, titleFont)
                writeText(0f, titleFont.height * 2.2f, "Schlussbilanz", titleFont)

                var y = 30f

                writeText(expenseColumn, y, "Aktive", groupFont, true)
                writeText(incomeColumn, y, "Passive", groupFont, true)

                y += stepSize * 2

                for (group in balanceGroups) {
                    writeText(
                        0f, y, group.title, groupFont
                    )

                    val liabiltyAccounts = group.accounts.filter { it.accountType == AccountType.LIABILITY }
                    val assetAccounts = group.accounts.filter { it.accountType == AccountType.ASSET }

                    val totalLiabilities = liabiltyAccounts.map { it.balance?.balance ?: BigDecimal.ZERO }
                        .fold(BigDecimal.ZERO, BigDecimal::add).setScale(2).negate()

                    val totalAssets = assetAccounts.map { it.balance?.balance ?: BigDecimal.ZERO }
                        .fold(BigDecimal.ZERO, BigDecimal::add).setScale(2)

                    writeText(expenseColumn, y, totalAssets.toString(), groupNumberFont, true)
                    writeText(incomeColumn, y, totalLiabilities.toString(), groupNumberFont, true)

                    y += stepSize

                    val accounts = group.accounts
                        .filter { !it.accountType.earningsAccount }
                        .filter { it.balance != null && it.balance.balance.compareTo(BigDecimal.ZERO) != 0 }
                        .sortedBy { it.accountNumber }

                    for (account in accounts) {
                        val balance = (account.balance?.balance ?: BigDecimal.ZERO).setScale(2)

                        writeText(5f, y, AccountId.of(account).toString() + " - " + account.title, accountFont)
                        if (account.accountType == AccountType.LIABILITY) {
                            writeText(incomeColumn, y, balance.negate().toString(), accountNumberFont, true)
                        } else {
                            writeText(expenseColumn, y, balance.toString(), accountNumberFont, true)
                        }

                        y += stepSize
                    }
                    y += stepSize
                }

                y += stepSize

                val allAccounts = balanceGroups.flatMap { it.accounts }

                val fullAssets = allAccounts.filter { it.accountType == AccountType.ASSET }
                    .map { it.balance?.balance ?: BigDecimal.ZERO }.fold(BigDecimal.ZERO, BigDecimal::add).setScale(2)
                val fullLiabilities = allAccounts.filter { it.accountType == AccountType.LIABILITY }
                    .map { it.balance?.balance ?: BigDecimal.ZERO }.fold(BigDecimal.ZERO, BigDecimal::add).setScale(2).negate()


                val balanceSum = if (fullAssets > fullLiabilities) {
                    writeText(0f, y, "Gewinn (Erfolgsrechnung)", groupFont)
                    writeText(incomeColumn, y, (fullAssets - fullLiabilities).toString(), groupNumberFont, true)
                    fullAssets
                } else {
                    writeText(0f, y, "Verlust (Erfolgsrechnung)", groupFont)
                    writeText(expenseColumn, y, (fullLiabilities - fullAssets).toString(), groupNumberFont, true)
                    fullLiabilities
                }
                y += stepSize * 2

                writeText(0f, y, "Bilanzsumme", groupFont)
                writeText(expenseColumn, y, balanceSum.toString(), groupNumberFont, true)
                writeText(incomeColumn, y, balanceSum.toString(), groupNumberFont, true)

                writeText(footerX, 257f, footer, accountFont)
            }

            addPage {
                writeText(0f, titleFont.height, book.title, titleFont)
                writeText(0f, titleFont.height * 2.2f, "Erfolgsrechnung", titleFont)

                var y = 30f


                writeText(expenseColumn, y, "Aufwand", groupFont, true)
                writeText(incomeColumn, y, "Ertrag", groupFont, true)

                y += stepSize * 2

                for (group in groups) {
                    writeText(
                        0f, y, group.title, groupFont
                    )

                    val incomeAccounts = group.accounts.filter { it.accountType == AccountType.INCOME }
                    val expenseAccounts = group.accounts.filter { it.accountType == AccountType.EXPENSE }

                    val totalIncome = incomeAccounts.map { it.balance?.balance ?: BigDecimal.ZERO }
                        .fold(BigDecimal.ZERO, BigDecimal::add).setScale(2).negate()

                    val totalExpense = expenseAccounts.map { it.balance?.balance ?: BigDecimal.ZERO }
                        .fold(BigDecimal.ZERO, BigDecimal::add).setScale(2)

                    writeText(expenseColumn, y, totalExpense.toString(), groupNumberFont, true)
                    writeText(incomeColumn, y, totalIncome.toString(), groupNumberFont, true)

                    y += stepSize

                    val accounts = group.accounts
                        .filter { it.accountType.earningsAccount }
                        .sortedBy { it.accountNumber }

                    for (account in accounts) {
                        val balance = (account.balance?.balance ?: BigDecimal.ZERO).setScale(2)

                        writeText(5f, y, AccountId.of(account).toString() + " - " + account.title, accountFont)
                        if (account.accountType == AccountType.INCOME) {
                            writeText(incomeColumn, y, balance.negate().toString(), accountNumberFont, true)
                        } else {
                            writeText(expenseColumn, y, balance.toString(), accountNumberFont, true)
                        }

                        y += stepSize
                    }
                    y += stepSize
                }


                y += stepSize

                writeText(0f, y, "Total", groupFont)

                val allAccounts = groups.flatMap { it.accounts }

                val fullExpense = allAccounts.filter { it.accountType == AccountType.EXPENSE }
                    .map { it.balance?.balance ?: BigDecimal.ZERO }.fold(BigDecimal.ZERO, BigDecimal::add).setScale(2)
                val fullIncome = allAccounts.filter { it.accountType == AccountType.INCOME }
                    .map { it.balance?.balance ?: BigDecimal.ZERO }.fold(BigDecimal.ZERO, BigDecimal::add).setScale(2).negate()

                writeText(expenseColumn, y, fullExpense.toString(), groupNumberFont, true)
                writeText(incomeColumn, y, fullIncome.toString(), groupNumberFont, true)

                y += stepSize * 2

                if (fullExpense > fullIncome) {
                    writeText(0f, y, "Verlust", groupFont)
                    writeText(incomeColumn, y, (fullExpense - fullIncome).toString(), groupNumberFont, true)
                } else {
                    writeText(0f, y, "Gewinn", groupFont)
                    writeText(expenseColumn, y, (fullIncome - fullExpense).toString(), groupNumberFont, true)
                }

                writeText(footerX, 257f, footer, accountFont)
            }

            for (group in groups) {
                addPage {
                    writeText(0f, titleFont.height, book.title, titleFont)
                    writeText(0f, titleFont.height * 2.2f, "Erfolgsrechnung ${group.title}", titleFont)

                    var y = 30f

                    writeText(expenseColumn, y, "Aufwand", groupFont, true)
                    writeText(incomeColumn, y, "Ertrag", groupFont, true)
                    y += stepSize * 2

                    val accounts = group.accounts
                        .filter { it.accountType.earningsAccount }
                        .sortedBy { it.accountNumber }

                    for (account in accounts) {
                        val balance = (account.balance?.balance ?: BigDecimal.ZERO).setScale(2)

                        writeText(0f, y, AccountId.of(account).toString() + " - " + account.title, groupFont)
                        if (account.accountType == AccountType.INCOME) {
                            writeText(incomeColumn, y, balance.negate().toString(), groupNumberFont, true)
                        } else {
                            writeText(expenseColumn, y, balance.toString(), groupNumberFont, true)
                        }
                        y += stepSize

                        val tags = accountTagBalanceRepository.findByAccountId(account.id)
                            .filter { it.balance != BigDecimal.ZERO }.map {
                                it.tag.takeIf { tag -> tag.isNotEmpty() } to it.balance.setScale(2)
                            }.sortedBy { (tag, _) -> tag ?: "}" }

                        if (tags.any { (tag, _) -> tag != null }) {
                            for ((tag, tagBalance) in tags) {
                                writeText(tagsColumn, y, tag ?: "(null)", tagFont, true)
                                if (account.accountType == AccountType.INCOME) {
                                    writeText(incomeColumn, y, tagBalance.negate().toString(), tagNumberFont, true)
                                } else {
                                    writeText(expenseColumn, y, tagBalance.toString(), tagNumberFont, true)
                                }
                                y += stepSize
                            }
                        }

                        y += stepSize
                    }


                    y += stepSize
                    val fullExpense = accounts.filter { it.accountType == AccountType.EXPENSE }
                        .map { it.balance?.balance ?: BigDecimal.ZERO }.fold(BigDecimal.ZERO, BigDecimal::add).setScale(2)
                    val fullIncome = accounts.filter { it.accountType == AccountType.INCOME }
                        .map { it.balance?.balance ?: BigDecimal.ZERO }.fold(BigDecimal.ZERO, BigDecimal::add).setScale(2).negate()

                    writeText(0f, y, "Total", groupFont)
                    writeText(expenseColumn, y, fullExpense.toString(), groupNumberFont, true)
                    writeText(incomeColumn, y, fullIncome.toString(), groupNumberFont, true)

                    y += stepSize * 2

                    if (fullExpense > fullIncome) {
                        writeText(0f, y, "Verlust", groupFont)
                        writeText(incomeColumn, y, (fullExpense - fullIncome).toString(), groupNumberFont, true)
                    } else {
                        writeText(0f, y, "Gewinn", groupFont)
                        writeText(expenseColumn, y, (fullIncome - fullExpense).toString(), groupNumberFont, true)
                    }

                    writeText(footerX, 257f, footer, accountFont)
                }

            }

        }
    }

}