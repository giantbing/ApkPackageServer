package com.giantbing.apkpackage.repository

import com.giantbing.apkpackage.model.ApkHadleOrder
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ApkOrderRepository : ReactiveMongoRepository<ApkHadleOrder, String> {

}