package com.github.xversial.talintellij.tal.highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class TalColorSettingsPage : ColorSettingsPage {
    override fun getDisplayName(): String = "TAL"
    override fun getIcon(): Icon? = null
    override fun getHighlighter(): SyntaxHighlighter = TalSyntaxHighlighter()

    override fun getDemoText(): String = """
        // region Example Region
        if condition then
            log "Hello";
        else
            /* Block comment */
            execute something();
        endif
        // endregion

        <editor-fold desc="Folded block">
        text "inside fold";
        </editor-fold>

        // Multiline string and regex
        ${"\"\"\""}
        multi-line
        string
        ${"\"\"\""};
        #/(.*)@(.*)/i;
        ${'$'}1; ${'$'}line; ${'$'}file;
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = arrayOf(
        AttributesDescriptor("Keyword", TalSyntaxHighlighter.KEYWORD),
        AttributesDescriptor("Identifier", TalSyntaxHighlighter.IDENTIFIER),
        AttributesDescriptor("Number", TalSyntaxHighlighter.NUMBER),
        AttributesDescriptor("String", TalSyntaxHighlighter.STRING),
        AttributesDescriptor("Regex", TalSyntaxHighlighter.REGEX),
        AttributesDescriptor("Match group (${ '$' }1)", TalSyntaxHighlighter.MATCH_GROUP),
        AttributesDescriptor("Special literal (${ '$' }line/${ '$' }file)", TalSyntaxHighlighter.SPECIAL_LITERAL),
        AttributesDescriptor("Line comment", TalSyntaxHighlighter.LINE_COMMENT),
        AttributesDescriptor("Block comment", TalSyntaxHighlighter.BLOCK_COMMENT),
        AttributesDescriptor("Operator", TalSyntaxHighlighter.OPERATION_SIGN),
        AttributesDescriptor("Comma", TalSyntaxHighlighter.COMMA),
        AttributesDescriptor("Semicolon", TalSyntaxHighlighter.SEMICOLON),
        AttributesDescriptor("Dot", TalSyntaxHighlighter.DOT),
        AttributesDescriptor("Parentheses", TalSyntaxHighlighter.PARENTHESES),
        AttributesDescriptor("Braces", TalSyntaxHighlighter.BRACES),
        AttributesDescriptor("Brackets", TalSyntaxHighlighter.BRACKETS),
    )

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
}
