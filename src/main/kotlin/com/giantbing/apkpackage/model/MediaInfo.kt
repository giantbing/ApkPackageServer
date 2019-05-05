package com.giantbing.apkpackage.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class MediaInfo(

        val filePath: String,
        @Id
        val id: String? = null)