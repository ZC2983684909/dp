<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom: 15px;">
      <div style="height: 50px;">
        <div class="el-col-6">
          昵称：
          <el-input placeholder="请输入内容" v-model="nickName" clearable style="width: 70%;">
          </el-input>
        </div>
        <div class="el-col-6">
          实名状态：
          <el-select v-model="authStatus" style="width: 70%;" size="medium" placeholder="实名状态 " clearable
            class="filter-item ">
            <el-option label="全部" value="-1" />
            <el-option label="未认证" value="1" />
            <el-option label="认证中" value="2" />
            <el-option label="通过" value="3" />
            <el-option label="拒绝" value="4" />
          </el-select>
        </div>
        <div class="el-col-6">
          用户状态：
          <el-select v-model="userStatus" style="width: 70%;" size="medium" placeholder="用户资料状态 " clearable
            class="filter-item ">
            <el-option label="全部" value="-1" />
            <el-option label="启用" value="ENABLE" />
            <el-option label="禁用" value="DISABLE" />
            <el-option label="注销" value="LOGOFF" />
          </el-select>
        </div>
        <div class="el-col-6">
          用户类型：
          <el-select v-model="userType" style="width: 70%;" size="medium" placeholder="用户资料状态 " clearable
            class="filter-item ">
            <el-option label="全部" value="-1" />
            <el-option label="正常" value="1" />
            <el-option label="虚拟" value="2" />
          </el-select>
        </div>
      </div>
      <div style="height: 50px;">
        <div class="el-col-6">
          性别：
          <el-select v-model="userSex" style="width: 70%;" size="medium" placeholder="用户资料状态 " clearable
            class="filter-item ">
            <el-option label="全部" value="-1" />
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </div>
        <div class="el-col-6">
          活跃时间：
          <el-date-picker v-model="latelyTime" type="daterange" style="width: 70%;" value-format="yyyy-MM-dd HH:mm:ss"
            range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期">
          </el-date-picker>
        </div>
        <div class="el-col-6">
          注册时间：
          <el-date-picker v-model="createTime" type="daterange" style="width: 70%;" value-format="yyyy-MM-dd HH:mm:ss"
            range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期">
          </el-date-picker>
        </div>
        <div class="el-col-6">
          用户id：
          <el-input placeholder="请输入内容" v-model="userId" clearable style="width: 70%;">
          </el-input>
        </div>

      </div>
      <div style="height: 50px;">
        <div class="el-col-6">
          分享类型：
          <el-select v-model="shareType" style="width: 70%;" size="medium" placeholder="" clearable class="filter-item">
            <el-option label="全部" value="" />
            <el-option label="分享码" value="1" />
            <el-option label="分享人" value="2" />
          </el-select>
        </div>
        <div class="el-col-6">
          用户手机号：
          <el-input placeholder="请输入内容" v-model="phone" clearable style="width: 70%;">
          </el-input>
        </div>
        <div class="el-col-6">
          分享人手机号：
          <el-input placeholder="请输入内容" v-model="distributionPhone" clearable style="width: 70%;">
          </el-input>
        </div>
        <div class="el-col-6">
          分享人名称：
          <el-input placeholder="请输入内容" v-model="distributionName" clearable style="width: 70%;">
          </el-input>
        </div>

      </div>
      <div style="height: 50px;">
        <div class="el-col-10">
          分享人的分享人手机号：
          <el-input placeholder="请输入内容" v-model="distributionLastPhone" clearable style="width: 50%;">
          </el-input>
        </div>

        <div class="el-col-6">
          <el-button class=" " size="medium" type="primary" icon="el-icon-search" @click="searchData">
            搜索
          </el-button>
          <el-button class=" " size="medium" icon="" @click="resetting">
            重置
          </el-button>
        </div>
      </div>
      <div style="height: 50px;">
        <div class="el-col-6">
          <router-link :to="'/user/userInfo/0/1'">
            <el-button class="filter-item el-col-6" size="small" type="primary" icon="" @click="AddUser">
              新增
            </el-button>
          </router-link>
        </div>
      </div>

    </div>

    <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" fit highlight-current-row>
      <el-table-column label="昵称" width="150">
        <template slot-scope="scope">
          {{ scope.row.nickName }}
        </template>
      </el-table-column>
      <el-table-column label="头像" width="100px">
        <template slot-scope="scope">
          <el-image style="width: 50px; height: 50px;" class="el-avatar--circle" :src="scope.row.headPortrait"
            :preview-src-list="[scope.row.headPortrait]" fit="fill"></el-image>
        </template>
      </el-table-column>
      <el-table-column label="性别" width="90" align="center">
        <template slot-scope="scope">
          {{scope.row.gender}}
        </template>
      </el-table-column>
      <el-table-column label="城市" width="200px">
        <template slot-scope="scope">
          {{ scope.row.city==null?"未知":scope.row.city }}
        </template>
      </el-table-column>
      <el-table-column label="申请时间" width="220px">
        <template slot-scope="scope">
          <i class="el-icon-time" />
          {{ scope.row.createTime }}
        </template>
      </el-table-column>
      <el-table-column label="最近活跃时间" width="220px">
        <template slot-scope="scope">
          <i class="el-icon-time" />
          {{ scope.row.latelyTime }}
        </template>
      </el-table-column>
      <el-table-column label="实名认证" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="warning" v-if="scope.row.authStatus=='1'">未认证</el-tag>
          <el-tag type="warning" v-if="scope.row.authStatus=='2'">认证中</el-tag>
          <el-tag type="success" v-if="scope.row.authStatus=='3'">通过</el-tag>
          <el-tag type="danger" v-if="scope.row.authStatus=='4'">拒绝</el-tag>
          <el-tag type="danger" v-if="scope.row.authStatus=='5'">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学历认证" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="warning" v-if="scope.row.eduAuth=='1'">未认证</el-tag>
          <el-tag type="warning" v-if="scope.row.eduAuth=='2'">认证中</el-tag>
          <el-tag type="success" v-if="scope.row.eduAuth=='3'">通过</el-tag>
          <el-tag type="danger" v-if="scope.row.eduAuth=='4'">拒绝</el-tag>
          <el-tag type="danger" v-if="scope.row.eduAuth=='5'">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="相册认证" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="warning" v-if="scope.row.photoAuth=='1'">未认证</el-tag>
          <el-tag type="warning" v-if="scope.row.photoAuth=='2'">认证中</el-tag>
          <el-tag type="success" v-if="scope.row.photoAuth=='3'">通过</el-tag>
          <el-tag type="danger" v-if="scope.row.photoAuth=='4'">拒绝</el-tag>
          <el-tag type="danger" v-if="scope.row.photoAuth=='5'">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="用户手机号" width="220px">
        <template slot-scope="scope">
          {{ scope.row.phone }}
        </template>
      </el-table-column>
      <el-table-column label="分享类型" width="220px">
        <template slot-scope="scope">
          {{ scope.row.type=='1'?"分享码":"分享人" }}
        </template>
      </el-table-column>
      <el-table-column label="分享人手机号" width="220px">
        <template slot-scope="scope">
          {{ scope.row.distributionPhone}}
        </template>
      </el-table-column>
      <el-table-column label="分享人名称" width="220px">
        <template slot-scope="scope">
          {{ scope.row.distributionName}}
        </template>
      </el-table-column>
      <el-table-column label="分享人的分享人名称" width="220px">
        <template slot-scope="scope">
          {{ scope.row.distributionLastNickName}}
        </template>
      </el-table-column>
      <el-table-column label="分享人的分享人手机号" width="220px">
        <template slot-scope="scope">
          {{ scope.row.distributionLastPhone}}
        </template>
      </el-table-column>
      <el-table-column label="分享人数" width="220px">
        <template slot-scope="scope">
          {{ scope.row.distributionCount}}
        </template>
      </el-table-column>
      <el-table-column label="分享人的分享人数" width="220px">
        <template slot-scope="scope">
          {{ scope.row.nextDistributionCount}}
        </template>
      </el-table-column>
      <el-table-column label="分享人是否提现" width="220px">
        <template slot-scope="scope">
          {{ scope.row.payStatus=='0'?"否":(scope.row.payStatus=='1'?"是":"无")}}
        </template>
      </el-table-column>
      <el-table-column label="分享人的分享人是否提现" width="220px">
        <template slot-scope="scope">
          {{ scope.row.higherPayStatus=='0'?"否":(scope.row.higherPayStatus=='1'?"是":"无")}}
        </template>
      </el-table-column>
      <el-table-column label="提现金额" width="220px">
        <template slot-scope="scope">
          {{ scope.row.amount}}
        </template>
      </el-table-column>
      <el-table-column label="用户状态" width="100">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.status=='ENABLE'">启用</el-tag>
          <el-tag type="warning" v-if="scope.row.status=='DISABLE'">禁用</el-tag>
          <el-tag type="danger" v-if="scope.row.status=='LOGOFF'">注销</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="用户类型" width="100">
        <template slot-scope="scope">
          <el-tag type="danger" v-if="scope.row.userType=='2'">虚拟</el-tag>
          <el-tag type="success" v-if="scope.row.userType=='1'">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="280" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <router-link :to="'/user/userInfo/'+scope.row.id+'/2'">
            <el-button type="primary" size="small" icon="el-icon-edit">
              编辑
            </el-button>
          </router-link>
          <el-button type="danger" style="margin-left: 10px;" @click="showDiaLog(scope.row.id)" size="small"
            icon="el-icon-delete">
            删除
          </el-button>
          <el-button type="primary" style="margin-left: 10px;" @click="showWithdrawalDiaLog(scope.row.id)" size="small"
            icon="el-icon-money">
            提现
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="提示" :visible.sync="dialogVisible" width="30%" :before-close="handleClose">
      <span>确定要删除该用户吗</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDiaLog()">取 消</el-button>
        <el-button type="primary" @click="delUserId">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="提示" :visible.sync="withdrawaldialogVisible" width="30%" :before-close="handleWithClose">
      <span>确定为该用户提现吗</span>
      <el-form>
        <el-form-item label="金额" :label-width="formLabelWidth">
          <el-input v-model="amount" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeWithdrawalDiaLog()">取 消</el-button>
        <el-button type="primary" @click="userWithdrawal">确定</el-button>
      </span>
    </el-dialog>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currPage"
      :page-sizes="[10, 20, 30, 40]" :page-size="10" background layout="total, prev, pager, next" :total="totalCount"
      style="margin: 15px 0;float: right;">
    </el-pagination>
  </div>
