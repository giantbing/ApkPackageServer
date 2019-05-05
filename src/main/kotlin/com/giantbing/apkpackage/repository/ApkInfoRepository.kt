package com.giantbing.apkpackage.repository

import com.giantbing.apkpackage.model.ApkInfo
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ApkInfoRepository : ReactiveMongoRepository<ApkInfo, String> {
    fun findAllByPackageNameAndCode(packageName: String, code: String): Flux<ApkInfo>
}