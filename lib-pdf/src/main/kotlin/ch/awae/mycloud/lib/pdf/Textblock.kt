package ch.awae.mycloud.lib.pdf

class Textblock(text: String, val font: SizedFont, lineWidth: Float = 170f) {
    val lines: List<String>

    init {
        val words = text.split(' ')

        val fullLines = mutableListOf<String>()
        var currentLine: String? = null
        for (word in words)
            if (currentLine == null) {
                // first word always goes on the line
                currentLine = word
            } else {
                val newCurrentLine = "$currentLine $word"
                if (font.getStringWidth(newCurrentLine) > lineWidth) {
                    // next word did not fit onto line. break before word.
                    fullLines.add(currentLine)
                    currentLine = word
                } else {
                    // still got space. add word to line.
                    currentLine = newCurrentLine
                }
            }
        // we are done. if we have a non-empty line, we should complete that too
        if (!currentLine.isNullOrEmpty()) {
            fullLines.add(currentLine)
        }

        this.lines = fullLines
    }

}