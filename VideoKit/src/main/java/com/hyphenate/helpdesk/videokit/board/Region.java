package com.hyphenate.helpdesk.videokit.board;

import com.google.gson.annotations.SerializedName;

/**
 * 数据中心。
 */
public enum Region {
    /**
     * `cn` (`cn-hz`)：中国杭州。
     *
     * 该数据中心为其他数据中心服务区未覆盖的地区提供服务。
     */
    @SerializedName("cn-hz")
    cn,
    /**
     * `us` (`us-sv`)：美国硅谷。
     *
     * 该数据中心为北美洲、南美洲地区提供服务。
     */
    @SerializedName("us-sv")
    us,
    /**
     * `sg`：新加坡。
     *
     * 该数据中心为新加坡、东亚、东南亚地区提供服务。
     */
    @SerializedName("sg")
    sg,
    /**
     * `in_mum`：印度孟买。
     *
     * 该数据中心为印度地区提供服务。
     */
    @SerializedName("in-mum")
    in_mum,
    /**
     * `gb_lon`：英国伦敦。
     *
     * 该数据中心为欧洲地区提供服务。
     */
    @SerializedName("gb-lon")
    gb_lon;
}