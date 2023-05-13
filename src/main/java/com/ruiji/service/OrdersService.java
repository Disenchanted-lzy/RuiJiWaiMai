package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders order);
}
