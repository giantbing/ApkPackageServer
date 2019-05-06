package com.giantbing.apkpackage.Service

import com.giantbing.apkpackage.model.SignInfo
import com.giantbing.apkpackage.repository.SignRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SignService {
    @Autowired
    private lateinit var signRepository:SignRepository

    fun saveOne(sign:SignInfo): Mono<SignInfo>{
       return signRepository.save(sign)
    }
}