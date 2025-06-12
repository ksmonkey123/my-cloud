package ch.awae.mycloud.module.bookkeping.service.document

import ch.awae.mycloud.module.bookkeping.pdf.PdfDocument
import org.apache.pdfbox.pdmodel.font.*
import java.math.*
import java.time.*
import java.time.format.*

object AccountLedgerRenderer {

    fun generateLedger(
        pdf: PdfDocument,
        title: String,
        subtitle: String,
        transactions: List<TransactionRecord>,
        invertedPresentation: Boolean,
    ) {
        // text fonts
        val titleFont = pdf.loadFont(Standard14Fonts.FontName.TIMES_BOLD, 14f)
        val groupFont = pdf.loadFont(Standard14Fonts.FontName.TIMES_BOLD, 11f)
        val itemFont = pdf.loadFont(Standard14Fonts.FontName.TIMES_ROMAN, 11f)

        val footerFont = pdf.loadFont(Standard14Fonts.FontName.TIMES_ROMAN, 9f)

        // number fonts
        val groupNumberFont = pdf.loadFont(Standard14Fonts.FontName.COURIER_BOLD, 10f)
        val itemNumberFont = pdf.loadFont(Standard14Fonts.FontName.COURIER, 10f)

        // document formatting rules
        val leading = 1.2f

        // tabs texts
        val dateColumn = 0f
        val idColumn = 26f
        val textColumn = 28f
        val creditColumn = 130f
        val debitColumn = 150f
        val balanceColumn = 170f

        val chunks = transactions.chunked(45)

        var balance = BalanceState()

        for ((index, chunk) in chunks.withIndex()) {

            // rendering
            pdf.addPage {
                var y = 0f
                var lastDate: LocalDate? = null

                // title lines
                y += titleFont.height * leading
                writeText(0f, y, title, titleFont)
                writeText(170f, y, "${index + 1} / ${chunks.size}", titleFont, true)
                y += titleFont.height * leading
                writeText(0f, y, subtitle, titleFont)

                // column labels
                y += 2 * groupFont.height * leading
                if (invertedPresentation) {
                    writeText(creditColumn - 2f, y, "Haben", groupFont, true)
                    writeText(debitColumn - 2f, y, "Soll", groupFont, true)
                } else {
                    writeText(creditColumn - 2f, y, "Soll", groupFont, true)
                    writeText(debitColumn - 2f, y, "Haben", groupFont, true)
                }
                writeText(balanceColumn - 2f, y, "Saldo", groupFont, true)

                if (index > 0) {
                    y += groupFont.height * leading
                    writeText(0f, y, "Übertrag von letzter Seite", groupFont)
                    if (invertedPresentation) {
                        writeText(creditColumn, y, formatNumber(balance.debits), groupNumberFont, true)
                        writeText(debitColumn, y, formatNumber(balance.credits), groupNumberFont, true)
                        writeText(balanceColumn, y, formatNumber(-balance.balance), groupNumberFont, true)
                    } else {
                        writeText(creditColumn, y, formatNumber(balance.credits), groupNumberFont, true)
                        writeText(debitColumn, y, formatNumber(balance.debits), groupNumberFont, true)
                        writeText(balanceColumn, y, formatNumber(balance.balance), groupNumberFont, true)
                    }
                }
                y += groupFont.height * leading

                // render individual records
                for (transaction in chunk) {
                    y += itemFont.height * leading

                    if (lastDate != transaction.date) {
                        // only print date on change
                        lastDate = transaction.date
                        writeText(
                            dateColumn,
                            y,
                            transaction.date.format(DateTimeFormatter.ofPattern("dd.MM.uu")),
                            itemFont
                        )

                    }

                    writeText(idColumn, y, transaction.id.toString(), itemFont, true)
                    if (transaction.marker) {
                        writeText(idColumn, y, "*", itemFont)
                    }
                    writeText(textColumn, y, transaction.text, itemFont)

                    balance += transaction.amount

                    if (invertedPresentation) {
                        if (transaction.amount < BigDecimal.ZERO) {
                            writeText(creditColumn, y, formatNumber(-transaction.amount), itemNumberFont, true)
                        } else {
                            writeText(debitColumn, y, formatNumber(transaction.amount), itemNumberFont, true)
                        }
                        writeText(balanceColumn, y, formatNumber(-balance.balance), itemNumberFont, true)
                    } else {
                        if (transaction.amount > BigDecimal.ZERO) {
                            writeText(creditColumn, y, formatNumber(transaction.amount), itemNumberFont, true)
                        } else {
                            writeText(debitColumn, y, formatNumber(-transaction.amount), itemNumberFont, true)
                        }
                        writeText(balanceColumn, y, formatNumber(balance.balance), itemNumberFont, true)
                    }

                }

                y += 2 * itemFont.height * leading
                writeText(
                    0f,
                    y,
                    if (index + 1 == chunks.size) "Schlusssaldo" else "Übertrag auf nächste Seite",
                    groupFont
                )
                if (invertedPresentation) {
                    writeText(creditColumn, y, formatNumber(balance.debits), groupNumberFont, true)
                    writeText(debitColumn, y, formatNumber(balance.credits), groupNumberFont, true)
                    writeText(balanceColumn, y, formatNumber(-balance.balance), groupNumberFont, true)
                } else {
                    writeText(creditColumn, y, formatNumber(balance.credits), groupNumberFont, true)
                    writeText(debitColumn, y, formatNumber(balance.debits), groupNumberFont, true)
                    writeText(balanceColumn, y, formatNumber(balance.balance), groupNumberFont, true)
                }
                // render footer
                val footer = "Stand " + LocalDateTime.now(ZoneId.of("Europe/Zurich"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                val footerX = ((170f - footerFont.getStringWidth(footer)) / 2) - 10
                writeText(footerX, 257f, footer, footerFont)
            }
        }
    }

    data class TransactionRecord(
        val date: LocalDate,
        val id: Long,
        val text: String,
        val amount: BigDecimal,
        val marker: Boolean,
    )

    data class BalanceState(
        val credits: BigDecimal = BigDecimal.ZERO,
        val debits: BigDecimal = BigDecimal.ZERO,
    ) {

        val balance: BigDecimal
            get() = credits - debits

        operator fun plus(delta: BigDecimal): BalanceState {
            return if (delta > BigDecimal.ZERO) {
                BalanceState(credits + delta, debits)
            } else {
                BalanceState(credits, debits - delta)
            }
        }
    }

}


private fun formatNumber(value: BigDecimal): String {
    return value.setScale(2).toString().replace(Regex("\\.00$"), ".- ")
}