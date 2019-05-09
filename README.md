# ApkPackageServer
360加固+vasdolly

# 启动命令：
`docker run --name apk -p 8080:8080 -v ~/docker_spring:/spring/upload -v ~/docker_spring_conf:/spring/config -d --link mongo:mongo giantbing/apkpackage:1.0`