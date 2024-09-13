package anda.travel.driver.util

interface Env {
    val envName: String
    val apiHost: String
    val socketHost: String
}

object EnvFactory {
    const val ENV_DEBUG = 0
    const val ENV_DEBUG_OUT = 1
    const val ENV_PRE_RELEASE = 2
    const val ENV_RELEASE = 3

    fun createEnv(type: Int): Env {
        return when (type) {
            0 -> EnvDebug()
            1 -> EnvDebugOut()
            2 -> EnvPreRelease()
            3-> EnvRelease()
            else -> EnvDebug()
        }
    }

    private class EnvDebug(
            override val envName: String = "测试环境",
            override val apiHost: String = "http://192.168.100.232:8011",
//            override val apiHost: String = "http://192.168.100.72:8011",//本地环境
            override val socketHost: String = "http://192.168.100.232:10020"
//            override val apiHost: String = "http://192.168.100.22:8088",
//            override val socketHost: String = "http://192.168.100.22:10020"

            ////分时定价
          /*  override val apiHost: String = "http://192.168.100.235:8999",
            override val socketHost: String = "http://192.168.100.235:10020"*/
    ) : Env

    private class EnvDebugOut(
            override val envName: String = "测试外网",
            override val apiHost: String = "http://112.31.111.155:8081",
            override val socketHost: String = "http://112.31.111.155:10021"
    ) : Env

    private class EnvPreRelease(
           override val envName: String = "预生产",
           override val apiHost: String = "http://api-pre.hexingyueche.com",
           override val socketHost: String = "http://121.199.164.53:10020"
    ) : Env

    private class EnvRelease(
            override val envName: String = "正式环境",
            override val apiHost: String = "https://api.hexingyueche.com",
            override val socketHost: String = "https://api.hexingyueche.com:10020"
    ) : Env
}
