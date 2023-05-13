package com.ruiji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruiji.common.CustomException;
import com.ruiji.entity.Category;
import com.ruiji.entity.Dish;
import com.ruiji.entity.Setmeal;
import com.ruiji.mapper.CategoryMapper;
import com.ruiji.service.CategoryService;
import com.ruiji.service.DishService;
import com.ruiji.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private DishService dishService;

    @Override
    public void remove(Long id){
        LambdaQueryWrapper<Dish> dishlqw = new LambdaQueryWrapper<>();
        dishlqw.eq(Dish::getId,id);
        long count1 = dishService.count(dishlqw);
        if(count1>0){
            throw new CustomException("当前分类下关联了菜品，无法删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getId,id);
        long count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            throw new CustomException("当前分类下关联了套餐，无法删除");
        }
        super.removeById(id);
    }
}
