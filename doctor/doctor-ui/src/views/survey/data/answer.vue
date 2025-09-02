<template>
  <div>
    <DataWrapper :is-answer-data="true">
      <template v-slot:slot="{ questionnaire }">
        <el-row type="flex">
          <el-col :span="18">
            <div style="font-size: 14px;padding: 10px;"><strong>原始数据列表</strong></div>
          </el-col>
        </el-row>
        <el-table :data="respondentsList" stripe style="width: 100%">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column label="用户名">
            <template slot-scope="scope">
              <div slot="reference" class="name-wrapper">
                <div v-html="scope.row.userName"></div>
              </div>
            </template>
          </el-table-column>

          <!--
              <el-table-column label="回答的题数" >
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.completeItemNum != null ? scope.row.completeItemNum : 0 }}&nbsp;题</span>
                </template>
              </el-table-column>
              -->

          <el-table-column label="回答时间">
            <template slot-scope="scope">
              <i class="el-icon-time"></i>
              <span style="margin-left: 10px">{{ scope.row.submitTime }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="160">
            <template slot-scope="scope">
              <el-button-group>
                <el-tooltip effect="dark" content="查看数据" placement="top">
                  <el-button size="mini" icon="el-icon-view" @click="handInfo(scope.row)"></el-button>
                </el-tooltip>
                <el-tooltip effect="dark" content="删除数据" placement="top">
                  <el-button size="mini" icon="el-icon-delete" @click="handleDelete(scope.row)"></el-button>
                </el-tooltip>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
        <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
      </template>
    </DataWrapper>
  </div>
</template>


<script>
import DataWrapper from "@/components/Survey/dataWapper";
import { listRespondents, delRespondents } from "@/api/survey/respondents";

export default {
  name: "answer",
  components: {
    DataWrapper,
  },
  data() {
    return {
      respondentsList: [],
      total:0,
      // 查询参数
      queryParams: {
        questionnaireId:'',
        pageNum: 1,
        pageSize: 10,
      },
      questionnaireId:'',
    }
  },
  created() {
    this.questionnaireId = this.$route.params.questionnaireId;
    this.queryParams.questionnaireId = this.questionnaireId;
    this.getList();
  },
  methods:{
    getList(){
      listRespondents(this.queryParams).then(response =>{
        this.respondentsList = response.rows;
        this.total = response.total;
      })
    },
    handInfo(row){
      const respondentsId = row.respondentsId;
      const url = "/survey/respondents/Info/" + respondentsId;
      this.$router.push(url);
    },
    handleDelete(row){
      delRespondents(row.respondentsId);
    }
  }
} 
</script>

<style></style>