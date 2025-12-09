package com.github.xversial.talintellij.tal.lexer

import com.github.xversial.talintellij.tal.TalLanguage
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

class TalTokenType(debugName: String) : IElementType(debugName, TalLanguage)

object TalTokens {
    // Punctuation & operators
    val OPERATOR = TalTokenType("OPERATOR")
    val COMMA = TalTokenType("COMMA")
    val DOT = TalTokenType("DOT")
    val COLON = TalTokenType("COLON")
    val SEMICOLON = TalTokenType("SEMICOLON")
    val LPAREN = TalTokenType("LPAREN")
    val RPAREN = TalTokenType("RPAREN")
    val LBRACE = TalTokenType("LBRACE")
    val RBRACE = TalTokenType("RBRACE")
    val LBRACKET = TalTokenType("LBRACKET")
    val RBRACKET = TalTokenType("RBRACKET")

    // Literals & identifiers
    val IDENTIFIER = TalTokenType("IDENTIFIER")
    val NUMBER = TalTokenType("NUMBER")
    val STRING = TalTokenType("STRING")
    val MULTILINE_STRING = TalTokenType("MULTILINE_STRING")
    val REGEX = TalTokenType("REGEX")
    val VARIABLE = TalTokenType("VARIABLE")
    val MATCH_GROUP = TalTokenType("MATCH_GROUP")
    val SPECIAL_LITERAL = TalTokenType("SPECIAL_LITERAL") // $line, $file

    // Keywords
    val KEYWORD = TalTokenType("KEYWORD")

    // Comments and whitespace
    val LINE_COMMENT = TalTokenType("LINE_COMMENT")
    val BLOCK_COMMENT = TalTokenType("BLOCK_COMMENT")
    val HASH_COMMENT = TalTokenType("HASH_COMMENT")

    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE

    val COMMENT_TOKENS: TokenSet = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT, HASH_COMMENT)
    val STRING_TOKENS: TokenSet = TokenSet.create(STRING, MULTILINE_STRING)
    val WHITESPACE_TOKENS: TokenSet = TokenSet.create(WHITE_SPACE)
}

val TAL_KEYWORDS: Set<String> = setOf(
    "if", "then", "else", "endif",
    "switch", "endswitch", "case", "default",
    "foreach", "endforeach", "in",
    "while", "endwhile", "loop", "endloop",
    "function", "endfunction", "plugin", "endplugin",
    "break", "continue", "return", "exit", "delete", "deletefirst", "deletelast",
    "execute", "assert", "invoke", "log", "goto",
    "success", "map", "text", "provider", "method",
    "writemap", "variable", "true", "false", "null",
    // operator-like keywords
    "matches", "contains", "eval", "not",
    // misc
    "on", "with", "at", "endinvoke"
)
