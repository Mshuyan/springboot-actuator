# springboot-actuator

> + springboot 内置的生产环境监控程序；如：监控CPU使用率、内存使用率等
> + 资料参见
>   + [Spring boot 2.0 Actuator 的健康检查(简书)](https://www.jianshu.com/p/1aadc4c85f51)
>   + [Spring Boot 2.0的 Actuator 有关监控页面全面的资料](https://www.2cto.com/kf/201804/738760.html)
>   + [Spring Boot 2.0官方文档之 Actuator](https://blog.csdn.net/alinyua/article/details/80009435)
>   + [Spring Boot 2.0 中使用 Actuator](https://blog.csdn.net/qq_35915384/article/details/80203768)

## 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> 此时访问`http://localhost:8080/actuator/health`和`http://localhost:8080/actuator/info`就可以查看一些信息了

## 端点

### 介绍

> `端点`就是暴露监控信息的接口，默认基于`actuator`路由

| 端点           | 描述                                                         |
| -------------- | ------------------------------------------------------------ |
| auditevents    | 显示当前应用程序的审计事件信息                               |
| beans          | 显示一个应用中`所有Spring Beans`的完整列表                   |
| conditions     | 显示`配置类和自动配置类`(configuration and auto-configuration classes)的状态及它们被应用或未被应用的原因 |
| configprops    | 显示一个所有`@ConfigurationProperties`的集合列表             |
| env            | 显示来自Spring的 `ConfigurableEnvironment`的属性             |
| flyway         | 显示数据库迁移路径，如果有的话                               |
| health         | 显示应用的`健康信息`（当使用一个未认证连接访问时显示一个简单的’status’，使用认证连接访问则显示全部信息详情） |
| info           | 显示任意的`应用信息`                                         |
| liquibase      | 展示任何Liquibase数据库迁移路径，如果有的话                  |
| metrics        | 展示当前应用的`metrics`信息                                  |
| mappings       | 显示一个所有`@RequestMapping`路径的集合列表                  |
| scheduledtasks | 显示应用程序中的`计划任务`                                   |
| sessions       | 允许从Spring会话支持的会话存储中检索和删除(retrieval and deletion)用户会话。使用Spring Session对反应性Web应用程序的支持时不可用。 |
| shutdown       | 允许应用以优雅的方式关闭（默认情况下不启用）                 |
| threaddump     | 执行一个线程dump                                             |

> 如果使用web应用(Spring MVC, Spring WebFlux, 或者 Jersey)，你还可以使用以下端点：

| 端点       | 描述                                                         |
| ---------- | ------------------------------------------------------------ |
| heapdump   | 返回一个GZip压缩的`hprof`堆dump文件                          |
| jolokia    | 通过HTTP暴露`JMX beans`（当Jolokia在类路径上时，WebFlux不可用） |
| logfile    | 返回`日志文件内容`（如果设置了logging.file或logging.path属性的话），支持使用HTTP **Range**头接收日志文件内容的部分信息 |
| prometheus | 以可以被Prometheus服务器抓取的格式显示`metrics`信息          |

### health端点

> `health`端点用于显示健康检查信息

#### 健康状态

>  访问`health`端点返回的数据中的`status`表示健康状态，健康状态有以下几种：

|      状态      |    说明    | 状态码 |
| :------------: | :--------: | :----: |
|      DOWN      | 服务不可用 |  503   |
| OUT_OF_SERVICE | 服务不可用 |  503   |
|       UP       |  运行正常  |  200   |
|    UNKNOWN     |  未知状态  |  200   |

#### 健康检查原理

> - Spring boot的健康信息都是从`ApplicationContext`中的各种`HealthIndicator` Beans中收集到的，Spring boot框架中包含了大量的`HealthIndicator`的实现类
> - 默认情况下，最终的spring boot应用的状态是由`HealthAggregator`汇总而成的，汇总的算法是：
>   - 设置状态码顺序：`setStatusOrder(Status.DOWN, Status.OUT_OF_SERVICE, Status.UP, Status.UNKNOWN);`。
>   - 过滤掉不能识别的状态码。
>   - 如果无任何状态码，整个spring boot应用的状态是 `UNKNOWN`。
>   - 将所有收集到的状态码按照 1 中的顺序排序。
>   - 返回有序状态码序列中的第一个状态码，作为整个spring boot应用的状态。

#### 健康检测项

> - Spring boot框架自带的 `HealthIndicator` 目前包括：

| Name                         | Description                          |
| ---------------------------- | ------------------------------------ |
| CassandraHealthIndicator     | 检查Cassandra数据库是否启动          |
| DiskSpaceHealthIndicator     | 检查磁盘空间是否不足。               |
| DataSourceHealthIndicator    | 检查是否可以获得与DataSource的连接。 |
| ElasticsearchHealthIndicator | 检查Elasticsearch集群是否启动。      |
| InfluxDbHealthIndicator      | 检查InfluxDB服务器是否启动。         |
| JmsHealthIndicator           | 检查JMS代理是否启动。                |
| MailHealthIndicator          | 检查邮件服务器是否启动。             |
| MongoHealthIndicator         | 检查Mongo数据库是否启动。            |
| Neo4jHealthIndicator         | 检查Neo4j服务器是否启动。            |
| RabbitHealthIndicator        | 检查Rabbit服务器是否启动。           |
| RedisHealthIndicator         | 检查Redis服务器是否启动。            |
| SolrHealthIndicator          | 检查Solr服务器是否已启动。           |

#### 自定义HealthIndicator

> 例程参见[MyOwnHealthIndicator](./src/main/java/com/shuyan/springbootactuator/MyHealthIndicators/MyOwnHealthIndicator.java)



## 配置

### 启用、不启用端点

+ `management.endpoints.web.exposure.include`

  + 作用：

    配置哪些端点启用

  + 默认值：

    `health,info`

  + 可设值

    所有端点，以逗号分割

    `"*"`代表全部启用（`*`是特殊字符，需要用双引号引起来）

+ `management.endpoints.web.exposure.exclude`

  + 作用：

    配置`management.endpoints.web.exposure.include`配置的启用的端点中哪些端点被排除掉（不启用），一般只有`management.endpoints.web.exposure.include`配置为`"*"`时才配置该项

  + 默认值

    空

  + 可设值

    所有端点，以逗号分割

    `"*"`代表全部启用（`*`是特殊字符，需要用双引号引起来）

### 端口及绝对路径

> 以下两个配置项只作用于actuator端点，不影响应用的正常接口

+ `management.server.port`

  + 作用

    配置端口

  + 默认值

    8080

+ `management.server.servlet.context-path`

  + 作用

    配置绝对路径

  + 默认值

    /

  + 注意

    本配置项只有配置了`management.server.port`时才生效

### 单个端点配置

> 单个端点的配置都是`management.endpoint`的子配置项

#### health

##### 详细健康信息显示方式

+ 配置项

  `management.endpoint.health.show-details`

+ 作用

  配置什么时候显示完整的健康信息

+ 可设值

  `always`：总是

  `never`：从不（默认值）

  `when_authorized`：向通过权限认证的用户显示

+ 例

  + 不显示详细信息时返回的数据

    ```Json
    {
        "status": "UP"
    }
    ```

  + 显示详细信息时返回的数据中包含应用中使用到的健康检查项的详细信息

    ```json
    {
        "status": "UP",
        "details": {
            "diskSpace": {
                "status": "UP",
                "details": {
                    "total": 121123069952,
                    "free": 53021790208,
                    "threshold": 10485760
                }
            }
        }
    }
    ```

##### 启用禁用健康检查项

+ `management.health.defaults.enabled`

  + 作用

    启用禁用所有检测项

  + 默认值

    true(启用)

+ `management.health.xxxx.enabled`

  + 作用

    单独禁用启用某个健康检测项

  + 默认值

    true（启用）

  + 说明

    本配置项优先级高于`management.health.defaults.enabled`

    