</template>

<script>
  import {
    getUserList,
    deleteUser,
    userPay
  } from '@/api/user'
  import {
    sysconfig
  } from '@/api/system'
  import {
    MessageBox,
    Message
  } from 'element-ui'
  export default {
    filters: {
      statusFilter(status) {
        const statusMap = {
          ENABLE: 'success',
          DISABLE: 'gray',
          LOGOFF: 'danger'
        }
        return statusMap[status]
      }
    },
    data() {
      return {
        list: null,
        totalCount: 0,
        totalPage: 1,
        currPage: 1,
        pageSize: 10,
        listLoading: true,
        // 查询条件
        nickName: '', //昵称
        authStatus: '', //认证状态
        userStatus: "", //用户状态
        userType: '', //用户类型
        userSex: "", //用户性别
        latelyTime: "", //活跃时间
        createTime: "", //注册时间
        userId: "",
        shareType: "",
        phone: "",
        distributionPhone: "",
        distributionName: "",
        distributionLastPhone: "",
        // 删除弹框
        dialogVisible: false,
        withdrawaldialogVisible: false,
        deleteUserId: "", //删除id
        withdrUserId: "",
        amount: 100
      }
    },
    created() {
      this.fetchData()
    },
    methods: {
      handleClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            this.deleteUserId = '';
            done();
          })
          .catch(_ => {});
      },
      showDiaLog(id) {
        this.deleteUserId = id;
        this.dialogVisible = true;
      },
      closeDiaLog() {
        this.deleteUserId = '';
        this.dialogVisible = false;
      },
      handleWithClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            this.withdrUserId = '';
            done();
          })
          .catch(_ => {});
      },
      showWithdrawalDiaLog(id) {
        this.withdrUserId = id;
        this.withdrawaldialogVisible = true;
      },
      closeWithdrawalDiaLog() {
        this.withdrUserId = ''; /*  */
        this.withdrawaldialogVisible = false;
      },
      // 删除数据
      delUserId() {
        var id = this.deleteUserId;
        deleteUser(id).then(response => {
          console.log(response)
          if (response.code == 200) {
            this.$message({
              message: '删除成功!',
              type: 'success'
            })
            this.fetchData();
            this.closeDiaLog();
          }
        })
      },
      userWithdrawal() {
        var userPayData = {
          userId: this.withdrUserId,
          amount: this.amount
        }
        userPay(userPayData).then(response => {
          console.log(response)
          if (response.code == 200) {
            this.$message({
              message: '提现成功!',
              type: 'success'
            })
            this.fetchData();
            this.closeWithdrawalDiaLog();
          } else {
            this.$message({
              message: response.msg,
              type: 'error'
            })
          }
          console.log(response)
        })
      },
      // 跳转到新增用户
      AddUser() {

      },
      // 分页
      handleSizeChange(val) {
        this.pageSize = val;
        this.fetchData();
        console.log(`每页 ${val} 条`);
      },
      handleCurrentChange(val) {
        this.currPage = val;
        this.fetchData();
        console.log(`当前页: ${val}`);
      },
      // 点击搜索
      searchData() {
        this.currPage = 1;
        this.fetchData();
      },
      // 重置搜索
      resetting() {
        console.log(this.latelyTime);

        this.nickName = "";
        this.authStatus = "";
        this.userStatus = "";
        this.userType = "";
        this.userSex = "";
        this.latelyTime = "";
        this.createTime = "";
        this.userId = "";
        this.shareType = "";
        this.phone = "";
        this.distributionPhone = "";
        this.distributionName = "";
        this.distributionLastPhone = "";
      },
      // 加载数量
      fetchData() {
        this.listLoading = true;
        if (this.authStatus == -1) {
          var authStatus = '';
        } else {
          var authStatus = this.authStatus;
        }
        if (this.userType == -1) {
          var userType = '';
        } else {
          var userType = this.userType;
        }
        if (this.userStatus == -1) {
          var userStatus = '';
        } else {
          var userStatus = this.userStatus;
        }
        if (this.userSex == -1) {
          var userSex = '';
        } else {
          var userSex = this.userSex;
        }

        console.log(this.latelyTime);
        if (this.latelyTime == null) {
          this.latelyTime = ""
        }
        if (this.createTime == null) {
          this.createTime = ""
        }
        var data = {
          "pageIndex": this.currPage,
          "pageSize": this.pageSize,
          "authStatus": authStatus,
          "userType": userType,
          "status": userStatus,
          "gender": userSex,
          "nickName": this.nickName,
          "userId": this.userId,
          "latelyTimeStart": this.latelyTime[0],
          "latelyTimeEnd": this.latelyTime[1],
          "createTimeStart": this.createTime[0],
          "createTimeEnd": this.createTime[1],
          "shareType": this.shareType,
          "phone": this.phone,
          "distributionPhone": this.distributionPhone,
          "distributionName": this.distributionName,
          "distributionLastPhone": this.distributionLastPhone
        }
        getUserList(data).then(response => {
          console.log(response.data.rows)
          this.list = response.data.rows
          this.totalCount = response.data.totalCount
          this.totalPage = response.data.totalPage
          this.currPage = response.data.currPage
          this.pageSize = response.data.pageSize
          this.listLoading = false
        })
      }
    }
  }
</script>
