package com.doctor.web.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 帮助内容对象 t_help
 * 
 * @author LI
 * @date 2024-05-09
 */
public class Help extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long helpId;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 类别 */
    @Excel(name = "类别")
    private String category;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 访问次数 */
    @Excel(name = "访问次数")
    private Long visitCount;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 相关链接 */
    @Excel(name = "相关链接")
    private String relatedLinks;

    /** 删除标志 */
    private String delFlag;

    public void setHelpId(Long helpId) 
    {
        this.helpId = helpId;
    }

    public Long getHelpId() 
    {
        return helpId;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setVisitCount(Long visitCount) 
    {
        this.visitCount = visitCount;
    }

    public Long getVisitCount() 
    {
        return visitCount;
    }
    public void setLikeCount(Long likeCount) 
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() 
    {
        return likeCount;
    }
    public void setRelatedLinks(String relatedLinks) 
    {
        this.relatedLinks = relatedLinks;
    }

    public String getRelatedLinks() 
    {
        return relatedLinks;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("helpId", getHelpId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("category", getCategory())
            .append("status", getStatus())
            .append("visitCount", getVisitCount())
            .append("likeCount", getLikeCount())
            .append("relatedLinks", getRelatedLinks())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
