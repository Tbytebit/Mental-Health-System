<template>
    <div>
        <div class="mobile-dialog">
            <el-dialog :visible="true" title="Dialog Title">
                <el-form>
                    <div v-for="(question, index) in questionList" :key="index" class="mobile-question">
                        <component :is="getComponentName(question.questionType)" :question="question"></component>
                    </div>
                </el-form>

                <div slot="footer" class="dialog-footer">
                    <el-button type="success" round @click="publish">确认发布</el-button>
                    <el-button type="primary" round @click="redirectToQuestionnaire">退出</el-button>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>

import { publishQuestionnaire } from '@/api/survey/questionnaire'
import {  listQuestion } from '@/api/survey/question'
import {  listOption } from '@/api/survey/option'
import TextQuestion from '@/components/Survey/textQuestion'
import TextAreaQuestion from '@/components/Survey/textAreaQuestion'
import RadioQuestion from '@/components/Survey/radioQuestion'
import CheckboxQuestion from '@/components/Survey/checkboxQuestion'

let questionnaireId

export default {
    components:{
        TextQuestion,
        TextAreaQuestion,
        RadioQuestion,
        CheckboxQuestion
    },
    data(){
        return{
            questionList: [],
        }
    },
    created(){
        questionnaireId=this.$route.params.questionnaireId;
        this.queryQuestionList(questionnaireId);
    },
    watch: {
    },
    mounted(){

    },
    methods: {
        queryQuestionList(questionnaireId) {
            listQuestion(questionnaireId).then(async response => {
                for (const item of response) {
                    if (item.questionType === "radio" || item.questionType === "checkbox") {
                        const options = await listOption(item.questionId);
                        item.options = options;
                        this.questionList.push(item);
                    } else if(item.questionType === "input" || item.questionType === "textarea"){
                        this.questionList.push(item);
                    }
                }
            }
            )
        },
        getComponentName(type) {
            switch (type) {
                case 'input':
                    return 'TextQuestion'
                case 'textarea':
                    return 'TextAreaQuestion'
                case 'radio':
                    return 'RadioQuestion'
                case 'checkbox':
                    return 'CheckboxQuestion'
                default:
                    return ''
            }
        },
        publish(){
            publishQuestionnaire(questionnaireId);
            this.$modal.msgSuccess("发布成功");
            this.redirectToQuestionnaire();
        },
        redirectToQuestionnaire() {
            location.href = "/system/questionnaire";
        },
    }
}
</script>


<style scoped>
.mobile-dialog {
  max-width: 100%;
  padding: 10px;
}

.mobile-question {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .mobile-question {
    margin-bottom: 10px;
  }
}
</style>

