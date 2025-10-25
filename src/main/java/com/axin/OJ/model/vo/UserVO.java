package com.axin.OJ.model.vo;

import java.io.Serializable;
import java.util.Date;

import cn.hutool.json.JSONUtil;
import com.axin.OJ.model.entity.JudgeConfig;
import com.axin.OJ.model.entity.Question;
import com.axin.OJ.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 用户视图（脱敏）
 *
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param user 用户
	 * @return 用户包装类
     */
    public static UserVO objToVo(User user) {
    	if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}