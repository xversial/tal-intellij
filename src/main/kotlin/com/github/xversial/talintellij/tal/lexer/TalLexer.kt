package com.github.xversial.talintellij.tal.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class TalLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset: Int = 0
    private var endOffset: Int = 0
    private var state: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.state = initialState
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.tokenType = null
        advance()
    }

    override fun getState(): Int = state
    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd
    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = endOffset

    override fun advance() {
        if (tokenEnd >= endOffset) {
            tokenType = null
            tokenStart = endOffset
            tokenEnd = endOffset
            return
        }

        var i = if (tokenEnd == 0) startOffset else tokenEnd
        // Skip whitespace
        if (i < endOffset && buffer[i].isWhitespace()) {
            val wsStart = i
            i++
            while (i < endOffset && buffer[i].isWhitespace()) i++
            tokenStart = wsStart
            tokenEnd = i
            tokenType = TokenType.WHITE_SPACE
            return
        }

        val c = buffer[i]
        val prev = if (i - 1 >= startOffset) buffer[i - 1] else '\n'

        // Line comments
        if (c == '/' && i + 1 < endOffset && buffer[i + 1] == '/') {
            val start = i
            i += 2
            while (i < endOffset && buffer[i] != '\n' && buffer[i] != '\r') i++
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.LINE_COMMENT
            return
        }
        // Hash line comments, but not regex ("#/...") at line start
        if (c == '#' && (i == startOffset || prev == '\n' || prev == '\r') && !(i + 1 < endOffset && buffer[i + 1] == '/')) {
            val start = i
            i++
            while (i < endOffset && buffer[i] != '\n' && buffer[i] != '\r') i++
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.HASH_COMMENT
            return
        }

        // Block comment
        if (c == '/' && i + 1 < endOffset && buffer[i + 1] == '*') {
            val start = i
            i += 2
            while (i < endOffset - 1 && !(buffer[i] == '*' && buffer[i + 1] == '/')) i++
            if (i < endOffset - 1) i += 2
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.BLOCK_COMMENT
            return
        }

        // Multiline string literal """..."""
        if (c == '"' && i + 2 < endOffset && buffer[i + 1] == '"' && buffer[i + 2] == '"') {
            val start = i
            i += 3
            while (i + 2 < endOffset && !(buffer[i] == '"' && buffer[i + 1] == '"' && buffer[i + 2] == '"')) {
                i++
            }
            if (i + 2 < endOffset) i += 3
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.MULTILINE_STRING
            return
        }

        // String literal "..."
        if (c == '"') {
            val start = i
            i++
            var escaped = false
            while (i < endOffset) {
                val ch = buffer[i]
                if (escaped) {
                    escaped = false
                } else if (ch == '\\') {
                    escaped = true
                } else if (ch == '"') {
                    i++
                    break
                } else if (ch == '\n' || ch == '\r') {
                    // terminate at EOL for safety
                    break
                }
                i++
            }
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.STRING
            return
        }

        // Variable interpolation ${...}
        if (c == '$' && i + 1 < endOffset && buffer[i + 1] == '{') {
            val start = i
            i += 2
            var depth = 1
            loop@ while (i < endOffset) {
                val ch = buffer[i]
                when (ch) {
                    '{' -> depth++
                    '}' -> {
                        depth--
                        if (depth == 0) { i++; break@loop }
                    }
                    '\n', '\r' -> break@loop
                }
                i++
            }
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.VARIABLE
            return
        }

        // Regex literal #/.../flags
        if (c == '#' && i + 1 < endOffset && buffer[i + 1] == '/') {
            val start = i
            i += 2
            var escaped = false
            var inClass = false
            while (i < endOffset) {
                val ch = buffer[i]
                if (escaped) {
                    escaped = false
                } else when (ch) {
                    '\\' -> escaped = true
                    '[' -> inClass = true
                    ']' -> inClass = false
                    '/' -> if (!inClass) { i++; break }
                }
                i++
            }
            // flags
            while (i < endOffset && buffer[i].isLetter()) i++
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.REGEX
            return
        }

        // Match group $0, $1, $12 ... or special $line / $file
        if (c == '$' && i + 1 < endOffset && buffer[i + 1].isDigit()) {
            val start = i
            i += 2
            while (i < endOffset && buffer[i].isDigit()) i++
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.MATCH_GROUP
            return
        }
        if (c == '$' && i + 5 <= endOffset) {
            val rem = buffer.subSequence(i, minOf(i + 6, endOffset)).toString()
            when {
                rem.startsWith("\$line") -> {
                    tokenStart = i
                    tokenEnd = i + 5
                    tokenType = TalTokens.SPECIAL_LITERAL
                    return
                }
                rem.startsWith("\$file") -> {
                    tokenStart = i
                    tokenEnd = i + 5
                    tokenType = TalTokens.SPECIAL_LITERAL
                    return
                }
            }
        }

        // Numbers
        if (c.isDigit()) {
            val start = i
            // Hex 0x...
            if (c == '0' && i + 1 < endOffset && (buffer[i + 1] == 'x' || buffer[i + 1] == 'X')) {
                i += 2
                while (i < endOffset && buffer[i].isHexDigit()) i++
            } else {
                i++
                var hasDot = false
                while (i < endOffset) {
                    val ch = buffer[i]
                    if (ch == '.' && !hasDot) { hasDot = true; i++; continue }
                    if (!ch.isDigit()) break
                    i++
                }
            }
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.NUMBER
            return
        }

        // Based integer: <radix>#<value> or #<value>
        if (c == '#' || (c.isDigit() && run {
                var j = i
                while (j < endOffset && buffer[j].isDigit()) j++
                j < endOffset && buffer[j] == '#'
            })) {
            val start = i
            if (c == '#') {
                // default base value
                i++
            } else {
                // consume radix
                while (i < endOffset && buffer[i].isDigit()) i++
                if (i < endOffset && buffer[i] == '#') i++
            }
            // consume value (letters/digits allowed)
            while (i < endOffset && (buffer[i].isLetterOrDigit())) i++
            tokenStart = start
            tokenEnd = i
            tokenType = TalTokens.NUMBER
            return
        }

        // Identifiers / keywords
        if (c.isIdentifierStart()) {
            val start = i
            i++
            while (i < endOffset && buffer[i].isIdentifierPart()) i++
            tokenStart = start
            tokenEnd = i
            val text = buffer.subSequence(start, i).toString()
            tokenType = if (TAL_KEYWORDS.contains(text.lowercase())) TalTokens.KEYWORD else TalTokens.IDENTIFIER
            return
        }

        // Single-char tokens & operators
        val start = i
        when (c) {
            ',' -> { i++; tokenType = TalTokens.COMMA }
            '.' -> { i++; tokenType = TalTokens.DOT }
            ':' -> { i++; tokenType = TalTokens.COLON }
            ';' -> { i++; tokenType = TalTokens.SEMICOLON }
            '(' -> { i++; tokenType = TalTokens.LPAREN }
            ')' -> { i++; tokenType = TalTokens.RPAREN }
            '{' -> { i++; tokenType = TalTokens.LBRACE }
            '}' -> { i++; tokenType = TalTokens.RBRACE }
            '[' -> { i++; tokenType = TalTokens.LBRACKET }
            ']' -> { i++; tokenType = TalTokens.RBRACKET }
            else -> {
                // Operator or unknown char
                i++
                // Combine two-char operators
                if (i < endOffset) {
                    val pair = "" + c + buffer[i]
                    if (pair in arrayOf("==", "!=", ">=", "<=", "&&", "||", "->", "=>")) {
                        i++
                    }
                }
                tokenType = TalTokens.OPERATOR
            }
        }
        tokenStart = start
        tokenEnd = i
    }
}

private fun Char.isIdentifierStart(): Boolean = this == '_' || this.isLetter()
private fun Char.isIdentifierPart(): Boolean = this == '_' || this == '-' || this.isLetterOrDigit()
private fun Char.isHexDigit(): Boolean = this.isDigit() || (this in 'a'..'f') || (this in 'A'..'F')
