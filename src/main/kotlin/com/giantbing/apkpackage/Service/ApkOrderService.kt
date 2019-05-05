package com.giantbing.apkpackage.Service

import com.giantbing.apkpackage.Utils.ApkCheackUtil
import com.giantbing.apkpackage.Response.RestResponseBody
import com.giantbing.apkpackage.model.ApkHadleOrder
import com.giantbing.apkpackage.model.ApkState
import com.giantbing.apkpackage.repository.ApkInfoRepository
import com.giantbing.apkpackage.repository.ApkOrderRepository
import com.giantbing.apkpackage.repository.MediaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.util.*

@Service
class ApkOrderService {
    @Autowired
    private lateinit var apkOrderRepository: ApkOrderRepository
    @Autowired
    private lateinit var apkInfoRepository: ApkInfoRepository
    @Autowired
    private lateinit var mediaRepository: MediaRepository

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
                        }
            }


        }


    }

    fun isHasOrder(file: File): Boolean {
        val apkInfo = ApkCheackUtil.getApkInfo(file) ?: return true

        return false
    }

}

