package com.ruiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.common.BaseContext;
import com.ruiji.common.CustomException;
import com.ruiji.entity.*;
import com.ruiji.mapper.OrdersMapper;
import com.ruiji.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Transactional
    @Override
    public void submit(Orders order) {
        Long currentUserId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(ShoppingCart::getUserId,currentUserId);
        List<ShoppingCart> userShoppingCartList = shoppingCartService.list(lqw1);
        shoppingCartService.remove(lqw1);
        if(userShoppingCartList.size()==0){
            throw new CustomException("购物车为空");
        }
        User user = userService.getById(currentUserId);
        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());
        if(addressBook==null){
            throw new CustomException("地址为空，无法下单");
        }

        Long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetailList = userShoppingCartList.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        order.setId(orderId);
        order.setUserId(currentUserId);
        order.setConsignee(addressBook.getConsignee());
        order.setPhone(addressBook.getPhone());
        order.setNumber(String.valueOf(orderId));
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setStatus(2);
        order.setAmount(new BigDecimal(amount.get()));
        order.setUserName(user.getName());
        order.setAddress((addressBook.getProvinceName()==null? "":addressBook.getProvinceName())+
                addressBook.getCityName()==null? "":addressBook.getCityName()+
                addressBook.getDistrictName()==null?"":addressBook.getDistrictName()+
                addressBook.getDetail()==null?"":addressBook.getDetail());

        this.save(order);
        orderDetailService.saveBatch(orderDetailList);


    }
}
