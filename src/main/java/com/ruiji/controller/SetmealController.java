package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.R;
import com.ruiji.dto.SetmealDto;
import com.ruiji.entity.Setmeal;
import com.ruiji.service.CategoryService;
import com.ruiji.service.SetmealDishService;
import com.ruiji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping()
    public R<String> setmeal(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithSetmealDish(setmealDto);
        return R.success("保存套餐成功");

    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> page1 = new Page<>(page,pageSize);
        Page<SetmealDto> page2 = new Page<>();
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name!=null,Setmeal::getName,name);
        setmealService.page(page1,lqw);
        BeanUtils.copyProperties(page1,page2,"records");

        List<Setmeal> list2 = page1.getRecords();
        List<SetmealDto> list = list2.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            val category = categoryService.getById(item.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        page2.setRecords(list);
        return R.success(page2);
    }

    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/0")
    public R<String> stop(@RequestBody @RequestParam List<Long> ids){
        LambdaUpdateWrapper<Setmeal> luw = new LambdaUpdateWrapper<>();
        luw.in(Setmeal::getId,ids).set(Setmeal::getStatus,0);
        setmealService.update(luw);
        return R.success("停售成功");
    }

    @PostMapping("/status/1")
    public R<String> start(@RequestBody @RequestParam List<Long> ids){
        LambdaUpdateWrapper<Setmeal> luw = new LambdaUpdateWrapper<>();
        luw.in(Setmeal::getId,ids).set(Setmeal::getStatus,1);
        setmealService.update(luw);
        return R.success("起售成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> list(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getWithDishes(id);
        return R.success(setmealDto);
    }

    @PutMapping()
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDishes(setmealDto);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Long categoryId,Integer status){
        LambdaUpdateWrapper<Setmeal> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(categoryId!=null,Setmeal::getCategoryId,categoryId).eq(status!=null,Setmeal::getStatus,status);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lqw);
        return R.success(list);
    }
}
