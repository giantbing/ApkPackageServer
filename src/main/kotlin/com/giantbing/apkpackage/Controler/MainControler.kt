package com.giantbing.apkpackage.Controler

import com.giantbing.apkpackage.Const
import com.giantbing.apkpackage.Utils.FileUtils
import com.giantbing.apkpackage.Response.RestResponseBody
import com.giantbing.apkpackage.Service.ApkOrderService
import com.giantbing.apkpackage.Service.MediaService
import com.giantbing.apkpackage.logger
import com.giantbing.apkpackage.model.ApkHadleOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.nio.file.Paths
import java.io.*
import java.nio.file.Files


@Controller
@RequestMapping("/apk")
class MainControler {


    @Autowired
    private lateinit var apkOrderService: ApkOrderService
    @Autowired
    private lateinit var mediaService:MediaService

    @PostMapping("/postApk", consumes = ["multipart/form-data"])
    @ResponseBody
    fun postApk(@RequestPart("file") file: FilePart): Mono<RestResponseBody<ApkHadleOrder>> {

//
        val tempFile = Files.createTempFile("temp", file.filename())
        if (!FileUtils.isApkFile(tempFile.toFile())) {
            return Mono.just(RestResponseBody<ApkHadleOrder>().isSuccess(false).setMsg("请检查文件类型 apk"))
        }
        val path = Paths.get(Const.UPLOADPATH + file.filename())

        try {
            file.transferTo(path)

        } catch (e: IOException) {
            logger.error("/postapk", e)
        }

        return apkOrderService.saveOneOrder(path.toFile())

//         RestResponseBody<Unit>().isSuccess(true).setMsg("上传成功").toMono()
    }

    @GetMapping("/download", params = ["id"])
    @ResponseBody
    fun downloadApk(response: ServerHttpResponse, @RequestParam id: String): Mono<Void> {

        return mediaService.getMediaById(id = id).flatMap {
            return@flatMap FileUtils.downloadFile(response, it.filePath)
        }

    }

}