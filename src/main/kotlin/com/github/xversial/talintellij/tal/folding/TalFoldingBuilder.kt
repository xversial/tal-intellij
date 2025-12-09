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
            descriptors += FoldingDescriptor(root.node, range)
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
                        descriptors += FoldingDescriptor(root.node, range)
                    }
                }
            }
            lineStart = lineEnd + 1
        }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? = "..."

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
