package com.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruiji.common.R;
import com.ruiji.entity.Employee;
import com.ruiji.service.EmployeeService;
import com.ruiji.service.impl.EmployeeImpl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeImpl employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request , @RequestBody Employee employee){
        String pd = employee.getPassword();
        pd = DigestUtils.md5DigestAsHex(pd.getBytes(StandardCharsets.UTF_8));

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if(emp==null) return R.error("登录失败");

        if(!emp.getPassword().equals(pd)) return R.error("登录失败");

        if(emp.getStatus()==0) return R.error("账号被禁用");

        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping("")
    public R<String> save(@RequestBody Employee emp,HttpServletRequest request){
        log.info("新增员工 信息{}",emp.toString());
        emp.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
//        emp.setCreateTime(LocalDateTime.now());
//        emp.setUpdateTime(LocalDateTime.now());
//        emp.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        emp.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.save(emp);
        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<Employee>();
        lambdaQueryWrapper.like(name!=null,Employee::getName,name);

        lambdaQueryWrapper.orderByDesc(Employee::getCreateTime);

        employeeService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Employee emp,HttpServletRequest request){
//        emp.setUpdateTime(LocalDateTime.now());
//        emp.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(emp);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee emp = employeeService.getById(id);
        log.info(emp.toString());
        if(emp==null)
            return R.error("没有查询到员工信息");
        return R.success(emp);
    }
}
