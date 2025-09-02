<template>
    <div>
        <el-page-header content="答卷详情" class="page-header" @back="goBack"></el-page-header>
        <div class="answer-content">
            <el-row type="flex" class="row-bg">
                <el-col :span="16" :push="4">
                    <div style="background: white;">
                        <el-descriptions :column="3" border>
                            <el-descriptions-item>
                                <template slot="label">
                                    <i class="el-icon-location-information"></i> 答卷用户名
                                </template>
                                {{ survey.userName }}
                            </el-descriptions-item>
                            <el-descriptions-item>
                                <template slot="label">
                                    <i class="el-icon-timer"></i>
                                    答卷时间
                                </template>
                                {{ survey.submitTime }}
                            </el-descriptions-item>
                        
                            <el-descriptions-item>
                                <template slot="label">
                                    <i class="el-icon-price-tag"></i> 答卷ID
                                </template>
                                {{ survey.respondentsId }}
                            </el-descriptions-item>
                        </el-descriptions>
                        <div class="answer-title" style="padding-top: 30px;padding-bottom: 0px;">答卷结果信息</div>
                        <div style="padding: 0px 20px 30px 20px;">
                            <answer-common v-for="(item, index) in survey.surveyInfoQuestionList" :key="item.id"
                                :id="item.questionId" :index="index" :question="item"></answer-common>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>
<script>

import DataWrapper from "@/components/Survey/dataWapper";
import answerCommon from './answerCommon';
import {getSurvey} from "@/api/survey/respondents";

export default {
    name: 'SurveyAnswerInfo',
    components: {
        DataWrapper,
        answerCommon,
    },
    data() {
        return {
            survey:[],
            respondentsId:'',
        }
    },
    mounted() {
    },
    created(){
        this.respondentsId  = this.$route.params.respondentsId;
        this.getList();
    },
    methods: {
        goBack() {
            this.$router.back()
        },
        getList() {
            getSurvey(this.respondentsId).then(response => {
                this.survey = response.data;
                this.survey.surveyInfoQuestionList.forEach(question => {
                    if (question.questionType === 'checkbox') {
                        question.answers = question.answers.split(';');
                    }
                });
            });
        },
    }
}

</script>
<style scoped>
.page-header {
    padding: 20px;
    background-color: white;
}

.answer-title {
    font-size: 16px;
    padding: 10px;
}

.answer-content {
    padding: 20px;
}

.margin-top {
    margin-top: 20px;
}

.qu-item {
    margin-top: 10px;
}

.qu-item-title {
    padding: 20px 0px;
    color: grey;
}

.qu-item-body {
    margin-left: 10px;
}</style>
  