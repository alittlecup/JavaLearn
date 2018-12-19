package com.system.business.operation.controller;

import com.system.business.operation.vo.OperationVO;
import com.system.common.constants.WebConstants;
import com.system.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Income", description = "收入相关接口")
@RequestMapping(value = WebConstants.API_PREFIX + "/operation")
public class OperationController {

    @ApiOperation("获取收入")
    @GetMapping
    public ResponseVO<OperationVO> getIncome(Integer page, Integer pageSize) {
        OperationVO incomeVO = new OperationVO();
//        List<OperationVO.Income> list = new ArrayList<>();
//        Random random = new Random();
//        for (int i = 0; i < 58; i++) {
//            OperationVO.Income operation = new OperationVO.Income();
//            operation.setDate(DateUtils.getFormatDate(DateUtils.randomDate("2017-1-1", "2018-12-30")));
//            operation.setChannelId("pad"+random.nextInt(500));
//            operation.setChannelName("phoenix");
//            operation.setPv(random.nextInt(10000));
//            operation.setUv(random.nextInt(10000));
//            operation.setIncome(random.nextInt(1000));
//            list.add(operation);
//        }
//
//        incomeVO.setTotal(list.size());
//        incomeVO.setList(list);

        return ResponseVO.successResponse(incomeVO);
    }
}