package com.github.xversial.talintellij.tal.psi

import com.github.xversial.talintellij.tal.TalLanguage
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType

object TalElementTypes {
    @JvmField
    val FILE: IFileElementType = IFileElementType(TalLanguage)
}

class TalElementType(debugName: String) : IElementType(debugName, TalLanguage)
