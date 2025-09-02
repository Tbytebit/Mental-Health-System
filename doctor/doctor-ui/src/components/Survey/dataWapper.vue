<template>
    <div>
        <el-row>
            <el-col :span="20" :offset="2">
                <div class="main">
                    <div class="survey-title">
                        <el-row type="flex" justify="space-between" align="msiddle">
                            <el-col>
                                <div class="survey-title-content">
                                    <div v-text="questionnaire.questionnaireName"></div>
                                </div>
                            </el-col>
                            <el-col :span="4">
                                <el-select :value="questionnaire.stauts" v-model="questionnaire.stauts" placeholder="请选择"
                                    @change="changeStatus">
                                    <el-option key="0" :value="0" label="设计中"></el-option>
                                    <el-option key="1" :value="1" label="发布收集"></el-option>
                                    <el-option key="2" :value="2" label="收集结束"></el-option>
                                </el-select>
                            </el-col>
                        </el-row>
                    </div>
                    <div class="survey-step">
                        <div class="survey-step-item" style="padding-left: 16px;">
                            <el-row v-show="isSurveyChart || isAnswerData">
                                <el-col :span="3">
                                    <router-link :to="`/survey/data/charts/${questionnaireId}`" :class="{ 'link-primary': isSurveyChart }" class="link">
                                        <i class="el-icon-discount"></i>默认统计</router-link>
                                </el-col>
                                <el-col :span="3">
                                    <router-link :to="`/survey/data/answer/${questionnaireId}`" :class="{ 'link-primary': isAnswerData }" class="link">
                                        <i class="el-icon-receiving"></i>原始数据</router-link>
                                </el-col>
                                <el-col :span="3"></el-col>
                                <el-col :span="3"></el-col>
                            </el-row>
                        </div>
                        <div class="survey-step-item survey-step-item-status">
                            <el-row type="flex" justify="space-between" align="msiddle">
                                <el-col :span="4">
                                    <div>状态：
                                        <el-tag v-if="questionnaire.status == 0" size="mini">设计中</el-tag>
                                        <el-tag v-else-if="questionnaire.status == 1" type="success" size="mini">收集中</el-tag>
                                        <el-tag v-else-if="questionnaire.status == 2" type="info" size="mini">收集结束</el-tag>
                                        <el-tag v-else disable-transitions style="margin-left: 10px" size="mini">未知</el-tag>
                                    </div>
                                </el-col>
                                <el-col :span="4">
                                    <div>收集数：{{ questionnaire.participantCount != null ? questionnaire.participantCount : 0 }} 份</div>
                                </el-col>
                                <el-col :span="16" style="text-align: right;">
                                    创建时间：{{ questionnaire.createTime }}
                                </el-col>
                            </el-row>
                        </div>
                        <div class="survey-step-main">
                            <slot :quesionnaire="questionnaire" name="slot"></slot>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>
  
<script>
import { getQuestionnaire, updateStatus } from '@/api/survey/questionnaire'

export default {
    name: 'DataWrapper',
    props: {
        id: { type: String, default: '' },
        isSurveyChart: { type: Boolean, default: false },
        isAnswerData: { type: Boolean, default: false },
        isSurveyLog: { type: Boolean, default: false },
        isAnswerLog: { type: Boolean, default: false }
    },
    data() {
        return {
            questionnaire: {
                questionnaireId: '',
                questionnaireName: '',
                createTime: '',
                questionCount: '',
                participantCount: '',
                status: ''
            },
            questionnaireId: '',
        }
    },
    created() {
        this.questionnaireId = this.$route.params.questionnaireId;
        
    },
    mounted() {
        this.getQuestionnaireInfo();
    },
    methods: {
        changeStatus() {
            console.debug(this.questionnaire.stauts)
            updateStatus({questionnaireId:this.questionnaireId, status:this.questionnaire.stauts}).then((response) => {
                if (response.code === 200) {
                    this.$message.success('问卷状态设置成功')
                } else {
                    this.$message.error('问卷状态设置失败')
                }
            })
        },
        getQuestionnaireInfo() {
            getQuestionnaire(this.questionnaireId).then((response) => {
                this.questionnaire = response.data;
            })
        }
    }
}

</script>
<style scoped>
.main {
    background-color: white;
    padding: 20px;
}

.survey-title {
    border-bottom: 1px solsid rgb(241, 242, 245);
    padding-bottom: 20px;
    padding-left: 10px;
}

.survey-title-content {
    font-size: 26px;
    font-weight: 300;
}

.survey-step {
    padding: 0px;
}

.survey-step-item {
    padding: 20px 10px;
    border-bottom: 1px solsid rgb(241, 242, 245);
}

.link {
    text-decoration: none;
    color: #606266;
    font-size: 14px;
}

.link-1 {
    font-size: 14px;
}

.link-primary,
.dw-link:hover {
    color: #409eff;
    font-weight: bold;
}

.link i {
    margin-right: 6px;
}

.survey-step-main {
    padding: 20px 10px;
}

.survey-step-item-status {
    background-color: rgb(241, 242, 245);
    font-size: 14px;
    padding: 10px;
}
</style>
  