package com.doctor.survey.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 问卷答卷存储对象 survey_respondents
 * 
 * @author ruoyi
 * @date 2024-01-19
 */
public class SurveyRespondents extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String respondentsId;

    /** 问卷id */
    @Excel(name = "问卷id")
    private String questionnaireId;

    /** 答卷提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "答卷提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submitTime;

    /** 答卷所耗时间(单位：s) */
    @Excel(name = "答卷所耗时间(单位：s)")
    private String elapsedTime;

    /** 答卷来源，是微信分享还是QQ分享，0-网站链接，1-QQ，2-WX，3-QQ空间，4-微博,5-二维码扫码，6-其他 */
    @Excel(name = "答卷来源，是微信分享还是QQ分享，0-网站链接，1-QQ，2-WX，3-QQ空间，4-微博,5-二维码扫码，6-其他")
    private Long source;

    /** 答卷提交的IP地址 */
    @Excel(name = "答卷提交的IP地址")
    private String ip;

    /** 答卷提交的地点，粗略精确到城市 */
    @Excel(name = "答卷提交的地点，粗略精确到城市")
    private String address;

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRespondentsId(String respondentsId)
    {
        this.respondentsId = respondentsId;
    }

    public String getRespondentsId() 
    {
        return respondentsId;
    }
    public void setQuestionnaireId(String questionnaireId) 
    {
        this.questionnaireId = questionnaireId;
    }

    public String getQuestionnaireId() 
    {
        return questionnaireId;
    }
    public void setSubmitTime(Date submitTime) 
    {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() 
    {
        return submitTime;
    }
    public void setElapsedTime(String elapsedTime) 
    {
        this.elapsedTime = elapsedTime;
    }

    public String getElapsedTime() 
    {
        return elapsedTime;
    }
    public void setSource(Long source) 
    {
        this.source = source;
    }

    public Long getSource() 
    {
        return source;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("respondentsId", getRespondentsId())
            .append("questionnaireId", getQuestionnaireId())
            .append("submitTime", getSubmitTime())
            .append("elapsedTime", getElapsedTime())
            .append("source", getSource())
            .append("ip", getIp())
            .append("address", getAddress())
            .toString();
    }
}
