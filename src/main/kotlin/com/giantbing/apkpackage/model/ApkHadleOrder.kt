package com.giantbing.apkpackage.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class ApkHadleOrder(

        val info: ApkInfo,
        var state: ApkState,
        val createTime: Date,
        var updateTime: Date,
        var channelList: MutableList<MediaInfo>,
        var channelZip: MediaInfo? = null,
        var signInfo: SignInfo? = null,
        var channelFile: MediaInfo? = null,
        @Id
        val id: String? = null)

enum class ApkState {
    INIT, ERROR, HANDLE, SUCCESS
}


