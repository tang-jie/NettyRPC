/**
 * Copyright (C) 2017 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.newlandframework.rpc.services.impl;

import com.newlandframework.rpc.services.JdbcPersonManage;
import com.newlandframework.rpc.services.pojo.Person;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:JdbcPersonManageImpl.java
 * @description:JdbcPersonManageImpl功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/9/25
 */
@Service
public class JdbcPersonManageImpl implements JdbcPersonManage {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String toString(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Transactional
    @Override
    public int save(Person p) {
        //your business logic code here!
        System.out.println("jdbc Person data[" + p + "] has save!");
        System.out.println(p);
        String sql = "insert into person(id,name,age,birthday) values(?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'))";
        System.out.println(sql);
        JdbcTemplate template = new JdbcTemplate(this.dataSource);
        template.update(sql, p.getId(), p.getName(), p.getAge(), toString(p.getBirthday()));

        return 0;
    }

    @Override
    public void query(Person p) {
        //your business logic code here!
        System.out.println("jdbc Person data[" + p + "] has query!");
        String sql = String.format("select * from person where id = %d", p.getId());
        JdbcTemplate template = new JdbcTemplate(this.dataSource);
        List<Map<String, Object>> rows = template.queryForList(sql);

        if (rows.size() == 0) {
            System.out.println("record doesn't exist!");
            return;
        } else {
            for (Map row : rows) {
                System.out.println(Integer.parseInt(row.get("ID").toString()));
                System.out.println((String) row.get("NAME"));
                System.out.println(Integer.parseInt(row.get("AGE").toString()));
                System.out.println(toString((Date) row.get("BIRTHDAY")));
                System.out.println("\n");
            }
        }
    }

    @Override
    public List<Person> query() {
        //your business logic code here!
        System.out.println("jdbc Person query!");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select * from person";
        JdbcTemplate template = new JdbcTemplate(this.dataSource);
        List<Map<String, Object>> rows = template.queryForList(sql);
        List<Person> list = new ArrayList<Person>();

        for (Map row : rows) {
            Person p = new Person();
            p.setId(Integer.parseInt(row.get("ID").toString()));
            p.setName((String) row.get("NAME"));
            p.setAge(Integer.parseInt(row.get("AGE").toString()));
            p.setBirthday((Date) row.get("BIRTHDAY"));
            list.add(p);
        }
        return list;
    }
}

