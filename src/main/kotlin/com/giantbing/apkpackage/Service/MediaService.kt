package com.giantbing.apkpackage.Service

import com.giantbing.apkpackage.model.MediaInfo
import com.giantbing.apkpackage.repository.MediaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MediaService {
    @Autowired
    private lateinit var mediaRepository: MediaRepository

    fun getMediaById(id:String):Mono<MediaInfo>{
        return mediaRepository.findById(id)
    }

    fun saveMedia(path:String):Mono<MediaInfo>{
        return mediaRepository.save(MediaInfo(path))
    }
}