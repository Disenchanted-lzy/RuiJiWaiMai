package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruiji.common.BaseContext;
import com.ruiji.common.R;
import com.ruiji.entity.ShoppingCart;
import com.ruiji.service.ShoppingCartService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        Long currentUserId = BaseContext.getCurrentId();
        lqw.eq(ShoppingCart::getUserId,currentUserId);
        if(shoppingCart.getDishId()!=null){
            lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else{
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(lqw);
        if(cartServiceOne!=null){
            cartServiceOne.setNumber(cartServiceOne.getNumber()+1);
            shoppingCartService.updateById(cartServiceOne);
        }else{
            shoppingCart.setUserId(currentUserId);
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        cartServiceOne.setCreateTime(LocalDateTime.now());
        return R.success(cartServiceOne);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> update(@RequestBody ShoppingCart shoppingCart){
        Long currentUserId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart>  lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,currentUserId);
        if(shoppingCart.getDishId()!=null){
            lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else{
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart updateOneService = shoppingCartService.getOne(lqw);
        Integer number = updateOneService.getNumber();
        updateOneService.setNumber(updateOneService.getNumber() - 1);
        if(number==1){
            shoppingCartService.removeById(updateOneService);
        }else{
            shoppingCartService.updateById(updateOneService);
        }
        return R.success(updateOneService);

    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long currentUserId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,currentUserId).orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentUserId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,currentUserId);
        shoppingCartService.remove(lqw);
        return R.success("清除购物车成功");
    }
}
