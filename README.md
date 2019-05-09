# ApkPackageServer
360加固+vasdolly
# 说明：
1. 介绍：一个为Android应用加固并打渠道包的SpringBoot后台应用,之前在AS中写过一个buildTask进行该操作，但是由于360加固区分了平台，以及平台之间的差异性，导致不好管理、并且打出来后用U盘传输效率低下，故此有了这个服务器。
2. 技术摘要：[springboot](https://spring.io/projects/spring-boot) 、webflux 、netty 、[ProjectReactor](https://projectreactor.io/)、kotlin 、Reactive MongoDB 、[360加固](https://jiagu.360.cn/x)、[H ui前端框架免费版](http://www.h-ui.net/) 、[qrcodejs](https://github.com/davidshimjs/qrcodejs) 、[VasDolly](https://github.com/Tencent/VasDolly)、[Thymeleaf](https://www.thymeleaf.org/documentation.html)、docker、[dockerfile-maven-plugin](https://github.com/spotify/docker-maven-plugin)
3. 该后台作为 开发者->测试->产品 之间桥梁，开发人员传入 keystore 、密码、alias 、渠道txt文件后 可通知测试进行扫码下载，在测试完成后 开始执行加固打包命令 ，然后通知产品经理单独下载渠道文件或者直接下载所有文件的zip包。
4. 目前仅支持 360加固 、VasDolly渠道 ，请确保您的包已经配置好VasDolly.
5. 执行原理：利用360加固和VasDolly 的命令行指令进行 异步操作。
# Docker部署以及外化配置
1. Docker运行：
 - 该项目依赖于mongoDB，需要启动mongoDB,并且关联上容器中的`mongo`域：`--link your-mongo-hostname:mongo`
 - 数据盘挂载：容器中的数据地址在：`/spring/upload`中，推荐挂载！！！，`-v your-link-disk:/spring/upload`
 - 外化的配置properties:如需更改默认的配置：根据springboot的加载规则，可以将你的客制化配置文件夹或文件挂载在`/spring/config`下：`-v ~/docker_spring_conf:/spring/config`
 - 如需修改您的360加固的位置（比如更新他的版本）：挂载在`/spring/`下的处`jiagu`路径外的任意配置中，并按照上一点客制化你的配置：`jiagu.path=your-path/`
 - 默认端口号：`8080`
 - mongodb端口号:`27017`
 - e.g：docker run --name apk -p 8080:8080 -v ~/docker_spring:/spring/upload  -d --link mongo:mongo giantbing/apkpackage:1.0`-v ~/docker_spring_conf:/spring/config`，并复制一份项目的application.properties到该目录中，并加以修改
# 本地Ide中debug运行
 1.在application.properties中配置了：`jiagu.path.prefix= /debug/` ，所以需要在项目根路径中有一个`debug`文件夹、将你的 加固的文件及 `VasDolly.jar` 放进该目录即可替换即可
# 项目配置解析：
 ```
          spring.data.mongodb.uri=mongodb://giantbing:gb952400@localhost:27017/apk //mongodb 的配置
          jiagu.path=jiagu/                                                        //加固路径
          jiagu.user=your 360user                                                  //360加固账号
          jiagu.pwd=your 360 pwd                                                   //360加固密码
          jiagu.path.prefix= /debug/   or jiagu.path.prefix=/                      //upload路径的前缀 线上的可传/
          giantbing.docker = debug                                                 //在运行时可以查看配置文件情况，只是一个TAG在运行时可以打印出来
 ```
# 启动命令：
`docker run --name apk -p 8080:8080 -v ~/docker_spring:/spring/upload -v ~/docker_spring_conf:/spring/config -d --link mongo:mongo giantbing/apkpackage:1.0`