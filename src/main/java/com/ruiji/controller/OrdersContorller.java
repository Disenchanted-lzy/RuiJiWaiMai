package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.BaseContext;
import com.ruiji.common.R;
import com.ruiji.dto.OrdersDto;
import com.ruiji.entity.OrderDetail;
import com.ruiji.entity.Orders;
import com.ruiji.service.OrderDetailService;
import com.ruiji.service.OrdersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrdersContorller {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, String beginTime,String endTime){
        LambdaQueryWrapper<Orders> lqw= new LambdaQueryWrapper<>();
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        lqw.like(number!=null,Orders::getId,number);
        if(beginTime!=null&&endTime!=null){
            LocalDateTime start = LocalDateTime.parse(beginTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(endTime,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            lqw.between(Orders::getOrderTime,start,end);
        }
        ordersService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        Page<OrdersDto> resPageInfo = new Page<>();
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        Long currentUserId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId,currentUserId).orderByDesc(Orders::getCheckoutTime);
        ordersService.page(pageInfo,lqw);

        BeanUtils.copyProperties(pageInfo,resPageInfo,"records");
        List<OrdersDto> ordersDtoList = pageInfo.getRecords().stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId,item.getId());
            List<OrderDetail> list  = orderDetailService.list(lambdaQueryWrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());
        resPageInfo.setRecords(ordersDtoList);
        return R.success(resPageInfo);
    }

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order){
        ordersService.submit(order);
        return R.success("下单成功");
    }

    @PutMapping("")
    public R<String> order(@RequestBody Orders order){
        ordersService.updateById(order);
        return R.success("修改状态成功");
    }
}
