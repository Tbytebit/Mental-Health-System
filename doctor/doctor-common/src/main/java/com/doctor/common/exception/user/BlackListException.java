package com.doctor.common.exception.user;

/**
 * 黑名单IP异常类
 * 
 * @author LI
 */
public class BlackListException extends UserException
{
    private static final long serialVersionUID = 1L;

    public BlackListException()
    {
        super("login.blocked", null);
    }
}
