<template>
  <div class="app-container">
    <el-form ref="forma" label-position="left" :model="examineData" label-width="80px">
      <el-row>
        <el-form-item label="举报人：" class="el-col-8">
          {{examineData.userName==null?"已注销":examineData.userName}}
        </el-form-item>
        <el-form-item label-width="20" label="举报人手机号：" class="el-col-8">
          {{examineData.phone}}
        </el-form-item>
        <el-form-item label-width="20" label="被举报人：" class="el-col-8">
          {{examineData.complaintName==null?"已注销":examineData.complaintName}}
        </el-form-item>
        <el-form-item label-width="20" label="被举报人手机号：" class="el-col-8">
          {{examineData.complaintPhone}}
        </el-form-item>
        <el-form-item label-width="20" label="被举报人id：" class="el-col-8">
          {{examineData.complaintId==null?"已注销":examineData.complaintId}}
        </el-form-item>
        <el-form-item label-width="20" label="举报类型" class="el-col-8">
          {{examineData.type=='1'?"用户":(examineData.type=='2'?"动态":"评论")}}
        </el-form-item>
      </el-row>
      <el-row v-if="examineData.type=='2'">
        <el-form-item label-width="20" label="动态内容：" class="el-col-8">
          {{examineData.articleContent}}
        </el-form-item>
        <el-form-item label="动态图片">
          <el-image v-for="item in examineData.articleImg" :preview-src-list="examineData.articleImg"
            style="width: 80px; height: 80px;margin-right: 10px;border-radius: 10px;" :src="item" fit="fill"></el-image>
          <div v-if="!examineData.articleImg">无</div>
        </el-form-item>
      </el-row>
      <el-row v-if="examineData.type=='3'">
        <el-form-item label-width="20" label="评论内容：" class="el-col-8">
          {{examineData.comment}}
        </el-form-item>
      </el-row>

      <el-row>
        <el-form-item label-width="20" label="举报内容：" class="el-col-8">
          {{examineData.content}}
        </el-form-item>
      </el-row>
      <el-form-item label="证明材料">
        <el-image v-for="item in examineData.img" :preview-src-list="examineData.img"
          style="width: 80px; height: 80px;margin-right: 10px;border-radius: 10px;" :src="item" fit="fill"></el-image>
        <div v-if="!examineData.img">无</div>
      </el-form-item>
      <el-row>
        <el-form-item label-width="20" label="举报时间：" class="el-col-8">
          {{examineData.createTime}}
        </el-form-item>
        <el-form-item label-width="20" label="处理时间：" class="el-col-8">
          {{examineData.modifyTime}}
        </el-form-item>
        <el-form-item label="处理状态" label-width="auto" class="el-col-8">

          <el-tag v-if="examineData.status=='1'">待处理</el-tag>
          <el-tag v-if="examineData.photoAuth=='2'">已处理</el-tag>
        </el-form-item>
      </el-row>

      <el-form-item>
        <el-button type="primary" size="medium" @click="submit()">已处理</el-button>
        <el-button size="medium" @click="closeExamine()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import {
    getComplaintInfo,postComplaint
  } from '@/api/table'
  import {
    sysconfig
  } from '@/api/system'
  import {
    MessageBox,
    Message
  } from 'element-ui'
  export default {
    filters: {},
    data() {
      return {
        pageType: '',
        // 用户资料详情
        examineData: {},
        // 审核理由
        examineRemarks: [],
        remarksVal: "",
        // 用户id
        dqUserId: ""
      }
    },
    created() {
      const id = this.$route.params && this.$route.params.id
      this.getuserInfoTy(id);
      this.$route.meta.title = "投诉详情"
      document.title = this.$route.meta.title;
      this.$route.matched[0].redirect = "/example/table"
    },
    methods: {
      // 用户详情
      getuserInfoTy(id) {
        var that = this;
        // 用户资料
        getComplaintInfo(id).then(response => {
          console.log(response.data)
          this.examineData = response.data;
        })

        var data = {
          "code": "examineRemarks"
        }
        sysconfig(data).then(response => {
          console.log(JSON.parse(response.data))
          this.examineRemarks = JSON.parse(response.data)
          // this.examineRemarks = response.data;
        })
      },

      // 提交审核
      submit() {
        var that = this;
        var data = {
          "id": this.examineData.id,
          "status":"ENABLE"
        }
        console.log(data);
        postComplaint(data).then(response => {
          if (response.code == 200) {
            Message({
              message: '处理成功！',
              type: 'success',
            })
            that.$router.go(-1);
          }
          console.log(response)
        })
      },
      // 审核理由
      remarksChang(value) {
        console.log(value);
        this.remarksVal = value;
        this.examineData.remarks = value;
      },
      // 返回上一页
      closeExamine() {
        this.$router.go(-1);
      }
    }
  }
</script>
