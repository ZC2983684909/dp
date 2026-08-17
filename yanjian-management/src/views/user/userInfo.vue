<template>
  <div class="app-container">
    <el-form ref="forma" label-position="left" v-if="pageType == 1">
      <el-row>
        <el-form-item label="昵称：" label-width="110px" class="el-col-8">
          <el-input v-model="nickName" style="width: 75%;" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="出生日期：" label-width="110px" class="el-col-8">
          <el-date-picker v-model="birthDate" format="yyyy 年 MM 月 dd 日" value-format="yyyy-MM-dd" style="width: 75%;"
            type="date" placeholder="请选择出生日期" />
        </el-form-item>
        <el-form-item label="性别：" label-width="110px" class="el-col-8">
          <el-select v-model="sex" style="width: 75%;" size="medium" placeholder="性别" clearable class="filter-item ">
            <el-option label="请选择" value="-1" />
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="身高：" label-width="110px" class="el-col-8">
          <el-input v-model="height" type="number" style="width: 75%;" placeholder="请输入身高(cm)"></el-input>
        </el-form-item>
        <el-form-item label="职业：" label-width="110px" class="el-col-8">
          <el-input v-model="jobMes" style="width: 75%;" placeholder="请输入职业"></el-input>
        </el-form-item>
        <el-form-item label="年薪：" label-width="110px" class="el-col-8">
          <el-select v-model="salarys" style="width: 75%;" size="medium" placeholder="年薪" clearable
            class="filter-item ">
            <el-option label="请选择" value="-1" />
            <el-option label="保密" value="保密" />
            <el-option label="小于10w" value="小于10w" />
            <el-option label="10~20w" value="10~20w" />
            <el-option label="20~30w" value="20~30w" />
            <el-option label="30~50w" value="30~50w" />
            <el-option label="50~100w" value="50~100w" />
            <el-option label="大于100w" value="大于100w" />
          </el-select>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="理想对象：" label-width="110px" class="el-col-8">
          <el-input v-model="idealFriend" type="textarea" :rows="2" style="width: 75%;" placeholder="请输入择偶要求">
          </el-input>
        </el-form-item>
        <el-form-item label-width="110px" label="自我描述：" class="el-col-8">
          <el-input v-model="selfDescription" type="textarea" :rows="2" style="width: 75%;"
            placeholder="请输入关于我"></el-input>
        </el-form-item>
        <el-form-item label="标签：" label-width="110px" class="el-col-8">
          <el-input v-model="fondTags" style="width: 75%;" placeholder="请输入兴趣标签,分割"></el-input>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="毕业学校：" label-width="110px" class="el-col-8">
          <el-input v-model="school" style="width: 75%;" placeholder="请输入毕业学校"></el-input>
        </el-form-item>
        <el-form-item label="最高学历：" label-width="110px" class="el-col-8">
          <el-select v-model="education" style="width: 75%;" size="medium" placeholder="最高学历 " clearable
            class="filter-item ">
            <el-option label="请选择" value="-1" />
            <el-option label="专科" value="专科" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号：" label-width="110px" class="el-col-8">
          <el-input v-model="phone" style="width: 75%;" placeholder="请输入手机号"></el-input>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label-width="110px" label="相册：">
          <el-upload action="https://api.doreasonable.com/file/upload" list-type="picture-card"
            :on-preview="handlePictureCardPreview" :on-remove="handleRemove" :file-list="personalPhoto"
            :show-file-list="true" :on-success="handleSuccess">
            <i class="el-icon-plus"></i>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label-width="110px" label="用户状态：" class="el-col-8">
          <el-radio v-model="status" label="ENABLE">启用</el-radio>
          <el-radio v-model="status" label="DISABLE">禁用</el-radio>
        </el-form-item>
      </el-row>
      <el-form-item>
        <el-button type="primary" size="medium" @click="submit()">提交</el-button>
        <el-button size="medium" @click="closeExamine()">返回</el-button>
      </el-form-item>
    </el-form>

    <el-form ref="forma" label-position="left" v-if="pageType == 2">
      <el-row>
        <el-form-item label-width="110px" label="头像：">
          <el-image style="width: 80px; height: 80px;" :src="editUserData.avatar" class="el-avatar--circle"
            :preview-src-list="[editUserData.avatar]" fit="fill"></el-image>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="城市：" label-width="110px" class="el-col-8">
          {{editUserData.residentialCity}}
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="微信号：" label-width="110px" class="el-col-8">
          {{editUserData.wechat}}
        </el-form-item>
        <el-form-item label="注册时间：" label-width="110px" class="el-col-8">
          {{editUserData.createTime}}
        </el-form-item>
        <el-form-item label="活跃时间：" label-width="110px" class="el-col-8">
          {{editUserData.latelyTime}}
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="用户类型：" label-width="110px" class="el-col-8">
          <el-tag type="success" v-if="editUserData.userType=='1'">正常</el-tag>
          <el-tag type="danger" v-if="editUserData.userType=='2'">虚拟</el-tag>
        </el-form-item>
        <el-form-item label="学历认证：" label-width="110px" class="el-col-8">
          <el-tag type="success" v-if="editUserData.eduAuth == '3'">通过</el-tag>
          <el-tag type="danger" v-if="editUserData.eduAuth == '4'">拒绝</el-tag>
        </el-form-item>
        <el-form-item label="实名认证：" label-width="110px" class="el-col-8">
          <el-tag type="warning" v-if="editUserData.idAuth=='2'">审核中</el-tag>
          <el-tag type="success" v-if="editUserData.idAuth=='3'">已通过</el-tag>
          <el-tag type="danger" v-if="editUserData.idAuth=='4'">已拒绝</el-tag>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="昵称：" label-width="110px" class="el-col-8">
          <el-input v-model="nickName" style="width: 75%;" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="出生日期：" label-width="110px" class="el-col-8">
          <el-date-picker v-model="birthDate" format="yyyy 年 MM 月 dd 日" value-format="yyyy-MM-dd" style="width: 75%;"
            type="date" placeholder="请选择出生日期" />
        </el-form-item>
        <el-form-item label="性别：" label-width="110px" class="el-col-8">
          <el-select v-model="sex" style="width: 75%;" size="medium" placeholder="性别" clearable class="filter-item ">
            <el-option label="请选择" value="-1" />
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="身高：" label-width="110px" class="el-col-8">
          <el-input v-model="height" type="number" style="width: 75%;" placeholder="请输入身高(cm)"></el-input>
        </el-form-item>
        <el-form-item label="职业：" label-width="110px" class="el-col-8">
          <el-input v-model="jobMes" style="width: 75%;" placeholder="请输入职业"></el-input>
        </el-form-item>
        <el-form-item label="年薪：" label-width="110px" class="el-col-8">
          <el-select v-model="salarys" style="width: 75%;" size="medium" placeholder="年薪" clearable
            class="filter-item ">
            <el-option label="请选择" value="-1" />
            <el-option label="还是学生" value="还是学生" />
            <el-option label="小于10万" value="小于10万" />
            <el-option label="10万-20万" value="10万-20万" />
            <el-option label="20万-30万" value="20万-30万" />
            <el-option label="30万-40万" value="30万-40万" />
            <el-option label="40万-60万" value="40万-60万" />
            <el-option label="60万-80万" value="60万-80万" />
            <el-option label="80万-100万" value="还80万-100万是学生" />
            <el-option label="大于100万" value="大于100万" />
            <el-option label="保密" value="保密" />


          </el-select>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="理想对象：" label-width="110px" class="el-col-8">
          <el-input v-model="idealFriend" type="textarea" :rows="2" style="width: 75%;" placeholder="请输入择偶要求">
          </el-input>
        </el-form-item>
        <el-form-item label-width="110px" label="自我描述：" class="el-col-8">
          <el-input v-model="selfDescription" type="textarea" :rows="2" style="width: 75%;"
            placeholder="请输入关于我"></el-input>
        </el-form-item>
        <el-form-item label="标签：" label-width="110px" class="el-col-8">
          <el-input v-model="fondTags" style="width: 75%;" placeholder="请输入兴趣标签,分割"></el-input>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="毕业学校：" label-width="110px" class="el-col-8">
          <el-input v-model="school" style="width: 75%;" placeholder="请输入毕业学校"></el-input>
        </el-form-item>
        <el-form-item label="最高学历：" label-width="110px" class="el-col-8">
          <el-select v-model="education" style="width: 75%;" size="medium" placeholder="最高学历 " clearable
            class="filter-item ">
            <el-option label="请选择" value="-1" />
            <el-option label="专科" value="专科" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号：" label-width="110px" class="el-col-8">
          <el-input v-model="phone" style="width: 75%;" placeholder="请输入手机号"></el-input>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label-width="110px" label="相册：">
          <el-upload action="https://api.doreasonable.com/file/upload" list-type="picture-card"
            :on-preview="handlePictureCardPreview" :on-remove="handleRemove" :file-list="personalPhoto"
            :show-file-list="true" :on-success="handleSuccess">
            <i class="el-icon-plus"></i>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label-width="110px" label="用户状态：" class="el-col-8">
          <el-radio v-model="status" label="ENABLE">启用</el-radio>
          <el-radio v-model="status" label="DISABLE">禁用</el-radio>
        </el-form-item>
      </el-row>

      <el-form-item v-if="editUserData.userType=='2'">
        <el-button type="primary" size="medium" @click="submit()">提交</el-button>
        <el-button size="medium" @click="closeExamine()">返回</el-button>
      </el-form-item>
      <el-form-item v-if="editUserData.userType=='1'">
        <el-button type="primary" size="medium" @click="submitcheck()">审核</el-button>
        <el-button size="medium" @click="closeExamine()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import {
    userUpdate,
    getuserInfo,
    userCheck
  } from '@/api/user'
  export default {
    filters: {},
    data() {
      return {
        selfDescription: "",
        fondTags: "",
        salarys: "",
        birthDate: "",
        education: "",
        educationalType: "",
        emotional: "",
        sex: "",
        height: '',
        id: 0,
        personalPhoto: [],
        fondTags: "",
        loveGoal: "",
        idealFriend: "",
        marriage: "",
        nickName: "",
        jobMes: "",
        school: "",
        status: "",
        phone:"",
        // 相册
        dialogImageUrl: '',
        dialogVisible: false,
        // 修改时用户id
        editUserId: "",
        pageType: 1, //页面类型1:新增2：修改

        editUserData: "", //用户数据
      }
    },
    created() {
      var that = this;
      const id = this.$route.params && this.$route.params.id
      const type = this.$route.params && this.$route.params.type;
      this.pageType = type;
      this.$route.matched[0].redirect = "/user/index"
      if (type == 1) {
        // 新增
        this.$route.meta.title = "新增前台用户"
      } else if (type == 2) {
        // 修改
        this.editUserId = id;
        this.$route.meta.title = "前台用户详情"
        getuserInfo(id).then(response => {
          console.log(response.data)
          var data = response.data;
          this.editUserData = data;
          this.selfDescription = data.selfDescription;
          this.salarys = data.salarys;
          this.birthDate = data.birthDate;
          this.education = data.education;
          this.educationalType = data.educationalType;
          this.emotional = data.emotional;
          this.sex = data.sex;
          this.height = data.height;
          var personalPhoto = data.personalPhoto;
          this.fondTags = data.fondTags;
          this.loveGoal = data.loveGoal;
          this.idealFriend = data.idealFriend;
          this.marriage = data.marriage;
          this.nickName = data.nickName;
           this.phone = data.phone;
          this.jobMes = data.jobMes;
          this.school = data.school;
          this.status = data.status;

          this.$nextTick(() => { //下面是图片数组，在页面更新后面赋值！！
            personalPhoto.map((v) => {
              var item = {
                url: v,
                name: "photo"
              }
              this.personalPhoto.push(item);
            })
          })

          console.log(this.personalPhoto)
          this.examineData = response.data;
        })
      }
      // this.pageType = type;
      // this.dqUserId = id;
      console.log(id);
      console.log(type);
      console.log(this.$route);
      // if (type == 1) {
      //   this.getuserInfoTy(id);
      //   this.$route.meta.title = "资料审核详情"
      //   document.title = this.$route.meta.title;
      //   this.$route.matched[0].redirect = "/example/table"
      // } else if (type == 2) {
      //   this.getuserInfoTy(id);
      //   this.$route.meta.title = "学历审核详情"
      //   document.title = this.$route.meta.title;
      //   this.$route.matched[0].redirect = "/example/schoolingList"
      // } else if (type == 3) {
      //   this.getuserInfoTy(id);
      //   this.$route.meta.title = "实名审核详情"
      //   document.title = this.$route.meta.title;
      //   this.$route.matched[0].redirect = "/example/identityList"
      // } else {
      //   this.$message({
      //     message: "非法进入",
      //     type: 'error'
      //   })
      // }
    },
    methods: {
      handleRemove(file, fileList) {
        for (let i = 0; i < this.personalPhoto.length; i++) {
          let attach = this.personalPhoto[i].url;
          if (file.url == attach) {
            this.personalPhoto.splice(i, 1)
          }
        }
      },
      handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      },
      // 图片上传成功
      handleSuccess(res, file, fileList) {
        console.log(res)
        console.log(file)
        // console.log(fileList)
        if (res.code === 200) {
          // this.personalPhoto.push(res.data.url)
          var item = {
            url: res.data.url,
            name: "photo"
          }
          this.personalPhoto.push(item)
        }
        console.log(this.personalPhoto)
      },
      // 返回上一页
      closeExamine() {
        this.$router.go(-1);
      },
      // 添加用户
      submit() {
        var that = this;
        // this.birthDate = this.birthDate+" 00:00:00";
        // console.log(that.birthDate);
        // return false;
        if (this.nickName == "") {
          this.$message({
            message: '昵称不能为空',
            type: 'error'
          })
          return false;
        }
        if (this.birthDate == "" && this.birthDate != null) {
          this.$message({
            message: '请选择出生日期',
            type: 'error'
          })
          return false;
        } else {
          if (this.birthDate.length < 15) {
            this.birthDate = this.birthDate + " 00:00:00";
          }
        }
        if (this.sex == "") {
          this.$message({
            message: '请选择性别',
            type: 'error'
          })
          return false;
        }
        if (this.height == "") {
          this.$message({
            message: '请输入身高',
            type: 'error'
          })
          return false;
        }
        if (this.school == "") {
          this.$message({
            message: '学校不能为空',
            type: 'error'
          })
          return false;
        }
        if (this.education == "" || this.education == -1) {
          this.$message({
            message: '请选择最高学历',
            type: 'error'
          })
          return false;
        }

        if (this.jobMes == "") {
          this.$message({
            message: '请输入职业',
            type: 'error'
          })
          return false;
        }
        if (this.salarys == "" || this.salarys == -1) {
          this.$message({
            message: '请选择年薪',
            type: 'error'
          })
          return false;
        }


        if (this.idealFriend == "") {
          this.$message({
            message: '请输入择偶要求',
            type: 'error'
          })
          return false;
        }
        if (this.selfescription == "") {
          this.$message({
            message: '请输入关于我',
            type: 'error'
          })
          return false;
        }
        var personalPhoto = [];
        console.log(this.personalPhoto)
        this.personalPhoto.map((v) => {
          personalPhoto.push(v.url)
        })
        if (this.editUserId == '') {
          var data = {
            "selfDescription": this.selfDescription,
            "salarys": this.salarys,
            "birthDate": this.birthDate,
            "education": this.education,
            "sex": this.sex,
            "height": this.height,
            "personalPhoto": personalPhoto,
            "fondTags": this.fondTags,
            "idealFriend": this.idealFriend,
            "nickName": this.nickName,
             "phone": this.phone,
            "jobMes": this.jobMes,
            "school": this.school,
            "status": this.status
          };
        } else {

          var data = {
            "selfDescription": this.selfDescription,
            "salarys": this.salarys,
            "birthDate": this.birthDate,
            "education": this.education,
            "sex": this.sex,
            "height": this.height,
            "id": this.editUserId,
            "personalPhoto": personalPhoto,
            "fondTags": this.fondTags,
            "idealFriend": this.idealFriend,
            "nickName": this.nickName,
             "phone": this.phone,
            "jobMes": this.jobMes,
            "school": this.school,
            "status": this.status
          };
        }
        userUpdate(data).then(response => {
          console.log(response)
          if (response.code == 200) {
            if (that.pageType == 1) {
              this.$message({
                message: '新增成功!',
                type: 'success'
              })
            } else {
              this.$message({
                message: '编辑成功!',
                type: 'success'
              })
            }
            that.$router.go(-1);
          } else {
            this.$message({
              message: response.msg,
              type: 'error'
            })
          }
          console.log(response)
        })

        // console.log(this.personalPhoto)
      },submitcheck() {
        var that = this;

        if (this.status == "") {
          this.$message({
            message: '请输入用户状态',
            type: 'error'
          })
          return false;
        }
        var data= {
           "id": this.editUserId,
           "status": this.status
        }
        userCheck(data).then(response => {
          console.log(response)
          if (response.code == 200) {
          this.$message({
            message: '审核成功!',
            type: 'success'
          })
            that.$router.go(-1);
          } else {
            this.$message({
              message: response.msg,
              type: 'error'
            })
          }
          console.log(response)
        })

        // console.log(this.personalPhoto)
      }

    }
  }
</script>

<style>
</style>
