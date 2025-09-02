<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="问卷名称" prop="questionnaireName">
        <el-input
          v-model="queryParams.questionnaireName"
          placeholder="请输入调查问卷名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="问卷状态" prop="status" style="margin-left: 40px;">
          <el-select 
            v-model="queryParams.status" 
            placeholder="请选择问卷状态"
            clearable >
              <el-option
                v-for="dict in dict.type.survey_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                  >
              </el-option>
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
          v-hasPermi="['survey:questionnaire:add']"
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
          v-hasPermi="['survey:questionnaire:edit']"
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
          v-hasPermi="['survey:questionnaire:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['survey:questionnaire:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionnaireList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="questionnaireId" />
      <el-table-column label="问卷" align="center" prop="questionnaireName" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="问题数量" align="center" prop="questionCount" />
      <el-table-column label="答卷数" align="center" prop="participantCount" />
      <el-table-column label="状态" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" >设计中</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success" >收集中</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="info" >收集结束</el-tag>
          <el-tag v-else disable-transitions style="margin-left: 10px" >未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template slot-scope="scope">
          <el-button-group>
            <el-tooltip effect="dark" content="编辑问卷" placement="top">
              <el-button size="mini" content="编辑问卷" icon="el-icon-edit" 
              @click="handleUpdate(scope.row)" 
              v-hasPermi="['survey:questionnaire:edit']" ></el-button>
            </el-tooltip>

            <!--TODO-->
            <el-tooltip effect="dark" content="分配用户" placement="top">
              <el-button size="mini" icon="el-icon-share" 
              @click="handleAuthUser(scope.row)"
              v-hasPermi="['survey:questionnaire:asuser']"></el-button>
            </el-tooltip>
            <el-tooltip effect="dark" content="答卷数据" placement="top">
              <el-button size="mini" icon="el-icon-s-data" 
              @click="handleData(scope.row)"
              v-hasPermi="['survey:respondents:data']"></el-button>
            </el-tooltip>
            <el-tooltip effect="dark" content="预览问卷" placement="top">
              <el-button size="mini" icon="el-icon-view" 
              @click="preview(scope.row)"
              v-hasPermi="['survey:questionnaire:preview']"></el-button>
            </el-tooltip>
            <el-tooltip effect="dark" content="参与问卷" placement="top">
              <el-button size="mini" icon="el-icon-copy-document" 
              @click="handleAnswer(scope.row)"
              v-hasPermi="['survey:respondents:edit']"></el-button>
            </el-tooltip>
            <!---->

            <el-tooltip effect="dark" content="删除问卷" placement="top">
              <el-button size="mini" icon="el-icon-delete" 
              @click="handleDelete(scope.row)"
              v-hasPermi="['survey:questionnaire:remove']"></el-button>
            </el-tooltip>
          </el-button-group>
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

    <!-- 添加或修改问卷中心对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-position="top">
        <el-form-item label="调查问卷名称" prop="questionnaireName">
          <el-input v-model="form.questionnaireName" placeholder="请输入调查问卷名称" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary"  @click="designForm" style="float: left;" >设计问卷</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listQuestionnaire, getQuestionnaire, delQuestionnaire, addQuestionnaire, updateQuestionnaire } from "@/api/survey/questionnaire";

export default {
  name: "Questionnaire",
  dicts:['survey_status'],
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
      // 问卷中心表格数据
      questionnaireList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        questionnaireName: null,
        publishDate: null,
        questionCount: null,
        participantCount: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询问卷中心列表 */
    getList() {
      this.loading = true;
      listQuestionnaire(this.queryParams).then(response => {
        this.questionnaireList = response.rows;
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
        questionnaireId: null,
        questionnaireName: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
        publishDate: null,
        questionCount: null,
        participantCount: null,
        status: null
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
      this.ids = selection.map(item => item.questionnaireId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加问卷中心";
    },
    /**设计表单**/
    designForm(){
      if(this.form.questionnaireId != null){
      const url =  `/survey/design/${this.form.questionnaireId}`;
      this.$router.push(url);
      } else {
        alert("问卷未创建");
      }
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const questionnaireId = row.questionnaireId || this.ids
      getQuestionnaire(questionnaireId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改问卷中心";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.questionnaireId != null) {
            updateQuestionnaire(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addQuestionnaire(this.form).then(response => {
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
      const questionnaireIds = row.questionnaireId || this.ids;
      this.$modal.confirm('是否确认删除问卷中心编号为"' + questionnaireIds + '"的数据项？').then(function() {
        return delQuestionnaire(questionnaireIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('survey/questionnaire/export', {
        ...this.queryParams
      }, `questionnaire_${new Date().getTime()}.xlsx`)
    },
    handleAnswer(row){
      const questionnaireId = row.questionnaireId || this.ids;
      const url =  `/survey/answer/${questionnaireId}`;
      this.$router.push(url);
    },
    handleData(row){
      const questionnaireId = row.questionnaireId || this.ids;
      const url = `/survey/data/answer/${questionnaireId}`;
      this.$router.push(url);
    },
    preview(row){
      const questionnaireId = row.questionnaireId || this.ids;
      const url =  `/survey/preview/${questionnaireId}`;
      this.$router.push(url);
    },
    handleAuthUser(row){
      const questionnaireId = row.questionnaireId || this.ids;
      const url =  `/survey/asUser/${questionnaireId}`;
      this.$router.push(url);
    },
  }
};
</script>
