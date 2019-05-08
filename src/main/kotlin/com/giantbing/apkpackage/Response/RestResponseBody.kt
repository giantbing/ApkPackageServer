package com.giantbing.apkpackage.Response

class RestResponseBody<T> {
    var isSuccess: Boolean = true
    var msg: String = "ok"
    var data: T? = null
    fun setData(data: T): RestResponseBody<T> {
        this.data = data
        return this
    }

    fun isSuccess(boolean: Boolean): RestResponseBody<T> {
        this.isSuccess = boolean
        return this
    }

    fun setMsg(msg: String): RestResponseBody<T> {
        this.msg = msg
        return this
    }

}