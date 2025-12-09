package com.github.xversial.talintellij.tal.highlighting

import com.github.xversial.talintellij.tal.lexer.TalLexer
import com.github.xversial.talintellij.tal.lexer.TalTokens
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class TalSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = TalLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = pack(ATTRS[tokenType])

    companion object {
        val KEYWORD: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD
        )
        val IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER
        )
        val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_NUMBER", DefaultLanguageHighlighterColors.NUMBER
        )
        val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_STRING", DefaultLanguageHighlighterColors.STRING
        )
        val REGEX: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_REGEX", DefaultLanguageHighlighterColors.STRING
        )
        val VARIABLE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_VARIABLE", DefaultLanguageHighlighterColors.PARAMETER
        )
        val MATCH_GROUP: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_MATCH_GROUP", DefaultLanguageHighlighterColors.CONSTANT
        )
        val SPECIAL_LITERAL: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_SPECIAL_LITERAL", DefaultLanguageHighlighterColors.CONSTANT
        )
        val LINE_COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT
        )
        val BLOCK_COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT
        )
        val OPERATION_SIGN: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
        val DOT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_DOT", DefaultLanguageHighlighterColors.DOT
        )
        val COMMA: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_COMMA", DefaultLanguageHighlighterColors.COMMA
        )
        val SEMICOLON: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON
        )
        val PARENTHESES: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_PAREN", DefaultLanguageHighlighterColors.PARENTHESES
        )
        val BRACES: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_BRACES", DefaultLanguageHighlighterColors.BRACES
        )
        val BRACKETS: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS
        )
        val BAD_CHAR: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
            "TAL_BAD_CHAR", HighlighterColors.BAD_CHARACTER
        )

        private val ATTRS: Map<IElementType, TextAttributesKey> = mapOf(
            TalTokens.KEYWORD to KEYWORD,
            TalTokens.IDENTIFIER to IDENTIFIER,
            TalTokens.NUMBER to NUMBER,
            TalTokens.STRING to STRING,
            TalTokens.MULTILINE_STRING to STRING,
            TalTokens.REGEX to REGEX,
            TalTokens.VARIABLE to VARIABLE,
            TalTokens.MATCH_GROUP to MATCH_GROUP,
            TalTokens.SPECIAL_LITERAL to SPECIAL_LITERAL,
            TalTokens.LINE_COMMENT to LINE_COMMENT,
            TalTokens.BLOCK_COMMENT to BLOCK_COMMENT,
            TalTokens.HASH_COMMENT to LINE_COMMENT,
            TalTokens.OPERATOR to OPERATION_SIGN,
            TalTokens.DOT to DOT,
            TalTokens.COMMA to COMMA,
            TalTokens.SEMICOLON to SEMICOLON,
            TalTokens.LPAREN to PARENTHESES,
            TalTokens.RPAREN to PARENTHESES,
            TalTokens.LBRACE to BRACES,
            TalTokens.RBRACE to BRACES,
            TalTokens.LBRACKET to BRACKETS,
            TalTokens.RBRACKET to BRACKETS,
        )
    }
}
