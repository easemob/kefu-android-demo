package com.hyphenate.helpdesk.easeui.agora.board.misc.flat;


import com.google.gson.annotations.SerializedName;
import com.herewhite.sdk.domain.WhiteObject;

/**
 * `PptPage` 类，用于配置待插入场景的图片或动态 PPT 页的参数。
 *
 * 你可以在场景初始化时传入 `PptPage` 实例，以在白板上展示图片或动态 PPT。
 *
 * 场景中可插入的图片或动态 PPT 包括：
 * - PNG、JPG/JPEG、WEBP 格式的图片，或由 PPT、PPTX、DOC、DOCX、PDF 格式的文件转换成 PNG、JPG/JPEG、WEBP 格式的图片。
 * - 使用 [Agora 互动文档转换功能](https://docs.agora.io/cn/whiteboard/file_conversion_overview?platform=RESTful) 转换过的 PPTX 文件。
 *
 */
public class PptPage extends WhiteObject {

    @SerializedName(value = "src", alternate = {"conversionFileUrl"})
    private String src;
    private Double width;
    private Double height;
    @SerializedName(value = "previewURL", alternate = {"preview"})
    private String preview;

    /**
     * `PptPage` 构造方法，用于创建待插入场景的图片或动态 PPT 实例。
     *
     * @note
     * - 该方法只能在场景初始化的时候调用。
     * - 每个场景中只能插入一张图片和一页动态 PPT。
     * - 场景中展示的图片或动态 PPT 中心点默认为白板内部坐标系得原点且无法移动，即无法改变图片或动态 PPT 在白板内部的位置。
     *
     * @param src 图片或动态 PPT 页的地址，支持的格式如下：
     * - 图片：URL 地址，可以是你自己生成的 URL 地址，也可以是通过文档转换功能生成的 URL 地址，例如，`"https://docs-test-xxx.oss-cn-hangzhou.aliyuncs.com/staticConvert/2fdxxxxx67e/1.jpeg"`。
     * - 动态 PPT 页：通过文档转换功能生成的 URI 地址，例如，`"pptx://cover.herewhite.com/dynamicConvert/6a212c90fa5311ea8b9c074232aaccd4/1.slide"`，即[动态文档转换任务的查询结果](https://docs.agora.io/cn/whiteboard/whiteboard_file_conversion?platform=RESTful#查询转换任务的进度（get）)中 `conversionFileUrl` 字段的值。
     * @param width  图片或动态 PPT 在白板中的宽度，单位为像素。
     * @param height 图片或动态 PPT 在白板中的高度，单位为像素。
     */
    public PptPage(String src, Double width, Double height) {
        this(src, width, height, null);
    }

    /**
     * `PptPage` 构造方法，用于创建待插入场景的图片或动态 PPT 实例。
     *
     * @note
     * - 该方法只能在场景初始化的时候调用。
     * - 每个场景中只能插入一张图片或一页动态 PPT。
     * - 场景中展示的图片或动态 PPT 中心点默认为白板内部坐标系得原点且无法移动，即无法改变图片或动态 PPT 在白板内部的位置。
     *
     * @param src 图片或动态 PPT 页的地址，支持的格式如下：
     * - 图片：URL 地址，可以是你自己生成的 URL 地址，也可以是通过文档转换功能生成的 URL 地址，例如，`"https://docs-test-xxx.oss-cn-hangzhou.aliyuncs.com/staticConvert/2fdxxxxx67e/1.jpeg"`。
     * - 动态 PPT 页：通过文档转换功能生成的 URI 地址，例如，`"pptx://cover.herewhite.com/dynamicConvert/6a212c90fa5311ea8b9c074232aaccd4/1.slide"`，即[动态文档转换任务的查询结果](https://docs.agora.io/cn/whiteboard/whiteboard_file_conversion?platform=RESTful#查询转换任务的进度（get）)中 `conversionFileUrl` 字段的值。
     * @param width   图片或动态 PPT 在白板中的宽度，单位为像素。
     * @param height  图片或动态 PPT 在白板中的高度，单位为像素。
     * @param preview 图片或动态 PPT 预览图的 URL 地址。动态 PPT 预览图的 URL 地址可以从[文档转换任务的查询结果](https://docs.agora.io/cn/whiteboard/whiteboard_file_conversion?platform=RESTful#查询转换任务的进度（get）)中的 `preview` 字段获取，例如，"https://docs-test-xxx.oss-cn-hangzhou.aliyuncs.com/dynamicConvert/2fdxxxxx67e/preview/1.png"。
     */
    public PptPage(String src, Double width, Double height, String preview) {
        this.src = src;
        this.width = width;
        this.height = height;
        this.preview = preview;
    }


    /**
     * 获取场景图片或动态 PPT 的地址。
     *
     * @return 图片的 URL 地址或动态 PPT 的 URI 地址。
     */
    public String getSrc() {
        return src;
    }

    /**
     * 指定待插入场景的图片或动态 PPT。
     *
     * @param src 图片或动态 PPT 页的地址，支持的格式如下：
     * - 图片：URL 地址，可以是你自己生成的 URL 地址，也可以是通过文档转换功能生成的 URL 地址，例如，`"https://docs-test-xxx.oss-cn-hangzhou.aliyuncs.com/staticConvert/2fdxxxxx67e/1.jpeg"`。
     * - 动态 PPT 页：通过文档转换功能生成的 URI 地址，例如，`"pptx://cover.herewhite.com/dynamicConvert/6a212c90fa5311ea8b9c074232aaccd4/1.slide"`，即[动态文档转换任务的查询结果](https://docs.agora.io/cn/whiteboard/whiteboard_file_conversion?platform=RESTful#查询转换任务的进度（get）)中 `conversionFileUrl` 字段的值。
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * 获取图片或动态 PPT 在白板中的宽度。
     *
     * @return 图片或动态 PPT 在白板中的宽度，单位为像素。
     */
    public double getWidth() {
        return width;
    }

    /**
     * 设置图片或动态 PPT 在白板中的宽度。
     *
     * @param width 图片或动态 PPT 在白板中的宽度，单位为像素。
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * 获取图片或动态 PPT 在白板中的高度。
     *
     * @return 图片或动态 PPT 在白板中的高度，单位为像素。
     */
    public double getHeight() {
        return height;
    }

    /**
     * 设置图片或动态 PPT 在白板中的高度。
     *
     * @return 图片或动态 PPT 在白板中的高度，单位为像素。
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * 获取图片或动态 PPT 预览图的 URL 地址。
     *
     * @return 图片的或动态 PPT 预览图的 URL 地址。
     */
    public String getPreview() {
        return preview;
    }

    /**
     * 指定图片或 PPT 预览图。
     *
     * 通过该方法指定的预览图会展示在白板右侧的页面预览中。
     *
     * @param preview 图片或动态 PPT 预览图的 URL 地址。动态 PPT 预览图的 URL 地址可以从[文档转换任务的查询结果](https://docs.agora.io/cn/whiteboard/whiteboard_file_conversion?platform=RESTful#查询转换任务的进度（get）)中的 `preview` 字段获取，例如，"https://docs-test-xxx.oss-cn-hangzhou.aliyuncs.com/dynamicConvert/2fdxxxxx67e/preview/1.png"。
     */
    public void setPreview(String preview) {
        this.preview = preview;
    }
}
