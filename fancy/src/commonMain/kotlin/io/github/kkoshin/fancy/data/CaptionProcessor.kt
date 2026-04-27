package io.github.kkoshin.fancy.data

object CaptionProcessor {
    fun processHighlight(text: String, style: CaptionStyle): List<CaptionSegment> {
        if (text.isEmpty()) return emptyList()

        val lines = text.split("\n")
        val result = mutableListOf<CaptionSegment>()

        lines.forEachIndexed { index, line ->
            if (line.isEmpty()) {
                if (index < lines.size - 1) {
                    result.add(CaptionSegment("\n"))
                }
                return@forEachIndexed
            }

            val length = line.length
            val mid = length / 2
            val isEven = length % 2 == 0

            val startHighlight = if (isEven) mid - 1 else mid
            val endHighlight = mid + 1

            // Prefix
            if (startHighlight > 0) {
                result.add(CaptionSegment(line.substring(0, startHighlight)))
            }

            // Highlight
            result.add(
                CaptionSegment(
                    line.substring(startHighlight, endHighlight),
                    style.highlightStyle
                )
            )

            // Suffix
            var suffix = line.substring(endHighlight)
            if (index < lines.size - 1) {
                suffix += "\n"
            }

            if (suffix.isNotEmpty()) {
                result.add(CaptionSegment(suffix))
            } else if (index < lines.size - 1) {
                // This case handles when suffix is empty but we need to add a newline
                // It's covered by the suffix += "\n" but for clarity
            }
        }

        return result
    }
}
