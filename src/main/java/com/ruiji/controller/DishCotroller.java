package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.R;
import com.ruiji.dto.DishDto;
import com.ruiji.entity.Dish;
import com.ruiji.entity.DishFlavor;
import com.ruiji.entity.Setmeal;
import com.ruiji.entity.SetmealDish;
import com.ruiji.service.*;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishCotroller {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(name!=null,Dish::getName,name);
        lqw.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,lqw);

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            val categoryId = item.getCategoryId();
            val category = categoryService.getById(categoryId);
            val name1 = category.getName();
            dishDto.setCategoryName(name1);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);


        return R.success(dishDtoPage);

    }

    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping()
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("更新成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lqw.eq(Dish::getStatus,1);
        lqw.like(dish.getName()!=null,Dish::getName,dish.getName());
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);

        List<DishDto> dishWithFlavorList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            LambdaUpdateWrapper<DishFlavor> lqw2 = new LambdaUpdateWrapper<>();
            lqw2.eq(DishFlavor::getDishId,item.getId());
            dishDto.setFlavors(dishFlavorService.list(lqw2));
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishWithFlavorList);
    }

    @PostMapping("/status/0")
    public R<String> stop(@RequestParam @RequestBody List<Long> ids){
        LambdaUpdateWrapper<Dish> luw = new LambdaUpdateWrapper<>();
        luw.in(Dish::getId,ids).set(Dish::getStatus,0);
        dishService.update(luw);
        LambdaQueryWrapper<SetmealDish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.in(SetmealDish::getDishId,ids);
        List<SetmealDish> setmealDishes = setmealDishService.list(lqw1);
        List<Long> setmealIdList = setmealDishes.stream().map((item)->{
            return item.getSetmealId();
        }).distinct().collect(Collectors.toList());
        LambdaUpdateWrapper<Setmeal> luw2 = new LambdaUpdateWrapper<>();
        luw2.in(Setmeal::getId,setmealIdList).set(Setmeal::getStatus,0);
        setmealService.update(luw2);

        return R.success("停售菜品成功");
    }

    @PostMapping("/status/1")
    public R<String> start(@RequestParam @RequestBody List<Long> ids){
        LambdaUpdateWrapper<Dish> luw = new LambdaUpdateWrapper<>();
        luw.in(Dish::getId,ids).set(Dish::getStatus,1);
        dishService.update(luw);
        LambdaQueryWrapper<SetmealDish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.in(SetmealDish::getDishId,ids);
        List<SetmealDish> setmealDishes = setmealDishService.list(lqw1);
        List<Long> setmealIdList = setmealDishes.stream().map((item)->{
            return item.getSetmealId();
        }).distinct().collect(Collectors.toList());
        LambdaUpdateWrapper<Setmeal> luw2 = new LambdaUpdateWrapper<>();
        luw2.in(Setmeal::getId,setmealIdList).set(Setmeal::getStatus,1);
        setmealService.update(luw2);

        return R.success("起售菜品成功");
    }
}
