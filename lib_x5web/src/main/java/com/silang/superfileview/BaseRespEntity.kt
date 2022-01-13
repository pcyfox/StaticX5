package com.silang.superfileview

import androidx.annotation.Keep
import com.silang.superfileview.RespCode.OK

@Keep
open class BaseRespEntity<D>(var resultCode: Int = -1,
                             var message: String = "",
                             var data: D? = null) {
    fun isOK() = resultCode == OK
}

object RespCode {
    const val OK = 0
}