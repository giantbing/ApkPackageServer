package com.giantbing.apkpackage.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class ApkInfo(val name: String, val code: String, val versionName: String, val packageName: String, var  iconPath: MediaInfo,
                   var filePath: MediaInfo,
                   @Id
                   val id: String? = null)