package com.giantbing.apkpackage.repository

import com.giantbing.apkpackage.model.MediaInfo
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface MediaRepository : ReactiveMongoRepository<MediaInfo, String> {
}