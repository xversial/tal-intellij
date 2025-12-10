package com.github.xversial.talintellij.tal.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class TalFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val text = document.charsSequence
        val descriptors = mutableListOf<FoldingDescriptor>()

        // <editor-fold desc="..."> ... </editor-fold> with arbitrary nesting
        run {
            val openFold = "<editor-fold"
            val closeFold = "</editor-fold>"
            var idx = 0
            val stack = ArrayDeque<Int>()
            while (idx < text.length) {
                val nextOpen = text.indexOf(openFold, idx)
                val nextClose = text.indexOf(closeFold, idx)
                if (nextOpen == -1 && nextClose == -1) break

                val takeOpen = nextOpen != -1 && (nextClose == -1 || nextOpen < nextClose)
                if (takeOpen) {
                    // Ensure we found a complete opening tag (has a '>')
                    val openEnd = text.indexOf('>', nextOpen)
                    if (openEnd == -1) {
                        // Malformed tag, skip past the token to avoid infinite loop
                        idx = nextOpen + openFold.length
                        continue
                    }
                    stack.addLast(nextOpen)
                    idx = openEnd + 1
                } else {
                    // Closing tag; if we have an opener, create a region
                    val closeIdx = nextClose
                    val start = stack.removeLastOrNull()
                    if (start != null) {
                        val range = TextRange(start, closeIdx + closeFold.length)
                        descriptors += FoldingDescriptor(root.node, range)
                    }
                    idx = closeIdx + closeFold.length
                }
            }
            // Unmatched open tags are ignored
        }

        // // region ... -> // endregion and # region -> # endregion
        data class Marker(val start: Int, val name: String)
        val regionStack = ArrayDeque<Marker>()
        var lineStart = 0
        while (lineStart < text.length) {
            val lineEnd = document.getLineEndOffset(document.getLineNumber(lineStart))
            val line = text.subSequence(lineStart, lineEnd).toString()
            val trimmed = line.trimStart()
            val col = line.length - trimmed.length
            val lower = trimmed.lowercase()
            when {
                lower.startsWith("// region") || lower.startsWith("# region") -> {
                    val name = trimmed.substringAfter("region").trim()
                    regionStack.addLast(Marker(lineStart + col, name))
                }
                lower.startsWith("// endregion") || lower.startsWith("# endregion") -> {
                    val start = regionStack.removeLastOrNull()
                    if (start != null) {
                        val range = TextRange(start.start, lineEnd)
                        descriptors += FoldingDescriptor(root.node, range)
                    }
                }
            }
            lineStart = lineEnd + 1
        }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? = "..."

    // Since all our descriptors are attached to the file root node,
    // use the range-aware override to compute a meaningful placeholder
    // from the actual folded text segment.
    override fun getPlaceholderText(node: ASTNode, range: TextRange): String {
        val fileText = node.psi.text
        val safeRange = range.intersection(TextRange(0, fileText.length)) ?: return "..."
        val segment = safeRange.substring(fileText)

        // Try: <editor-fold desc="..."> ... </editor-fold>
        // Support both single and double quotes
        val editorFoldIdx = segment.indexOf("<editor-fold", ignoreCase = true)
        if (editorFoldIdx >= 0) {
            val header = segment.substring(editorFoldIdx, segment.indexOf('>', editorFoldIdx).let { if (it == -1) segment.length else it })
            // Extract desc attribute
            val desc = extractAttribute(header, "desc")
            if (!desc.isNullOrBlank()) return withPersistentId(desc, range)
            // Fallback: show tag name
            return withPersistentId("editor-fold", range)
        }

        // Try: // region NAME or # region NAME (use first line inside range)
        val firstLineEnd = segment.indexOf('\n').let { if (it == -1) segment.length else it }
        val firstLine = segment.substring(0, firstLineEnd)
        val trimmed = firstLine.trimStart()
        val lower = trimmed.lowercase()
        if (lower.startsWith("// region") || lower.startsWith("# region")) {
            val name = trimmed.substringAfter("region").trim()
            if (name.isNotEmpty()) return withPersistentId(name, range)
            return withPersistentId("region", range)
        }

        // Default placeholder
        return withPersistentId("...", range)
    }

    private fun extractAttribute(header: String, attr: String): String? {
        // Matches: attr="..." or attr='...'
        val dq = Regex("(?i)\\b" + Regex.escape(attr) + "\\s*=\\s*\"([^\"]*)\"")
        val sq = Regex("(?i)\\b" + Regex.escape(attr) + "\\s*=\\s*'([^']*)'")
        return dq.find(header)?.groupValues?.getOrNull(1)
            ?: sq.find(header)?.groupValues?.getOrNull(1)
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false

    private fun withPersistentId(base: String, range: TextRange): String {
        // Append an invisible identifier so each fold region has a unique signature for persistence
        // while keeping the visible placeholder clean. U+2063 is an invisible separator.
        return base + "\u2063" + range.startOffset
    }
}
