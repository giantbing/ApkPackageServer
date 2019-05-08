package com.giantbing.apkpackage.Service

import com.giantbing.apkpackage.Const
import com.giantbing.apkpackage.Utils.ApkCheackUtil
import com.giantbing.apkpackage.Response.RestResponseBody
import com.giantbing.apkpackage.Utils.FileUtils
import com.giantbing.apkpackage.logger
import com.giantbing.apkpackage.model.ApkHadleOrder
import com.giantbing.apkpackage.model.ApkState
import com.giantbing.apkpackage.model.MediaInfo
import com.giantbing.apkpackage.model.SignInfo
import com.giantbing.apkpackage.repository.ApkInfoRepository
import com.giantbing.apkpackage.repository.ApkOrderRepository
import com.giantbing.apkpackage.repository.MediaRepository
import com.giantbing.apkpackage.repository.SignRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorReturn
import reactor.core.publisher.toMono
import reactor.core.scheduler.Schedulers
import java.io.File
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class ApkOrderService {
    @Autowired
    private lateinit var apkOrderRepository: ApkOrderRepository
    @Autowired
    private lateinit var apkInfoRepository: ApkInfoRepository
    @Autowired
    private lateinit var mediaRepository: MediaRepository
    @Autowired
    private lateinit var signRepository: SignRepository

    fun getAllApk(): Flux<ApkHadleOrder> {
        return apkOrderRepository.findAll()
    }

    fun saveOneOrder(file: File): Mono<RestResponseBody<ApkHadleOrder>> {
        val apkInfo = ApkCheackUtil.getApkInfo(file)
                ?: return Mono.just(RestResponseBody<ApkHadleOrder>().isSuccess(false).setMsg("获取apk信息失败"))
        val apkFileMedia = apkInfo.filePath
        val apkIconFileMedia = apkInfo.iconPath
        return apkInfoRepository.findAllByPackageNameAndCode(apkInfo.packageName, apkInfo.code).count().flatMap {

            if (it > 0) {
                Mono.just(RestResponseBody<ApkHadleOrder>().isSuccess(false).setMsg("已有进行中的任务"))
            } else {
                mediaRepository.save(apkFileMedia)
                        .zipWith(mediaRepository.save(apkIconFileMedia)) { t, u ->
                            Pair(t, u)
                        }.flatMap { pair ->
                            apkInfo.filePath = pair.first
                            apkInfo.iconPath = pair.second
                            apkInfoRepository.save(apkInfo)
                        }.flatMap {
                            apkOrderRepository.save(ApkHadleOrder(it, ApkState.INIT, Date(), Date(), mutableListOf()))
                        }.flatMap { data ->
                            Mono.just(RestResponseBody<ApkHadleOrder>().setData(data))
                        }.doOnError { t ->
                            logger.error("postapk", t)
                            Mono.just(RestResponseBody<ApkHadleOrder>().setMsg(t.toString()))
                        }
            }


        }


    }

    fun updateOrder(order: ApkHadleOrder): Mono<ApkHadleOrder> {
        return apkOrderRepository.save(order)
    }

    fun updateOrderZipFile(id: String,mediaInfo: MediaInfo): Mono<ApkHadleOrder> {
        return apkOrderRepository.findById(id)
                .flatMap {
                    val copy = it.copy(channelZip =mediaInfo )
                    apkOrderRepository.save(copy)
                }

    }
    fun addSignOrder(id: String, sign: SignInfo): Mono<ApkHadleOrder> {
        return getOneById(id).flatMap {
            val order = it
            it.signInfo = sign
            it.updateTime = Date()
            updateOrder(order)
        }
    }

    fun addChannel(id: String, channel: MediaInfo): Mono<ApkHadleOrder> {
        return getOneById(id).flatMap {
            val order = it
            it.channelFile = channel
            it.updateTime = Date()
            updateOrder(order)
        }
    }

    fun getOneById(id: String): Mono<ApkHadleOrder> {
        return apkOrderRepository.findById(id)
    }

    @Throws(Exception::class)
    fun deleteOrderById(id: String): Mono<RestResponseBody<Unit>> {
        var data: ApkHadleOrder? = null
        return apkOrderRepository.findById(id)
                .doOnNext {
                    data = it
                }
                .flatMapMany { _ ->
                    val apk = data!!
                    val list = mutableListOf<MediaInfo>()
                    list.addAll(apk.channelList)
                    apk.channelFile?.let {
                        list.add(it)
                    }
                    apk.signInfo?.signPath?.let {
                        list.add(it)
                    }
                    apk.channelZip?.let {
                        list.add(it)
                    }
                    list.add(apk.info.filePath)
                    list.add(apk.info.iconPath)

                    Flux.fromIterable(list)
                }
                .doOnNext {
                    if (File(it.filePath).exists()) {
                        Files.delete(Paths.get(it.filePath))
                    }
                }.doOnComplete {
                    apkOrderRepository.deleteById(id).subscribe()

                    apkInfoRepository.delete(data!!.info).subscribe()
                    if (data!!.signInfo != null) {
                        signRepository.delete(data!!.signInfo!!).subscribe()
                    }
                    //删除一下 文件夹
                    FileUtils.deleteDir(File("${Const.OUTCHANNELPATH}${data!!.id}/"))
                    FileUtils.deleteDir(File("${Const.CHANNELSPATH}${data!!.id}/"))
                    FileUtils.deleteDir(File("${Const.SIGNPATH}${data!!.id}/"))
                }
                .collectList()
                .doOnNext {
                    mediaRepository.deleteAll(it).subscribe()
                }

                .flatMap {
                    RestResponseBody<Unit>().toMono()
                }


    }

}

