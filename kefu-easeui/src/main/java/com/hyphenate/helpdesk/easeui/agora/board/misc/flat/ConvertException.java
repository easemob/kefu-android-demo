package com.hyphenate.helpdesk.easeui.agora.board.misc.flat;

public final class ConvertException extends Exception {
    private ConvertErrorCode code;

    public ConvertException(ConvertErrorCode code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        String detailMessage = "";
        switch (code) {
            case CreatedFail:
                detailMessage = "创建失败";
                break;
            case ConvertFail:
                detailMessage = "转换失败";
                break;
            case NotFound:
                detailMessage = "没有在服务器上未找到对应的任务";
                break;
            case CheckFail:
                detailMessage = "检查转换状态时，出错";
                break;
            case CheckTimeout:
                detailMessage = "查询请求超时，请重启轮询";
                break;
            case GetDynamicFail:
                detailMessage = "请求动态转换结果出错";
                break;
        }

        if (detailMessage.isEmpty()) {
            return super.getMessage();
        } else {
            return "convert error: " + detailMessage + " error: " + super.getMessage();
        }
    }

    public ConvertException(ConvertErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ConvertException(ConvertErrorCode code, Exception e) {
        super(e);
        this.code = code;
    }

    public ConvertErrorCode getCode() {
        return code;
    }
}