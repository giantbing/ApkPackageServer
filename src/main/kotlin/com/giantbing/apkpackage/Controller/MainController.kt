package com.giantbing.apkpackage.Controller

import com.giantbing.apkpackage.Const
import com.giantbing.apkpackage.Request.SignRequest
import com.giantbing.apkpackage.Utils.FileUtils
import com.giantbing.apkpackage.Response.RestResponseBody
import com.giantbing.apkpackage.Service.ApkOrderService
import com.giantbing.apkpackage.Service.MediaService
import com.giantbing.apkpackage.Service.SignService
import com.giantbing.apkpackage.Utils.ApkCheackUtil
import com.giantbing.apkpackage.Utils.ZipUtils
import com.giantbing.apkpackage.model.ApkHadleOrder
import com.giantbing.apkpackage.model.ApkState
import com.giantbing.apkpackage.model.MediaInfo
import com.giantbing.apkpackage.model.SignInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.io.File
import java.lang.RuntimeException
import java.nio.file.Paths
import java.nio.file.Files
import java.util.*
import javax.validation.Valid


@Controller
@RequestMapping("/apk")
class MainController {


    @Autowired
    private lateinit var apkOrderService: ApkOrderService
    @Autowired
    private lateinit var mediaService: MediaService
    @Autowired
    private lateinit var signService: SignService

    @PostMapping("/post-apk", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun postApk(@RequestPart("file") file: FilePart): Mono<RestResponseBody<ApkHadleOrder>> {

//
        val tempFile = Files.createTempFile("temp", file.filename())
        if (!FileUtils.isApkFile(tempFile.toFile())) {
            return Mono.just(RestResponseBody<ApkHadleOrder>().isSuccess(false).setMsg("请检查文件类型 apk"))
        }
        val path = Paths.get(Const.UPLOADPATH + file.filename())

        file.transferTo(path)


        return apkOrderService.saveOneOrder(path.toFile())


    }

    @GetMapping("/download", params = ["id"])
    @ResponseBody
    fun downloadApk(response: ServerHttpResponse, @RequestParam id: String): Mono<Void> {

        return mediaService.getMediaById(id = id).flatMap {
            return@flatMap FileUtils.downloadFile(response, it.filePath)
        }

    }

    @PostMapping("/upload-sign", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun uploadSign(@Valid @ModelAttribute request: SignRequest, errors: BindingResult): Mono<RestResponseBody<Unit>> {
        if (errors.hasErrors()) {
            return RestResponseBody<Unit>().isSuccess(false).setMsg(errors.toString()).toMono()
        }
        //  val tempFile = Files.createTempFile("temp", file.filename())
        val dir = Paths.get(Const.SIGNPATH + request.id + "/")
        dir.toFile().mkdir()
        val path = Paths.get(Const.SIGNPATH + request.id + "/" + request.file.filename())
        request.file.transferTo(path)
        return mediaService.saveMedia(path = path.toFile().absolutePath)
                .flatMap { media ->
                    signService.saveOne(SignInfo(media, request.alias, request.pwd, request.aliasPwd))
                }.flatMap { sign ->
                    apkOrderService.addSignOrder(request.id, sign)
                }.flatMap {
                    RestResponseBody<Unit>().toMono()
                }
    }

    @PostMapping("/test")
    @ResponseBody
    fun test(@Valid @ModelAttribute request: SignRequest, errors: BindingResult): Mono<RestResponseBody<Unit>> {
        if (errors.hasErrors()) {
            return RestResponseBody<Unit>().isSuccess(false).setMsg(errors.toString()).toMono()
        }
        return RestResponseBody<Unit>().setMsg(request.toString()).toMono()
    }

    @PostMapping("/upload-channel/{id}")
    @ResponseBody
    fun uploadChannel(@RequestPart("file") file: FilePart, @PathVariable id: String): Mono<RestResponseBody<Unit>> {

        //  val tempFile = Files.createTempFile("temp", file.filename())

        if (id.isEmpty()) {
            return RestResponseBody<Unit>().isSuccess(false).setMsg("id不能为空").toMono()
        }
        val dir = Paths.get(Const.CHANNELSPATH + id + "/")
        dir.toFile().mkdir()
        val path = Paths.get(Const.CHANNELSPATH + id + "/" + file.filename())
        file.transferTo(path)

        return mediaService.saveMedia(path = path.toFile().absolutePath)
                .flatMap { media ->
                    apkOrderService.addChannel(id, media)
                }.flatMap {
                    RestResponseBody<Unit>().toMono()
                }
    }


    @GetMapping("/start-sign", params = ["id"])
    @ResponseBody
    fun startSign(@RequestParam id: String): Mono<RestResponseBody<Unit>> {

        val apkOrder = apkOrderService.getOneById(id)
        return apkOrder.flatMap {
            if (it.channelFile != null && it.signInfo != null) {
                it.updateTime = Date()
                it.state=ApkState.HANDLE
                apkOrderService.updateOrder(it).subscribe()
                handleApk(it)
                return@flatMap RestResponseBody<Unit>().toMono()
            } else {
                return@flatMap RestResponseBody<Unit>().setMsg("文件不全").isSuccess(false).toMono()
            }
        }

    }

    //删除任务，默认会删除文件
    @RequestMapping("/order-delete",params = ["id"])
    @ResponseBody
    fun deleteOrder(@RequestParam id:String):Mono<RestResponseBody<Unit>>{
      return  apkOrderService.deleteOrderById(id)
    }

    private fun handleApk(apk: ApkHadleOrder) {
        ApkCheackUtil.handleApk(apk)
                .flatMap {
                    mediaService.saveMedia(it.path)
                }
                .collectList()
                .flatMap {
                    val apkOrder = apk.copy( updateTime = Date(), channelList = it)
                    apkOrderService.updateOrder(apkOrder)
                }.flatMap {
                    return@flatMap ZipUtils.reativeCompress("${Const.OUTCHANNELPATH}${apk.id}/","${Const.OUTCHANNELPATH}/${it.info.name}.zip")
                }.flatMap {
                    if (!File(it).exists()) throw RuntimeException("未找到文件")
                    return@flatMap mediaService.saveMedia(it)
                }.flatMap {
                    return@flatMap apkOrderService.updateOrderZipFile(apk.id!!,it)
                }.flatMap {
                    val apkOrder = apk.copy(state = ApkState.SUCCESS, updateTime = Date())
                    apkOrderService.updateOrder(apkOrder)
                }
                .doOnError {
                    val apkOrder = apk.copy(state = ApkState.ERROR, updateTime = Date())
                    apkOrderService.updateOrder(apkOrder).subscribe()
                }
                .subscribe()
    }


}