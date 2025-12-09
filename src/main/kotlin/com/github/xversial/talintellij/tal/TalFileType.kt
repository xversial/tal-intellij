package com.github.xversial.talintellij.tal

import com.intellij.openapi.fileTypes.LanguageFileType
import com.github.xversial.talintellij.tal.icons.TalIcons
import javax.swing.Icon

class TalFileType : LanguageFileType(TalLanguage) {
    override fun getName(): String = "TAL"
    override fun getDescription(): String = "TAL and MAP files"
    override fun getDefaultExtension(): String = "tal"
    override fun getIcon(): Icon? = TalIcons.FILE

    companion object {
        @JvmField
        val INSTANCE = TalFileType()
    }
}
