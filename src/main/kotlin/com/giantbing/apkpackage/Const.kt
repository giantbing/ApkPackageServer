package com.giantbing.apkpackage

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.system.ApplicationHome
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.io.File


object Const {
    //ApplicationHome(this::class.java)
    //val ROOTPATH: String = ApplicationHome(ApkpackageApplication::class.java).source.parentFile.absolutePath + "/upload/"

    private var jiaguPrefix: String = ""

    val ROOTPATH: String by lazy {   ResourceUtils.getFile(ResourceUtils.FILE_URL_PREFIX).absolutePath + "${jiaguPrefix}upload/"}
    val JIAGUROOTPATH: String by lazy { ResourceUtils.getFile(ResourceUtils.FILE_URL_PREFIX).absolutePath + jiaguPrefix}
    val UPLOADPATH: String by lazy { "${ROOTPATH}apk/"}
    val ICONPATH: String by lazy { "${ROOTPATH}icon/"}
    val CHANNELSPATH: String by lazy { "${ROOTPATH}channel/"}
    val SIGNPATH: String by lazy { "${ROOTPATH}sign/"}
    val OUTPath: String by lazy { "${UPLOADPATH}out/"}
    val OUTCHANNELPATH: String by lazy { "${OUTPath}channels/"}

    fun init(prefix: String) {
        this.jiaguPrefix = prefix
        val file0 = File(ROOTPATH)
        if (!file0.exists()) file0.mkdir()
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
        val file5 = File(OUTCHANNELPATH)
        if (!file5.exists()) file5.mkdir()

    }
}