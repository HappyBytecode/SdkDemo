package anda.travel.driver.util

import anda.travel.driver.api.ApiConfig
import anda.travel.driver.data.entity.OssTokenEntity
import anda.travel.driver.service.RecordingService
import anda.travel.driver.baselibrary.network.RequestBean
import anda.travel.driver.baselibrary.network.RetrofitRequestTool
import anda.travel.driver.baselibrary.utils.file.FileUtil
import anda.travel.driver.baselibrary.utils.security.EncryptionUtil
import android.content.Context
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSS
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.OSSConstants
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken
import com.alibaba.sdk.android.oss.common.utils.IOUtils
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object OssManager {

    //////////////设定为北京的
    private const val endPoint = "http://oss-cn-hangzhou.aliyuncs.com"
    const val bucketName = "driveraudio"

    var appId = ""
    var noncestr = ""
    var tokenName = ""
    var sign = ""
    private var ossClient: OSS? = null

    //////////初始化Oss
    fun initOss(context: Context) {
        appId = RetrofitRequestTool.getAppid()
        val sortMap = mutableMapOf<String, String>()
        sortMap.put("appid", appId)
        noncestr = System.currentTimeMillis().toString()
        sortMap.put("noncestr", noncestr)
        tokenName = DeviceUtil.getDeviceToken(context)
        sortMap.put("tokenName", tokenName)

        val params = StringBuilder()

        for ((key, value) in sortMap) {
            println("${key}->${value}")
            params.append(key)
            params.append("=")
            params.append(value)
            params.append("&")
        }
        params.append("key=" + RetrofitRequestTool.getKey())
        sign = EncryptionUtil.md5Encode(params.toString()).toUpperCase()

        val credentialProvider = object : OSSFederationCredentialProvider() {
            override fun getFederationToken(): OSSFederationToken? {
                try {
                    val stsUrl = URL(ApiConfig.getDriverApi() + "upload/audioToken?appid=$appId&noncestr=$noncestr&tokenName=$tokenName&sign=$sign")
                    val conn: HttpURLConnection = stsUrl.openConnection() as HttpURLConnection
                    val input: InputStream = conn.inputStream
                    val jsonText: String = IOUtils.readStreamAsString(input, OSSConstants.DEFAULT_CHARSET_NAME)
                    val bean: RequestBean = JSON.parseObject(jsonText, RequestBean::class.java)
                    val realData: OssTokenEntity = JSON.parseObject(bean.data, OssTokenEntity::class.java)
                    return OSSFederationToken(realData.accessKeyId, realData.accessKeySecret, realData.securityToken, realData.expiration)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }
        }
        ossClient = OSSClient(context, endPoint, credentialProvider)
    }

    //////////上传录音文件到oss
    fun uploadFile(orderUuid: String?, path: String) {
        // 获取路径下文件的数据
        val file = File(path)
        val files = file.listFiles()
        ////////遍历文件夹下的数据传送给oss
        for (index in files.indices) {
            var tempFile = files[index]
            val tempFileName = tempFile.name
            var objectkey = "upload/$orderUuid/$tempFileName"
            val put = PutObjectRequest(bucketName, objectkey, file.path + "/$tempFileName")

            var hostAddress = ApiConfig.getRootApi() + "/upload/audio/oss/callback"

            ///////////设置上传回调url
            put.callbackParam = object : HashMap<String?, String?>() {
                init {
                    put("callbackUrl", hostAddress)
                    put("callbackBody", "{\"fileName\":\"$tempFileName\",\"orderId\":\"$orderUuid\"}")
                }
            }

            ossClient!!.asyncPutObject(put, object : OSSCompletedCallback<PutObjectRequest?, PutObjectResult> {
                override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult) {
                    Timber.d("上传oss成功")
                    if (RecordingService.getInstance() != null) {
                        RecordingService.getInstance()!!.stopService()
                    }
                    /////////发送成功后需删除录音文件
                    if (index == files.size - 1) {
                        var parent = tempFile.parent
                        FileUtil.deleteFile(tempFile)
                        /////文件夹下没有数据的时候删除数据
                        if (FileUtil.getFileSize(parent) <= 0) {
                            FileUtil.deleteFile(parent)
                        }
                    } else {
                        FileUtil.deleteFile(tempFile)
                    }
                }

                override fun onFailure(request: PutObjectRequest?, clientExcepion: ClientException, serviceException: ServiceException) {
                    Timber.d("上传oss失败")
                    if (RecordingService.getInstance() != null) {
                        RecordingService.getInstance()!!.stopService()
                    }
                    // 请求异常。
                    clientExcepion?.printStackTrace()
                    if (serviceException != null) {
                        // 服务异常。
                        Timber.e("ErrorCode$serviceException.errorCode")
                    }
                }
            })
        }
    }
}