package com.github.xversial.talintellij.tal.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.lang.folding.NamedFoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class TalFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val text = document.charsSequence
        val descriptors = mutableListOf<FoldingDescriptor>()

        // <editor-fold desc="..."> ... </editor-fold>
        val openFold = "<editor-fold"
        val closeFold = "</editor-fold>"
        var idx = 0
        while (true) {
            idx = text.indexOf(openFold, idx)
            if (idx < 0) break
            val openEnd = text.indexOf('>', idx)
            if (openEnd < 0) break
            val closeIdx = text.indexOf(closeFold, openEnd + 1)
            if (closeIdx < 0) break
            val range = TextRange(idx, closeIdx + closeFold.length)
            // Try to extract a stable name for persistence based on desc attribute; fall back to start offset
            val header = text.subSequence(idx, openEnd + 1).toString()
            val desc = extractAttribute(header, "desc")
            val name = (desc?.takeIf { it.isNotBlank() } ?: "editor-fold") + "@" + idx
            descriptors += NamedFoldingDescriptor(root.node, range, null, name)
            idx = closeIdx + closeFold.length
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
                        val base = if (start.name.isNotBlank()) start.name else "region"
                        val name = "$base@${start.start}"
                        descriptors += NamedFoldingDescriptor(root.node, range, null, name)
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
            if (!desc.isNullOrBlank()) return desc
            // Fallback: show tag name
            return "editor-fold"
        }

        // Try: // region NAME or # region NAME (use first line inside range)
        val firstLineEnd = segment.indexOf('\n').let { if (it == -1) segment.length else it }
        val firstLine = segment.substring(0, firstLineEnd)
        val trimmed = firstLine.trimStart()
        val lower = trimmed.lowercase()
        if (lower.startsWith("// region") || lower.startsWith("# region")) {
            val name = trimmed.substringAfter("region").trim()
            if (name.isNotEmpty()) return name
            return "region"
        }

        // Default placeholder
        return "..."
    }

    private fun extractAttribute(header: String, attr: String): String? {
        // Matches: attr="..." or attr='...'
        val dq = Regex("(?i)\\b" + Regex.escape(attr) + "\\s*=\\s*\"([^\"]*)\"")
        val sq = Regex("(?i)\\b" + Regex.escape(attr) + "\\s*=\\s*'([^']*)'")
        return dq.find(header)?.groupValues?.getOrNull(1)
            ?: sq.find(header)?.groupValues?.getOrNull(1)
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
