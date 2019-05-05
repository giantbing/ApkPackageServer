package com.giantbing.apkpackage.Utils

import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.multipart.MultipartFile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ZeroCopyHttpOutputMessage
import reactor.core.publisher.Mono
import java.io.File


object FileUtils {

    fun downloadFile(response: ServerHttpResponse, file: File): Mono<Void> {

        val fileName = file.name
        val zeroCopyResponse = response as ZeroCopyHttpOutputMessage
        response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")
        response.getHeaders().contentType = MediaType.IMAGE_PNG


        return zeroCopyResponse.writeWith(file, 0, file.length())


    }


    fun downloadFile(response: ServerHttpResponse, filePath: String): Mono<Void> {
        return downloadFile(response, File(filePath))
    }

    //检查apk文件
    fun isApkFile(file: File): Boolean {
        val suffix = file.extension
        return "apk".equals(suffix, true)

    }

    fun isApkFile(file: MultipartFile): Boolean {
        return "apk".equals(file.extension, true)
    }


}

val MultipartFile.extension: String?
    get() = originalFilename?.substringAfterLast('.', "")