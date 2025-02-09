package org.xuliang.vo;

import org.xuliang.constant.AppConstant;


/**
 * 操作结果封装类
 * <p>
 * 用于新增、修改、删除等操作，返回操作是否成功和提示消息等
 * 
 * @author ZXY
 * 
 */
public class Result
{
    /**
     * 操作是否成功
     */
    private Boolean success = false;

    /**
     * 提示消息
     */
    private String msg = AppConstant.ACTION_ERROR_MSG;

    /**
     * 异常信息
     */
    private String exception;

    public Result()
    {
    }

    public Result(Boolean success, String msg)
    {
        this.success = success;
        this.msg = msg;
    }

    public Result(Boolean success, String msg, String exception)
    {
        this.success = success;
        this.msg = msg;
        this.exception = exception;
    }

    public Boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(Boolean success)
    {
        this.success = success;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getException()
    {
        return exception;
    }

    public void setException(String exception)
    {
        this.exception = exception;
    }

}
