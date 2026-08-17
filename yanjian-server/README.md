# 技术架构

开发语言： java<br>
开发框架： springboot Spring Cloud Alibaba<br>
单体和微服务： 单体和微服务版是一个项目，设计时按微服务设计开发，鉴于微服务的复杂性，为降低维护成本，改造了一个单体springboot版本(其实就是微服务一个服务单独启动，不往注册中心注册就行了)，功能完全一样，可根据自己需求选择合适的版本<br>
默认为单体，如果要修改为微服务版，则删除 msfast-modules->msfast-yanjian->pom.xml-微服务配置依赖代码
数据库：mysql redis <br>
文件存储: minio 腾讯云对象存储cos，可根据配置自行选择,文件存储服务类型 minio-minio文件存储 tencentIo-腾讯云对象存储cos 配置项：wxmfast.config.file.objectServiceType<br>

# 启动必要基础环境
请确保本地基础环境搭建完毕 <br>
jdk 1.8<br>
Mysql 8.0<br>
redis<br>
minio或腾讯云对象存储cos<br>
其他配置文件如微信支付相关证书密钥等，如果暂时没有可以将相关启动加载配置先注释

# 快速启动
1. 新建数据库 数据库名：yanjian_demo，注意设置数据库 字符集为utf8mb4 排序规则为utf8mb4_unicode_ci<br>
2. 执行此项目doc文件夹下yanjian_demo.sql脚本<br>
3. 修改项目根目录 config/application.yml 文件中相关配置，启动端口，数据库信息，redis连接信息，minio连接信息或腾讯云对象存储cos连接信息，微信支付相关参数，微信小程序appid，微信小程序appsecret，微信公众号appid等 具体信息见文件中注释<br>
4. 构建好项目后，加载maven相关依赖，如果出现com.wxmblog相关依赖包无法下载，请检查maven仓库是否配置正确，例如 配置了阿里云maven镜像仓库时配置了central或是 ，如果工程中的jar包都能在阿里镜像中找到，mirrorOf填central都是可以的。central表示覆盖maven中央仓库的默认url，*表示所有的仓库都到我配置的这个url取，所以会导致部分依赖阿里云镜像更新不及时而提示包不存在的错误，请以maven官方中央仓库为准，可以尝试修改为maven默认配置，就是下载maven后最原始的配置，不使用阿里云仓库<br>
5. 相关配置修改完毕后通过idea运行项目,如果报错，请检查相关配置是否正确，如果部分配置（如微信支付证书密钥等）暂时无法获取可先将相关启动加载注释掉，具体注释位置根据报错自行调试<br>
6. 运行时小程序端部分图片和字体文件会报404的错误，是为了减少客户端内存,静态资源文件部署在服务端，将此项目doc文件夹中system文件夹放至相关网络环境中，例如腾讯云对象存储cos，minio存储，服务端静态路径等，对应小程序端修改访问路径配置文件env.js->env.imgBaseURL<br>

# 联系我们
## 作者微信
MMRWXM <br>
如果有什么问题咨询或者建议，合作等，都可以添加我的[README.md](..%2FREADME.md)微信，一起交流学习
## QQ交流群
1061244492
