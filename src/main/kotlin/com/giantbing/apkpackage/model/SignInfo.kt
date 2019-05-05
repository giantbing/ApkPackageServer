package com.giantbing.apkpackage.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class SignInfo(
        var signPath: MediaInfo,
        val alias: String,
        val pwd: String,
        val aliasPwd: String,
        @Id
        var id: String? = null
)