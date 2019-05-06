package com.giantbing.apkpackage.repository

import com.giantbing.apkpackage.model.SignInfo
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface SignRepository:ReactiveMongoRepository<SignInfo,String>