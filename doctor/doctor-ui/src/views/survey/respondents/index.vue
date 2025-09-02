<template>
    <div>
        <div class="mobile-dialog">
            <el-dialog :visible="true" title="Dialog Title">
                <el-form>
                    <div v-for="(question, index) in questionList" :key="index" class="mobile-question">
                        <component 
                        :is="getComponentName(question.questionType)" 
                        :question="question"
                        :text-input.sync="question.answer"
                        :textarea-input.sync="question.answer"
                        :selectedOption.sync="question.answer"
                        :selectedOptions.sync="question.answer"></component>
                    </div>
                </el-form>

                <div slot="footer" class="dialog-footer">
                    <el-button type="success" round @click="submit">提交</el-button>
                    <el-button type="primary" round @click="redirectToQuestionnaire">退出</el-button>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>

import { answerQuestionnaire } from '@/api/survey/questionnaire'
import {  listQuestion, answerQuestion } from '@/api/survey/question'
import {  listOption } from '@/api/survey/option'
import TextQuestion from '@/components/Survey/textQuestion'
import TextAreaQuestion from '@/components/Survey/textAreaQuestion'
import RadioQuestion from '@/components/Survey/radioQuestion'
import CheckboxQuestion from '@/components/Survey/checkboxQuestion'

let questionnaireId

export default {
    components: {
        TextQuestion,
        TextAreaQuestion,
        RadioQuestion,
        CheckboxQuestion
    },
    data() {
        return {
            questionList: [],
            answerList: [],
        }
    },
    created() {
        questionnaireId = this.$route.params.questionnaireId;
        this.queryQuestionList(questionnaireId);
    },
    watch: {
    },
    mounted() {
        this.questionList.forEach(question => {
            this.addAnswerToQuestion(question);
        });
    },
    methods: {
        addAnswerToQuestion(question) {
            this.$set(question, 'answer', '');
        },
        queryQuestionList(questionnaireId) {
            listQuestion(questionnaireId).then(async response => {
                for (const item of response) {
                    if (item.questionType === "radio" || item.questionType === "checkbox") {
                        const options = await listOption(item.questionId);
                        item.options = options;
                        this.questionList.push(item);
                    } else if (item.questionType === "input" || item.questionType === "textarea") {
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
        async submit() {
            const respondents = {
                questionnaireId: questionnaireId,
            };
            const { data: respondentsId } = await answerQuestionnaire(respondents);
            this.questionList.forEach((item, index) => {
                const answers =  {
                    answerType:item.questionType,
                    answerId: '',
                    respondentsId: respondentsId,
                    questionId: item.questionId,
                    answer: Array.isArray(item.answer) ? item.answer.join(';') : item.answer
                };
                answerQuestion(answers).then(response =>{
                    this.$modal.msgSuccess("新增成功");
                });
                this.redirectToQuestionnaire();
            });

        }
        ,
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

