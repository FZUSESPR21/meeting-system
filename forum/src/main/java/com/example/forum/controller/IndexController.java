package com.example.forum.controller;


import com.example.forum.bean.User;
import com.example.forum.bean.UserResponsBody;
import com.example.forum.service.serviceImpl.IndexServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController
{
    @Autowired
    IndexServiceImpl indexSerice;

    @RequestMapping("/registered")
    @ResponseBody
    public UserResponsBody registered(User user)
    {
        System.out.println("开始登录......");
        indexSerice.registered(user);

        UserResponsBody userResponsBody=new UserResponsBody();
        userResponsBody.setMsg("注册成功");
        userResponsBody.setCode("200");
        return userResponsBody;
    }

    @RequestMapping("/chairManList")
    @ResponseBody
    public UserResponsBody getChairManList(User user)
    {
        System.out.println("开始登录......");


        UserResponsBody userResponsBody=new UserResponsBody();
        userResponsBody.setMsg("注册成功");
        userResponsBody.setCode("200");
        userResponsBody.setData(indexSerice.showMeeting(user));
        return userResponsBody;
    }

    @RequestMapping("/getmessage")
    @ResponseBody
    public UserResponsBody getmessage(User user)
    {
        System.out.println("获取Message的用户:"+user);

        UserResponsBody userResponsBody=new UserResponsBody();
        userResponsBody.setMsg("获取成功");
        userResponsBody.setCode("200");
        userResponsBody.setData(indexSerice.showMessage(user));

        return userResponsBody;
    }


}