<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="广告状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择广告状态" clearable>
          <el-option
            v-for="dict in dict.type.ad_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
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
          v-hasPermi="['maincode:advertisements:add']"
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
          v-hasPermi="['maincode:advertisements:edit']"
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
          v-hasPermi="['maincode:advertisements:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['maincode:advertisements:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="advertisementsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="广告标题" align="center" prop="title" />
      <el-table-column label="广告链接" align="center" prop="link" />
      <el-table-column label="上线时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}')}}</span>
        </template>
      </el-table-column>
      <el-table-column label="过期时间" align="center" prop="expirationTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expirationTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="点击次数" align="center" prop="clicks" />
      <el-table-column label="广告状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.ad_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="广告图片地址"  prop="imageUrl" >
        <template  slot-scope="scope" width="90">
          <!-- <img style="width:80px;height:80px;border:none;" :src="uploadImgUrl+scope.row.targetImg"> -->
          <ImagePreview style="width:80px;height:80px;border:none;" :src="scope.row.imageUrl" />
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
            v-hasPermi="['maincode:advertisements:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['maincode:advertisements:remove']"
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

    <!-- 添加或修改广告对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="广告标题" prop="title">
          <el-input v-model="form.title" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="广告内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="广告链接" prop="link">
          <el-input v-model="form.link" placeholder="请输入广告链接" />
        </el-form-item>
        <el-form-item label="上线时间" prop="startTime">
          <el-date-picker clearable
            v-model="form.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择上线时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="过期时间" prop="expirationTime">
          <el-date-picker clearable
            v-model="form.expirationTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择过期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="点击次数" prop="clicks">
          <el-input v-model="form.clicks" placeholder="请输入点击次数" />
        </el-form-item>
        <el-form-item label="广告状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择广告状态">
            <el-option
              v-for="dict in dict.type.ad_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="广告图片" prop="imageUrl">
          <image-upload :limit="1" v-model="form.imageUrl"/>
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
import { listAdvertisements, getAdvertisements, delAdvertisements, addAdvertisements, updateAdvertisements } from "@/api/maincode/advertisements";

export default {
  name: "Advertisements",
  dicts: ['ad_status'],
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
      // 广告表格数据
      advertisementsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        content: null,
        link: null,
        startTime: null,
        expirationTime: null,
        clicks: null,
        status: null,
        imageUrl: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "广告标题不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "广告内容不能为空", trigger: "blur" }
        ],
        link: [
          { required: true, message: "广告链接不能为空", trigger: "blur" }
        ],
        startTime: [
          { required: true, message: "上线时间不能为空", trigger: "blur" }
        ],
        expirationTime: [
          { required: true, message: "过期时间不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "广告状态不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询广告列表 */
    getList() {
      this.loading = true;
      listAdvertisements(this.queryParams).then(response => {
        this.advertisementsList = response.rows;
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
        adId: null,
        title: null,
        content: null,
        link: null,
        startTime: null,
        expirationTime: null,
        clicks: null,
        status: null,
        imageUrl: null,
        delFlag: null,
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
      this.ids = selection.map(item => item.adId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加广告";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const adId = row.adId || this.ids
      getAdvertisements(adId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改广告";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.adId != null) {
            updateAdvertisements(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAdvertisements(this.form).then(response => {
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
      const adIds = row.adId || this.ids;
      this.$modal.confirm('是否确认删除广告编号为"' + adIds + '"的数据项？').then(function() {
        return delAdvertisements(adIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('maincode/advertisements/export', {
        ...this.queryParams
      }, `advertisements_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
