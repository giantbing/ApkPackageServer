package com.giantbing.apkpackage.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class ApkHadleOrder(

        val info: ApkInfo,
        val state: ApkState,
        val createTime: Date,
        val updateTime: Date,
        var channelList: MutableList<ApkInfo>,
        var signInfo: SignInfo? = null,
        var channelFile: MediaInfo? = null,
        @Id
        val id: String? = null)

enum class ApkState {
    INIT, ERROR, HANDLE,SUCCESS
}


