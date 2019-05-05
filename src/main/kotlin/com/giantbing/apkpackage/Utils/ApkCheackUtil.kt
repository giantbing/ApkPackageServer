package com.giantbing.apkpackage.Utils

import com.giantbing.apkpackage.Const
import com.giantbing.apkpackage.logger
import com.giantbing.apkpackage.model.ApkInfo
import com.giantbing.apkpackage.model.MediaInfo
import net.dongliu.apk.parser.ApkFile
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.File
import java.nio.file.Files

//用于检查 apk 等包信息
object ApkCheackUtil {
    private lateinit var jiaguPath: String
    private lateinit var jiaguUser: String
    private lateinit var jiaguPwd: String
    private lateinit var vasDollyPath: String
    fun setPathValue(jiaguPath: String, jiaguUser: String, jiaguPwd: String) {
        this.jiaguUser = jiaguUser
        this.jiaguPath = "$jiaguPath/jiagu.jar"
        this.jiaguPwd = jiaguPwd
        this.vasDollyPath = "$jiaguPath/VasDolly.jar"
    }

    private val jiaguLoginCmd: String by lazy {
        "java -jar $jiaguPath -login $jiaguUser $jiaguPwd"
    }
    private val jiaguSignCmd: String by lazy {
        "java -jar $jiaguPath -importsign ${Const.SIGNPATH}potato.jks 123123 key0 123123"
    }
//    private val jiaguCmd: String by lazy {
//        "java -jar $jiaguPath -jiagu /Users/giantbing/Downloads/apkpackage/target/classes/upload/apk/app-armeabi-v7a-debug.apk ${Const.OUTPath} -autosign"
//    }


    private fun getjiaguCmd(): String {
        val outPath = "${Const.OUTPath}potato_1/"
        File(outPath).mkdir()
        return "java -jar $jiaguPath -jiagu /Users/giantbing/Downloads/apkpackage/target/classes/upload/apk/app-armeabi-v7a-debug.apk $outPath -autosign"
    }


    private fun getVasDollyCmd(): Mono<Pair<Boolean, String?>> {

        val jiaguApkPath = File("${Const.OUTPath}potato_1/")
        var jiaguApk: File? = null
        jiaguApkPath.listFiles().toMutableList().forEach {
            if ("apk".equals(it.extension, true)) {
                jiaguApk = it
                return@forEach
            }
        }
        if (jiaguApk != null && jiaguApk!!.exists()) {
            val jiaguChannelPath = "${Const.OUTCHANNELPATH}potato_1/"
            return Mono.just(Pair(true, "java -jar $vasDollyPath put -c ${Const.CHANNELSPATH}channel.txt -f ${jiaguApk!!.absolutePath} $jiaguChannelPath"))
        }
        return Mono.just(Pair(false, null))
    }

    fun testCmd() {
        cmdCreat(jiaguLoginCmd)
                .flatMap {
                    cmdCreat(jiaguSignCmd)
                }
                .flatMap {
                    cmdCreat(getjiaguCmd())
                }.flatMap {
                    getVasDollyCmd()
                }.flatMap {
                    if (!it.first) {
                        Mono.just(1)
                    } else {
                        cmdCreat(it.second!!)
                    }
                }
                .subscribeOn(Schedulers.newSingle("cmd"))
                .subscribe {
                    logger.error("resualt{}", it)
                }


    }

    private fun cmdCreat(cmd: String): Mono<Int> {
        return Mono.create<Int> {
            logger.info("cmd:{}", cmd)
            val runtime = Runtime.getRuntime()
            val process = runtime.exec(cmd)
            it.success(process.waitFor())
        }
    }

    fun handleApk(file: File) {

    }


    fun getApkInfo(file: File): ApkInfo? {
        if (file.exists()) {
            val apkParser = ApkFile(file)
            val apkMeta = apkParser.apkMeta
            val iconList = apkParser.allIcons
            var iconPath = ""
            for (icon in iconList) {
                if (icon.isFile) {
                    val iconFile = File(Const.ICONPATH + apkMeta.packageName + "_" + apkMeta.versionCode + ".png")
                    Files.write(iconFile.toPath(), icon.data)
                    iconPath = iconFile.absolutePath
                    break
                }
            }

            val apkinfo = ApkInfo(apkMeta.label,
                    apkMeta.versionCode.toString(),
                    apkMeta.versionName,
                    apkMeta.packageName,
                    MediaInfo(iconPath), MediaInfo(filePath = file.absolutePath))
            apkParser.close()
            return apkinfo

        }
        return null
    }
}

