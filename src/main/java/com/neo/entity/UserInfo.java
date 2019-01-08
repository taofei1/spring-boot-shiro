package com.neo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "sys_user_info")
public class UserInfo implements Serializable {
    @Id
    @GeneratedValue
    private Integer uid;
    @Column(unique =true)
    @NotBlank(message = "{user.username.notBlank}")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,16}$",message = "{user.username.errorPattern}")
    private String username;//帐号
    @Column(unique =true)
   // @NotBlank(message = "{user.name.notBlank}")
  //  @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$",message = "{user.email.errorPattern}")
    private String email;
    private String emailPassword;
    private String emailHost;
    private String emailPort;
    @Column(name = "avatar")
    private String avatar;
  //  @NotBlank(message = "{user.email.notBlank}")
  //  @Email(message = "{user.name.errorPattern}")
    private String name;//名称（昵称或者真实姓名，不同系统不同定义）
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="yyyy-MM-dd")
  //  @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",message = "{user.birthday.errorPattern}")
    private Date birthday;
    private String mobile;
    private Character gender;
    @JsonIgnore
    private String password; //密码;
    @JsonIgnore
    private String salt;//加密密码的盐
    private Integer state;//用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.
    private Integer isFirst;//是否登陆过 0：未登录 1：已登录
    @ManyToMany(fetch= FetchType.EAGER)//立即从数据库中进行加载数据;
    @JoinTable(name = "SysUserRole", joinColumns = { @JoinColumn(name = "uid") }, inverseJoinColumns ={@JoinColumn(name = "roleId") })
    private List<Role> roleList=new ArrayList<>();// 一个用户具有多个角色


    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="SysUserDept",joinColumns = {@JoinColumn(name="uid",referencedColumnName = "uid")},inverseJoinColumns ={@JoinColumn(name = "deptId",referencedColumnName = "id") })
    private Set<Dept> deptList=new HashSet<>();//一个用户可以分管多个部门
    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;
    public Set<Dept> getDeptList() {
        return deptList;
    }

    public void setDeptList(Set<Dept> deptList) {
        this.deptList = deptList;
    }
    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public enum Gender{
        M,F
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public Integer getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(Integer isFirst) {
        this.isFirst = isFirst;
    }


    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public String getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(String emailPort) {
        this.emailPort = emailPort;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 密码盐.
     * @return
     */
    @JsonIgnore
    public String getCredentialsSalt(){
        return this.salt;
    }
    //重新对盐重新进行了定义，用户名+salt，这样就更加不容易被破解


    @Override
    public String toString() {
        return "UserInfo{" + "uid=" + uid + ", username='" + username + '\'' + ", email='" + email + '\'' + ", name='" + name + '\'' + ", birthday=" + birthday + ", mobile='" + mobile + '\'' + ", gender=" + gender + ", password='" + password + '\'' + ", salt='" + salt + '\'' + ", state=" + state + ", roleList=" + roleList + ", deptList=" + deptList + '}';
    }
}