package com.system.user.service.impl;

import com.system.common.constants.WebConstants;
import com.system.common.constants.YesNoEnum;
import com.system.common.support.XBeanUtil;
import com.system.common.utils.SHA256Utils;
import com.system.exception.BizException;
import com.system.user.dao.UserDao;
import com.system.user.domain.UserDomain;
import com.system.user.dto.ModifyPasswordDTO;
import com.system.user.dto.UserDTO;
import com.system.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Value("${application.login.salt:phoenixAd}")
    private String salt;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public UserDTO addUser(UserDTO userDTO) {
        // 用户名唯一性校验
        UserDomain exist = userDao.findByName(userDTO.getName());
        if (null != exist) {
            throw new BizException("user.name.exist");
        }

        // 添加用户默认密码
        userDTO.setPassword(WebConstants.DREFAULT_PASSWORD);
        userDTO.setIsDeleted(YesNoEnum.NO.getValue());
        userDTO.setIsRoot(YesNoEnum.NO.getValue());

        UserDomain user = new UserDomain();
        try {
            XBeanUtil.copyProperties(user, userDTO, false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException();
        }
        if (StringUtils.isNotBlank(userDTO.getPassword())) {
            user.setPassword(SHA256Utils.encryptPassword(userDTO.getPassword(), salt));
        }
        user = userDao.save(user);
        userDTO.setId(user.getId());
        return userDTO;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public Integer editUser(UserDTO userDTO) {
        // 校验用户是否存在
        UserDomain editUser = userDao.findByIdAndIsDeleted(userDTO.getId(), YesNoEnum.NO.getValue());
        if (null == editUser) {
            throw new BizException("User not exist!");
        }

        // 校验用户名唯一性
        if (!StringUtils.isEmpty(userDTO.getName())) {
            UserDomain user = userDao.findByName(userDTO.getName());
            if (null != user && !user.getId().equals(editUser.getId())) {
                throw new BizException("user.name.exist");
            }
        }

        try {
            XBeanUtil.copyProperties(editUser, userDTO, false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException();
        }
        UserDomain saved = userDao.save(editUser);

        return saved.getId();
    }

    @Override
    public Integer modifyPassword(ModifyPasswordDTO dto) {
        UserDomain user = userDao.findByIdAndIsDeleted(dto.getId(), YesNoEnum.NO.getValue());
        if (null == user) {
            throw new BizException("User not exist!");
        }

        String prePassword = SHA256Utils.encryptPassword(dto.getPrePassword(), salt);
        if (!prePassword.equals(user.getPassword())) {
            throw new BizException("Original password error!");
        }

        user.setPassword(SHA256Utils.encryptPassword(dto.getNewPassword(), salt));
        UserDomain saved = userDao.save(user);

        return saved.getId();
    }

    @Override
    public Integer deleteUser(int id) {
        return null;
    }
}