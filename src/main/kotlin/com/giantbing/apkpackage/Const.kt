package com.giantbing.apkpackage

import org.springframework.util.ResourceUtils
import java.io.File

object Const {
    val ROOTPATH: String = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX).absolutePath + "/upload/"
    val UPLOADPATH: String = "${ROOTPATH}apk/"
    val ICONPATH: String = "${ROOTPATH}icon/"
    val CHANNELSPATH: String = "${ROOTPATH}channel/"
    val SIGNPATH: String = "${ROOTPATH}sign/"
    val OUTPath:String="${UPLOADPATH}out/"
    val OUTCHANNELPATH:String="${OUTPath}channels/"

    fun init() {
        val file = File(UPLOADPATH)
        if (!file.exists()) file.mkdir()
        val file1 = File(ICONPATH)
        if (!file1.exists()) file1.mkdir()
        val file2 = File(CHANNELSPATH)
        if (!file2.exists()) file2.mkdir()
        val file3 = File(SIGNPATH)
        if (!file3.exists()) file3.mkdir()
        val file4 = File(OUTPath)
        if (!file4.exists()) file4.mkdir()
        val file5=File(OUTCHANNELPATH)
        if (!file5.exists()) file5.mkdir()

    }
}