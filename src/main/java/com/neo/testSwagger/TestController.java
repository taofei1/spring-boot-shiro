package com.neo.testSwagger;

import com.neo.exception.ErrorEnum;
import com.neo.util.Response;
import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * swagger ���Է���
 *
 * @author ruoyi
 */
@Api("�û���Ϣ����")
@RestController
@RequestMapping("/test/*")
public class TestController {
    private final static List<Test> testList = new ArrayList<>();

    {
        testList.add(new Test("1", "admin", "admin123"));
        testList.add(new Test("2", "ry", "admin123"));
    }

    @ApiOperation("��ȡ�б�")
    @GetMapping("list")
    public List<Test> testList() {
        return testList;
    }

    @ApiOperation("�����û�")
    @PostMapping("save")
    public Response save(Test test) {
        return testList.add(test) ? Response.success() : Response.fail(ErrorEnum.UNKNOWN_ERROR);
    }

    @ApiOperation("�����û�")
    @ApiImplicitParam(name = "Test", value = "�����û���Ϣ", dataType = "Test")
    @PutMapping("update")
    public Response update(Test test) {
        return testList.remove(test) && testList.add(test) ? Response.success() : Response.fail(ErrorEnum.UNKNOWN_ERROR);
    }

    @ApiOperation("ɾ���û�")
    @ApiImplicitParam(name = "Tests", value = "�����û���Ϣ", dataType = "Test")
    @DeleteMapping("delete")
    public Response delete(Test test) {
        return testList.remove(test) ? Response.success() : Response.fail(ErrorEnum.UNKNOWN_ERROR);
    }
}

class Test {
    private String userId;
    private String username;
    private String password;

    public Test() {

    }

    public Test(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Test test = (Test) o;

        return userId != null ? userId.equals(test.userId) : test.userId == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
