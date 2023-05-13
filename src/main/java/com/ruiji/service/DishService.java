package com.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.dto.DishDto;
import com.ruiji.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
