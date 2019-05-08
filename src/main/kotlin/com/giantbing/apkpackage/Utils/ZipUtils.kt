package com.giantbing.apkpackage.Utils

import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.zip.ZipEntry
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.util.zip.ZipOutputStream
import java.io.IOException
import java.util.zip.CRC32
import java.util.zip.CheckedOutputStream
import java.io.FileNotFoundException
import java.io.File


object ZipUtils {
    private const val BUFFER = 8192


    fun reativeCompress(srcPath: String, dstPath: String): Mono<String> {
        return compress(srcPath, dstPath).toMono()
    }

    @Throws(IOException::class)
    fun compress(srcPath: String, dstPath: String): String {
        val srcFile = File(srcPath)
        val dstFile = File(dstPath)
        if (!srcFile.exists()) {
            throw FileNotFoundException(srcPath + "不存在！")
        }

        var out: FileOutputStream? = null
        var zipOut: ZipOutputStream? = null
        try {
            out = FileOutputStream(dstFile)
            val cos = CheckedOutputStream(out, CRC32())
            zipOut = ZipOutputStream(cos)
            val baseDir = ""
            compress(srcFile, zipOut, baseDir)
        } finally {
            if (null != zipOut) {
                zipOut.close()
                out = null
            }

            out?.close()
            return dstPath
        }
    }

    @Throws(IOException::class)
    private fun compress(file: File, zipOut: ZipOutputStream, baseDir: String) {
        if (file.isDirectory) {
            compressDirectory(file, zipOut, baseDir)
        } else {
            compressFile(file, zipOut, baseDir)
        }
    }

    @Throws(IOException::class)
    private fun compressDirectory(dir: File, zipOut: ZipOutputStream, baseDir: String) {
        val files = dir.listFiles()
        for (i in files!!.indices) {
            compress(files[i], zipOut, baseDir + dir.name + "/")
        }
    }

    @Throws(IOException::class)
    private fun compressFile(file: File, zipOut: ZipOutputStream, baseDir: String) {
        if (!file.exists()) {
            return
        }

        var bis: BufferedInputStream? = null
        try {
            bis = BufferedInputStream(FileInputStream(file))
            val entry = ZipEntry(baseDir + file.name)
            zipOut.putNextEntry(entry)
            var count: Int
            val data = ByteArray(BUFFER)

            do {
                count = bis.read(data, 0, BUFFER)
                if (count == -1) {
                    break
                }
                zipOut.write(data, 0, count)
            } while (true)

//            while ((count = bis.read(data, 0, BUFFER)) != -1) {
//                zipOut.write(data, 0, count)
//            }

        } finally {
            bis?.close()
        }
    }

}