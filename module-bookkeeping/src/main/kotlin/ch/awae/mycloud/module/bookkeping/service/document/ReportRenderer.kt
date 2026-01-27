package ch.awae.mycloud.module.bookkeping.service.document

import ch.awae.mycloud.lib.pdf.Document
import ch.awae.mycloud.lib.pdf.StandardFont.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ReportRenderer {

    fun generateReport(
        pdf: Document,
        mode: Mode,
        title: String,
        subtitle: String,
        groups: List<Group>,
        hideZeroProfitLine: Boolean = false,
    ) {
        // text fonts
        val titleFont = pdf.loadFont(LIBERATION_SERIF_BOLD, 18f)
        val groupFont = pdf.loadFont(LIBERATION_SERIF_BOLD, 12f)
        val itemFont = pdf.loadFont(LIBERATION_SERIF_REGULAR, 12f)
        val tagFont = pdf.loadFont(LIBERATION_SERIF_ITALIC, 12f)

        val footerFont = pdf.loadFont(LIBERATION_SERIF_REGULAR, 9f)

        // number fonts
        val groupNumberFont = pdf.loadFont(LIBERATION_MONO_BOLD, 11f)
        val itemNumberFont = pdf.loadFont(LIBERATION_MONO_REGULAR, 11f)
        val tagNumberFont = pdf.loadFont(LIBERATION_MONO_ITALIC, 11f)

        // document formatting rules
        val leading = 1.2f

        // tabs texts
        val itemColumn = 5f
        val tagsColumn = 100f
        val creditColumn = 130f
        val debitColumn = 160f

        // rendering
        pdf.addPage {
            var y = 0f

            // title lines
            y += titleFont.height * leading
            writeText(0f, y, title, titleFont)
            y += titleFont.height * leading
            writeText(0f, y, subtitle, titleFont)

            // column labels
            y += 2 * groupFont.height * leading
            writeText(creditColumn - 4f, y, mode.creditLabel, groupFont, true)
            writeText(debitColumn - 4f, y, mode.debitLabel, groupFont, true)

            // render individual groups
            groups@ for (group in groups) {
                if (group.items.isEmpty() && group.valueOverride == null) {
                    continue@groups
                }

                var groupCredit = group.valueForType(ItemType.CREDIT)
                var groupDebit = group.valueForType(ItemType.DEBIT)

                // on balance sheets, hide nil groups
                if (mode == Mode.BALANCE) {
                    val onlyZeroCredits = group.items
                        .filter { it.type == ItemType.CREDIT }
                        .none { it.amount.compareTo(BigDecimal.ZERO) != 0 }
                    val onlyZeroDebits = group.items
                        .filter { it.type == ItemType.DEBIT }
                        .none { it.amount.compareTo(BigDecimal.ZERO) != 0 }

                    if (onlyZeroCredits && onlyZeroDebits) {
                        continue@groups
                    }
                    if (onlyZeroCredits) {
                        groupCredit = null
                    }
                    if (onlyZeroDebits) {
                        groupDebit = null
                    }
                }

                // leave empty line above each group
                y += 2 * groupFont.height * leading

                writeText(0f, y, group.label, groupFont)
                if (groupCredit != null) {
                    writeText(creditColumn, y, formatNumber(groupCredit), groupNumberFont, true)
                }
                if (groupDebit != null) {
                    writeText(debitColumn, y, formatNumber(groupDebit), groupNumberFont, true)
                }

                // print group items
                items@ for (item in group.items) {
                    // skip nil elements
                    if (mode == Mode.BALANCE && (item.amount.compareTo(BigDecimal.ZERO) == 0)) {
                        continue@items
                    }
                    if (item.tag) {
                        y += tagFont.height * leading
                        writeText(tagsColumn, y, item.label ?: "(null)", tagFont, true)
                    } else {
                        y += itemFont.height * leading
                        writeText(itemColumn, y, item.label ?: "(null)", itemFont)
                    }

                    val numberFont = if (item.tag) tagNumberFont else itemNumberFont

                    if (item.type == ItemType.CREDIT) {
                        writeText(creditColumn, y, formatNumber(item.amount), numberFont, true)
                    } else {
                        writeText(debitColumn, y, formatNumber(item.amount), numberFont, true)
                    }
                }
            }

            // render closing columns
            val totalCredit = groups.sumOf { it.valueForType(ItemType.CREDIT) ?: BigDecimal.ZERO }
            val totalDebit = groups.sumOf { it.valueForType(ItemType.DEBIT) ?: BigDecimal.ZERO }

            val showProfitLine = !hideZeroProfitLine || totalCredit.compareTo(totalDebit) != 0

            y += 3 * groupFont.height * leading
            if (mode == Mode.EARNINGS) {
                writeText(0f, y, "Total", groupFont)
                writeText(creditColumn, y, formatNumber(totalCredit), groupNumberFont, true)
                writeText(debitColumn, y, formatNumber(totalDebit), groupNumberFont, true)

                if (showProfitLine) {
                    y += 2 * groupFont.height * leading
                    if (totalCredit > totalDebit) {
                        // net loss
                        writeText(0f, y, "Verlust", groupFont)
                        writeText(debitColumn, y, formatNumber(totalCredit - totalDebit), groupNumberFont, true)
                    } else {
                        // net profit (or zero)
                        writeText(0f, y, "Gewinn", groupFont)
                        writeText(creditColumn, y, formatNumber(totalDebit - totalCredit), groupNumberFont, true)
                    }
                }
            } else {
                if (showProfitLine) {
                    if (totalCredit < totalDebit) {
                        // net loss
                        writeText(0f, y, "Verlust (Erfolgsrechnung)", groupFont)
                        writeText(creditColumn, y, formatNumber(totalDebit - totalCredit), groupNumberFont, true)
                    } else {
                        // net profit (or zero)
                        writeText(0f, y, "Gewinn (Erfolgsrechnung)", groupFont)
                        writeText(debitColumn, y, formatNumber(totalCredit - totalDebit), groupNumberFont, true)
                    }
                    y += 2 * groupFont.height * leading
                }

                writeText(0f, y, "Bilanzsumme", groupFont)

                val balanceSum = totalDebit.max(totalCredit)
                writeText(creditColumn, y, formatNumber(balanceSum), groupNumberFont, true)
                writeText(debitColumn, y, formatNumber(balanceSum), groupNumberFont, true)
            }

            // render footer
            val footer = "Stand " + LocalDateTime.now(ZoneId.of("Europe/Zurich"))
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
            val footerX = ((170f - footerFont.getStringWidth(footer)) / 2) - 10
            writeText(footerX, 257f, footer, footerFont)
        }
    }

    data class Group(
        val label: String,
        val items: List<Item>,
        val valueOverride: ValueOverride?,
    ) {
        fun valueForType(itemType: ItemType): BigDecimal? {
            // if we have an override, we provide these values
            if (valueOverride != null) {
                return when (itemType) {
                    ItemType.CREDIT -> valueOverride.credit
                    ItemType.DEBIT -> valueOverride.debit
                }
            }
            // if no override is present, provide sum of items
            return items.filter { it.type == itemType }.map { it.amount }.reduceOrNull(BigDecimal::add)
        }

        data class ValueOverride(
            val credit: BigDecimal?,
            val debit: BigDecimal?,
        )
    }

    data class Item(
        val label: String?,
        val amount: BigDecimal,
        val type: ItemType,
        val tag: Boolean,
    )

    enum class ItemType {
        CREDIT, DEBIT;

        companion object {
            fun getType(inverted: Boolean) = when (inverted) {
                false -> CREDIT
                true -> DEBIT
            }
        }
    }

    enum class Mode(
        val creditLabel: String,
        val debitLabel: String,
    ) {
        BALANCE("Aktiva", "Passiva"),
        EARNINGS("Aufwand", "Ertrag"),
    }

}

private fun formatNumber(value: BigDecimal): String {
    return value.setScale(2).toString().replace(Regex("\\.00$"), ".- ")
}