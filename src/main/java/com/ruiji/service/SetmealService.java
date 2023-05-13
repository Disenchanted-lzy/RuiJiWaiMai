package com.ruiji.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruiji.dto.SetmealDto;
import com.ruiji.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithSetmealDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    SetmealDto getWithDishes(Long id);

    void updateWithDishes(SetmealDto setmealDto);
}
