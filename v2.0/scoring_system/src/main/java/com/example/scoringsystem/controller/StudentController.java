package com.example.scoringsystem.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.example.scoringsystem.bean.PageRequest;
import com.example.scoringsystem.bean.ResponseData;
import com.example.scoringsystem.bean.User;
import com.example.scoringsystem.bean.UserWithTaskAndScore;
import com.example.scoringsystem.mapper.StudentMapper;
import com.example.scoringsystem.service.StudentService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Pattern;

//学生控制类
@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;

    @Autowired
    StudentMapper studentMapper;

//增加单个学生
    @RequestMapping("/selSingleStudent/{id}")
    @ResponseBody
    public ResponseData selSingleStudent(@PathVariable String id){
        ResponseData responseData;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        boolean isNumber = pattern.matcher(id).matches();
        if (!isNumber) {
            responseData = new ResponseData("查询失败1","1001","[]");
            return responseData;
        }

        User student = studentService.selSingleStudent(id);
        if (student == null){
            responseData = new ResponseData("查询失败2","1002","[]");
            return responseData;
        }

        responseData = new ResponseData("查询成功","200",student);
        return responseData;
    }

//返回所有学生
    @RequestMapping("/selAll")
    @ResponseBody
    public List<User> selAllStudent(){
        return studentService.selAll();
    }

//分页查询学生数据
    @RequestMapping("/selByPage")
    @ResponseBody
    public ResponseData selByPage(PageRequest pageRequest){
        PageInfo<User> pageInfo = studentService.selByPage(pageRequest);
        return new ResponseData("返回的学生列表","200",pageInfo);
    }

    @RequestMapping("/selStudentByPageAndClassRoomId")
    @ResponseBody
    public ResponseData selStudentByPageAndClassRoomId(PageRequest pageRequest,String classRoomId){
        PageInfo<User> pageInfo = studentService.selByPageAndClassRoomId(pageRequest,classRoomId);
        return new ResponseData("返回的学生列表","200",pageInfo);
    }

//增加单个学生
    @RequestMapping("/addSingleStudent")
    @ResponseBody
    public ResponseData addSingleStudent( User user){
        boolean result;
        ResponseData responseData;

        responseData = studentService.isRightStuData(user);
        if (!responseData.getCode().equals("200")){
            return responseData;
        }

        result = studentService.addSingleStudent(user);
        if (!result){
            responseData = new ResponseData("学生账户添加失败","1003","[]");
            return responseData;
        }

        responseData = new ResponseData("学生账户添加成功","200","[]");
        return responseData;
    }

//删除单个学生
    @RequestMapping("/delStuById/{id}")
    @ResponseBody
    public ResponseData delStudent(@PathVariable String id){
        boolean result;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        boolean isNumber = pattern.matcher(id).matches();
        if (!isNumber) {
            return new ResponseData("删除失败","1001","[]");
        }
        User user = studentMapper.selStuById(id);
        if (user == null){
            return new ResponseData("没有这个学生账户","1002","[]");
        }

        result = studentService.delStudent(id);
        if (result){
            return new ResponseData("删除学生成功","200","[]");
        }
        return new ResponseData("删除失败","1003","[]");
    }

//更新学生数据
    @RequestMapping("/updStudent1")
    @ResponseBody
    public ResponseData updStudent1(@RequestBody User user){
        if (user == null){
            return new ResponseData("没有要修改学生数据","1001","[]");
        }
//        if (user.getAccount() == null || user.getUserName() == null){
//            return new ResponseData("没有要修改学生账户信息","1002","[]");
//        }
        log.info(user.toString());
        User user1 = studentMapper.selStuById(user.getId());
        if (user1 == null){
            return new ResponseData("没有这个学生账户","1002","[]");
        }

        boolean result = studentService.updStudent1(user);
        if (result){
            return new ResponseData("修改成功","200","[]");
        }
        return new ResponseData("修改失败","1001","[]");
    }

    @RequestMapping("/updStudent2")
    @ResponseBody
    public ResponseData updStudent2(@RequestBody User user){
        if (user == null){
            return new ResponseData("没有要修改学生数据","1001","[]");
        }
        if (user.getAccount() == null || user.getUserName() == null){
            return new ResponseData("没有要修改学生账户信息","1002","[]");
        }

        boolean result = studentService.updStudent2(user);
        if (result){
            return new ResponseData("修改成功","200","[]");
        }
        return new ResponseData("修改失败","1001","[]");
    }

    @RequestMapping("/updStudent3")
    @ResponseBody
    public ResponseData updStudent3(@RequestBody User user){
        if (user == null){
            return new ResponseData("没有要修改学生数据","1001","[]");
        }

        boolean result = studentService.updStudent3(user);
        if (result){
            return new ResponseData("修改成功","200","[]");
        }
        return new ResponseData("修改失败","1001","[]");
    }

    /**
    * @Description: 更改用户密码
    * @Param: [user, oldPwd]
    * @return: com.example.scoringsystem.bean.ResponseData
    * @Date: 2021/7/16
    */
    @RequestMapping("/updStudentPwd")
    @ResponseBody
    public ResponseData updStudentPwd(User user,String oldPwd){
        if (user.getId() == null||user.getPassword() == null){
            return new ResponseData("id或密码为空","1001","[]");
        }

        boolean result = studentService.verifyPassword(user,oldPwd);
        if (result){
            return new ResponseData("修改成功","200","[]");
        }
        return new ResponseData("修改失败,可能是原密码错误!","1001","[]");
    }

    @RequestMapping("/importAll")
    @ResponseBody
    public ResponseData importAll(MultipartFile excel){
        int size = 0;
        log.info("上传的文件名称：" + excel.getOriginalFilename());
        ImportParams params = new ImportParams();
        params.setTitleRows(1);//一级标题
        params.setHeadRows(1);//header标题
        try {
            List<User> studentList = ExcelImportUtil.importExcel(excel.getInputStream(), User.class, params);
            log.error("导入的数量:" + studentList.size());
            size = studentService.insStudentBatch(studentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (size == 0)
            return new ResponseData("插入的表内没有数据","1002","[]");
        else if (size < 0){
            size = -size;
            return new ResponseData("学生列表文件中的第"+String.valueOf(size)+"学生数据有误，插入失败","1003","[]");
        }
        else {
            return new ResponseData("成功插入学生数据"+String.valueOf(size)+"条","200","[]");
        }
    }

    @ResponseBody
    @RequestMapping("/chart")
    public ResponseData chart(){
        List<UserWithTaskAndScore> chartDate = studentService.chart();
        return new ResponseData("成功返回千帆图数据","200",chartDate);
    }

}