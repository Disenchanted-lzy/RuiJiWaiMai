package com.ruiji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruiji.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
