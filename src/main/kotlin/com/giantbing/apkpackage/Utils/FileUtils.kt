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
        //response.getHeaders().contentType = MediaType.ALL


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

    fun findAllApkFile(path: File): MutableList<File> {
        val list = mutableListOf<File>()
        path.listFiles().toMutableList().forEach {
            if ("apk".equals(it.extension, true)) {
                list.add(it)
            }
        }
        return list
    }

    fun doDeleteEmptyDir(dir: String) {
        val success = File(dir).delete()
        if (success) {
            println("Successfully deleted empty directory: $dir")
        } else {
            println("Failed to delete empty directory: $dir")
        }
    }

    fun deleteDir(dir: File) {
        if (dir.isDirectory) {
            val children = dir.list()
            children.forEachIndexed { index, s ->
                deleteDir(File(dir, children[index]))
            }
        }
        dir.delete()
    }
}

val MultipartFile.extension: String?
    get() = originalFilename?.substringAfterLast('.', "")