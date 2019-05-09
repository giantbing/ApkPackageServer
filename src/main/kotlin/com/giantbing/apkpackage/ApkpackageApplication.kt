package com.giantbing.apkpackage

import com.giantbing.apkpackage.Utils.ApkCheackUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.system.ApplicationHome
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.util.ResourceUtils
import java.io.File
import java.nio.file.Files
import java.util.logging.Logger

@SpringBootApplication
@EnableMongoRepositories
@EnableTransactionManagement
class ApkpackageApplication : ApplicationRunner {
    @Value("\${jiagu.path}")
    private lateinit var jiaguPath: String
    @Value("\${jiagu.user}")
    private lateinit var jiaguUser: String
    @Value("\${jiagu.pwd}")
    private lateinit var jiaguPwd: String
    @Value("\${giantbing.docker}")
    private lateinit var appName:String
    @Value("\${jiagu.path.prefix}")
    private lateinit var jiaguPrefix: String


    override fun run(args: ApplicationArguments?) {
        Const.init(jiaguPrefix)
        logger.error(jiaguPrefix)
        logger.error(Const.ROOTPATH)
        logger.error("appName:{}",appName)
        ApkCheackUtil.setPathValue(File(Const.JIAGUROOTPATH + jiaguPath).absolutePath, jiaguUser, jiaguPwd)
        //ApkCheackUtil.testCmd()
    }
}

fun main(args: Array<String>) {
    runApplication<ApkpackageApplication>(*args)
}

val logger by lazy { LoggerFactory.getLogger(ApkpackageApplication::class.java) }