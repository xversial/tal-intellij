package com.github.xversial.talintellij.tal.psi

import com.github.xversial.talintellij.tal.TalLanguage
import com.github.xversial.talintellij.tal.TalFileType
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class TalFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, TalLanguage) {
    override fun getFileType(): FileType = TalFileType.INSTANCE
    override fun toString(): String = "TAL File"
}
