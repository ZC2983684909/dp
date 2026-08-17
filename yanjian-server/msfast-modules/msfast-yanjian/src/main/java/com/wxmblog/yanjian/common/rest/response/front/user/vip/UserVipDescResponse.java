package com.wxmblog.yanjian.common.rest.response.front.user.vip;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserVipDescResponse {

    @ApiModelProperty(value = "是否是vip")
    private Boolean isVip;

    //到期时间
    @ApiModelProperty(value = "到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    @ApiModelProperty(value = "会员头像")
    private String avatar;

    @ApiModelProperty(value = "会员价格列表")
    private List<VipPriceVo> vipPriceList;

}
