package com.system.business.channel.service.impl;

import com.system.business.channel.dao.ChannelDao;
import com.system.business.channel.domain.ChannelDomain;
import com.system.business.channel.dto.ChannelDto;
import com.system.business.channel.service.ChannelService;
import com.system.common.constants.YesNoEnum;
import com.system.common.dto.PageDTO;
import com.system.common.dto.PageQueryDTO;
import com.system.common.support.XBeanUtil;
import com.system.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    ChannelDao channelDao;

    @Override
    public Boolean addChannel(ChannelDto channelDto) {
        ChannelDomain exist = channelDao.findByChannelId(channelDto.getChannelId());
        if (null != exist) {
            throw new BizException("channel.channelId.exist");
        }

        ChannelDomain domain = new ChannelDomain();
        try {
            XBeanUtil.copyProperties(domain, channelDto, false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException();
        }

        channelDao.save(domain);

        return true;
    }

    @Override
    public Boolean deleteChannel(String channelId) {
        ChannelDomain exist = channelDao.findByChannelIdAndIsDeleted(channelId, YesNoEnum.NO.getValue());
        if (null == exist) {
            throw new BizException("channel.not.exist");
        }

        exist.setIsDeleted(YesNoEnum.YES.getValue());
        exist.setUpdateTime(new Date());
        channelDao.save(exist);

        return true;
    }

    @Override
    public Integer deleteChannelBatch(List<String> channelIds) {
        Integer i = channelDao.deleteChannelBatchByChannelId(channelIds, YesNoEnum.YES.getValue());

        return i;
    }

    @Override
    public Boolean updateChannel(ChannelDto channelDto) {
        ChannelDomain exist = channelDao.findByChannelIdAndIsDeleted(channelDto.getChannelId(), YesNoEnum.NO.getValue());
        if (null == exist) {
            throw new BizException("channel.not.exist");
        }

        try {
            XBeanUtil.copyProperties(exist, channelDto, false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException();
        }

        exist.setUpdateTime(new Date());

        channelDao.save(exist);

        return true;
    }

    @Override
    public ChannelDto getChannel(String channelId) {
        ChannelDomain exist = channelDao.findByChannelIdAndIsDeleted(channelId, YesNoEnum.NO.getValue());
        if (null == exist) {
            throw new BizException("channel.not.exist");
        }

        ChannelDto channel = new ChannelDto();
        try {
            XBeanUtil.copyProperties(channel, exist, false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException();
        }

        return channel;
    }

    @Override
    public PageDTO<ChannelDto> getChannels(PageQueryDTO pageQueryDTO) {

        // 指定查询条件
        Specification<ChannelDomain> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            //默认查询未删除
            predicates.add(builder.equal(root.get("isDeleted"), YesNoEnum.NO.getValue()));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        // 指定查询页面以及排序
        PageRequest request = new PageRequest(pageQueryDTO.getPage() - 1, pageQueryDTO.getPageSize(), Sort.Direction
                .fromString(pageQueryDTO.getOrderDesc()), pageQueryDTO.getOrder());

        Page<ChannelDomain> findList = channelDao.findAll(spec, request);


        PageDTO<ChannelDto> result = PageDTO.of(findList, domain -> {
            ChannelDto dto = new ChannelDto();

            try {
                XBeanUtil.copyProperties(dto, domain, false);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException();
            }

            return dto;
        });

        return result;
    }

    @Override
    public List<String> getAllChannelName() {
        List<ChannelDomain> findAll = channelDao.findAllChannels();
        List<String> allChannelName = new ArrayList<>();
        for (ChannelDomain domain : findAll) {
            allChannelName.add(domain.getChannelName());
        }

        return allChannelName;
    }
}
