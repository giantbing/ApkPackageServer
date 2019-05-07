package com.giantbing.apkpackage.Request

import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.multipart.MultipartFile

data class SignRequest (val id:String, val file: FilePart,val alias: String,val pwd: String,val aliasPwd: String)