package com.giantbing.apkpackage.Controller

import com.giantbing.apkpackage.Service.ApkOrderService
import com.giantbing.apkpackage.logger
import com.giantbing.apkpackage.model.ApkState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class WebController {

    @Autowired
    private lateinit var apkOrderService: ApkOrderService

    @RequestMapping("/")
    fun mainIndex(): String {
        return "index"
    }

    @RequestMapping("/welcome")
    fun welcome(): String {
        return "welcome"
    }

    @RequestMapping("/404")
    fun notFound(): String {
        return "404"
    }

    //任务管理页面
    @RequestMapping("/get-order", params = ["!id"])
    fun getOrder(model: Model): String {
        model.addAttribute("orders", apkOrderService.getAllApk())

        return "order"
    }

    //获取一任务物信息
    @RequestMapping("/get-order", params = ["id"])
    fun getOrderById(model: Model, @RequestParam id: String): String {

        model.addAttribute("orderItem", apkOrderService.getOneById(id))
        return "order-item"
    }

    //跳转 信息添加页面
    @RequestMapping("/order-add")
    fun orderAdd(): String {
        return "order-add"
    }

    //添加信息页面
    @RequestMapping("/add-order-info", params = ["id"])
    fun addOrderInfo(model: Model, @RequestParam id: String): String {
        model.addAttribute("orderItem", apkOrderService.getOneById(id))
        return "add-info"
    }

    //添加签名信息
    @RequestMapping("/add-sign-info", params = ["id"])
    fun addSignInfo(model: Model, @RequestParam id: String): String {
        model.addAttribute("orderItem", apkOrderService.getOneById(id))
        return "sign-add"
    }

    //获取成功后的页面
    @RequestMapping("/get-order-success", params = ["id"])
    fun getOrderByIdAndSuccess(model: Model, @RequestParam id: String): String {
        val data = apkOrderService.getOneById(id)
        model.addAttribute("orderItem", data)
        return "order-success-item"


    }



//    //上传APK信息页面
//    @RequestMapping("/upload-order")
//    fun uploadOrder(){
//
//    }

}