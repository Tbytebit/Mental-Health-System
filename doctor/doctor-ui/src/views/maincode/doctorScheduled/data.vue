<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
<!--      <el-form-item label="排班id" prop="scheduledId">-->
<!--        <el-input-->
<!--          v-model="queryParams.scheduledId"-->
<!--          placeholder="请输入排班id"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="医生id" prop="doctorId">-->
<!--        <el-input-->
<!--          v-model="queryParams.doctorId"-->
<!--          placeholder="请输入医生id"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item label="排班时间" prop="time">
        <el-date-picker clearable
          v-model="queryParams.time"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择排班时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="可预约数" prop="number">
        <el-input
          v-model="queryParams.number"
          placeholder="请输入可预约数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="剩余号数" prop="remainder">
        <el-input
          v-model="queryParams.remainder"
          placeholder="请输入剩余号数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.scheduled_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
<!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['maincode:doctorScheduled:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['maincode:doctorScheduled:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['maincode:doctorScheduled:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['maincode:doctorScheduled:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="doctorScheduledList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="医生" align="center" prop="doctorName" />
      <el-table-column label="排班时间" align="center" prop="time" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.time, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="可预约数" align="center" prop="number" />
      <el-table-column label="剩余号数" align="center" prop="remainder" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.scheduled_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['maincode:doctorScheduled:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['maincode:doctorScheduled:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改医生预约排版对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
<!--        <el-form-item label="排班id" prop="scheduledId">-->
<!--          <el-input v-model="form.scheduledId" placeholder="请输入排班id" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="医生id" prop="doctorId">-->
<!--          <el-input v-model="form.doctorId" placeholder="请输入医生id" />-->
<!--        </el-form-item>-->
        <el-form-item label="排班时间" prop="time" >
          <el-date-picker clearable disabled
            v-model="form.time"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择排班时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
             type="datetime"
             value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择开始时间">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="过期时间" prop="datetime">
          <el-date-picker clearable
                          v-model="form.endTime"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="请选择结束时间">
          </el-date-picker>
        </el-form-item>

<!--        -->
<!--        <el-form-item label="结束时间" prop="endTime">-->
<!--          <el-date-picker clearable-->
<!--            v-model="form.endTime"-->
<!--            type="datetime"-->
<!--            value-format="yyyy-MM-dd HH:mm:ss"-->
<!--            placeholder="请选择结束时间">-->
<!--          </el-date-picker>-->
<!--        </el-form-item>-->
        <el-form-item label="可预约数" prop="number">
          <el-input v-model="form.number" placeholder="请输入可预约数" />
        </el-form-item>
        <el-form-item label="剩余号数" prop="remainder">
          <el-input v-model="form.remainder" placeholder="请输入剩余号数" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="dict in dict.type.scheduled_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDoctorScheduled, getDoctorScheduled, delDoctorScheduled, addDoctorScheduled, updateDoctorScheduled } from "@/api/maincode/doctorScheduled";
import {getScheduled} from "@/api/maincode/scheduled";

export default {
  name: "DoctorScheduled",
  dicts: ['scheduled_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 医生预约排版表格数据
      doctorScheduledList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //获取的id
      scheduledId: '',
      // 今天
      time:null,
      // 医生id
      doctorId:"",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        scheduledId: null,
        doctorId: null,
        time: null,
        startTime: null,
        endTime: null,
        number: null,
        remainder: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        scheduledId: [
          { required: true, message: "排班id不能为空", trigger: "blur" }
        ],
        doctorId: [
          { required: true, message: "医生id不能为空", trigger: "blur" }
        ],
        time: [
          { required: true, message: "排班时间不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "开始不能为空", trigger: "blur" }
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" }
        ],
        number: [
          { required: true, message: "可预约数不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "blur" }
        ],

      }
    };
  },
  // created() {
  //   this.getList();
  // },
  created() {
    const scheduledId = this.$route.params && this.$route.params.scheduledId;
    const doctorId = this.$route.params && this.$route.params.doctorId;

    console.log(this.$route.params && this.$route.params.time)

    this.doctorId = doctorId
    this.scheduledId = scheduledId
    this.queryParams.scheduledId = scheduledId

    getScheduled(scheduledId).then(response => {
      this.time = response.data.time
      this.queryParams.time = this.time

      this.getList()
    });

  },
  methods: {
    /** 查询医生预约排版列表 */
    getList() {
      this.loading = true;
      listDoctorScheduled(this.queryParams).then(response => {
        this.doctorScheduledList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        scheduledId: null,
        doctorId: null,
        time: null,
        startTime: null,
        endTime: null,
        number: null,
        remainder: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.form.scheduledId = this.scheduledId
      this.form.doctorId = this.doctorId
      this.form.time = this.time
      this.title = "添加医生预约排版";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDoctorScheduled(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改医生预约排版";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDoctorScheduled(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDoctorScheduled(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除医生预约排版编号为"' + ids + '"的数据项？').then(function() {
        return delDoctorScheduled(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('maincode/doctorScheduled/export', {
        ...this.queryParams
      }, `doctorScheduled_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
