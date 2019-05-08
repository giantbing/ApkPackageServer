package com.giantbing.apkpackage.Utils

import com.giantbing.apkpackage.Const
import com.giantbing.apkpackage.logger
import com.giantbing.apkpackage.model.ApkHadleOrder
import com.giantbing.apkpackage.model.ApkInfo
import com.giantbing.apkpackage.model.MediaInfo
import net.dongliu.apk.parser.ApkFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import reactor.core.scheduler.Schedulers
import java.io.File
import java.lang.IllegalArgumentException
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


    private fun getJiaguSignCmd(apk: ApkHadleOrder): String {

        return "java -jar $jiaguPath -importsign ${apk.signInfo!!.signPath.filePath} ${apk.signInfo!!.pwd} ${apk.signInfo!!.alias} ${apk.signInfo!!.aliasPwd}"
    }

    //存储 加固好的包的位置
    private fun getjiaguCmd(apk: ApkHadleOrder): String {
        val outPath = "${Const.OUTPath}${apk.id}/"
        File(outPath).mkdir()
        return "java -jar $jiaguPath -jiagu ${apk.info.filePath.filePath} $outPath -autosign"
    }


    private fun getVasDollyCmd(apk: ApkHadleOrder): Mono<Pair<Boolean, String?>> {

        val jiaguApkPath = File("${Const.OUTPath}${apk.id}/")
        var jiaguApk: File? = null
        val apkList = FileUtils.findAllApkFile(jiaguApkPath)
        if (apkList.isNotEmpty()) jiaguApk = apkList[0]

        if (jiaguApk != null && jiaguApk.exists()) {
            val jiaguChannelPath = "${Const.OUTCHANNELPATH}${apk.id}/"
            File(jiaguChannelPath).mkdir()
            return Mono.just(Pair(true, "java -jar $vasDollyPath put -c ${apk.channelFile!!.filePath} -f ${jiaguApk.absolutePath} $jiaguChannelPath"))
        }
        return Mono.just(Pair(false, null))
    }

    private fun getJiaguApk(apk: ApkHadleOrder): String? {
        val jiaguApkPath = File("${Const.OUTPath}${apk.id}/")
        var jiaguApk: File? = null
        val apkList = FileUtils.findAllApkFile(jiaguApkPath)
        if (apkList.isNotEmpty()) jiaguApk = apkList[0]

        return jiaguApk?.absolutePath
    }

    fun handleApk(apk: ApkHadleOrder): Flux<File> {
        return cmdCreat(jiaguLoginCmd).flatMap {
            if (it > 0) throw IllegalArgumentException()
            cmdCreat(getJiaguSignCmd(apk))
        }.flatMap {
            if (it > 0) throw IllegalArgumentException()
            cmdCreat(getjiaguCmd(apk))
        }.flatMap {
            if (it > 0) throw IllegalArgumentException()
            getVasDollyCmd(apk)
        }.flatMap {
            if (!it.first) {
                Mono.just(1)
                throw IllegalArgumentException()
            } else {
                cmdCreat(it.second!!)
            }
        }.flatMapMany {
            return@flatMapMany Flux.fromIterable(FileUtils.findAllApkFile(File("${Const.OUTCHANNELPATH}${apk.id}/")))
        }.subscribeOn(Schedulers.newSingle("cmd"))
                .doOnComplete {
                    //删掉加固的包
                    val jiaguApk = File(getJiaguApk(apk))
                    if (jiaguApk.exists()) jiaguApk.delete()
                    FileUtils.deleteDir(File("${Const.OUTPath}${apk.id}/"))
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
                    MediaInfo(iconPath, File(iconPath).name), MediaInfo(filePath = file.absolutePath, fileName = file.name))
            apkParser.close()
            return apkinfo

        }
        return null
    }
}

