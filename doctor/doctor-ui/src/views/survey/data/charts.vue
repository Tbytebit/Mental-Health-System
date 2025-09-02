<template>
    <div>
      <DataWrapper :is-survey-chart="true">
        <template v-slot:slot="{ questionnaire }">
          <div v-loading="loading" >
            <charts-common v-for="(item,index) in questions" :key="item.questionId" :id="item.questionId" :index="index" :question="item" ></charts-common>
          </div>
        </template>
      </DataWrapper>
    </div>
  </template>
  <script>

import DataWrapper from "@/components/Survey/dataWapper";
import chartsCommon from './chartsCommon';
import {getReport} from "@/api/survey/questionnaire";
  
  export default {
    name: 'chart',
    components: {
        DataWrapper,
        chartsCommon,
    },
    data () {
      return {
        questionnaire: [],
        questions:[],
        loading: true,
        questionnaireId:'',
      }
    },
    created(){
      this.questionnaireId = this.$route.params.questionnaireId;
    },
    mounted () {
      this.surveyChartData()
    },
    methods: {
      surveyChartData: function () {
        getReport(this.questionnaireId).then((response) => {

          this.questionnaire = response.data
          this.questions = this.questionnaire.surveyDataQuestionList
          
          if (this.questions.length <= 0) {
            msgInfo('问卷还没有任何题目')
            this.loading = false
          }
          for (let i=0; i < this.questions.length; i++) {
            const questionData = this.questions[i]
            let count = questionData.anCount
            let quOptionsObj
            if (questionData.questionType === 'checkbox') {
              questionData.quTypeName = '多选题'
              quOptionsObj = questionData.quCheckboxs
            } else if (questionData.questionType === 'radio') {
              questionData.quTypeName = '单选题'
              quOptionsObj = questionData.quRadios
            } else if (questionData.questionType === 'input') {
              questionData.quTypeName = '填空题'
            } 
            const quStatOptions = []
            if (questionData.quType === 'radio' || questionData.quType === 'checkbox') {
              for (let j=0; j < quOptionsObj.length; j++) {
                const item = quOptionsObj[j]
                let quStatOption
                if (questionData.quType === 'radio' || questionData.quType === 'checkbox') {
                  const anCount = item.anCount
                  if (count === 0) {
                    count = 1
                  }
                  if (anCount === null) {
                    quOptionsObj[j].anCount = 0
                  }
                  const bfbFloat = anCount / count * 100
                  const percent = bfbFloat.toFixed(2)
                  quOptionsObj[j].percent = percent
                  quStatOption = {'optionName': item.optionName, 'anCount': item.anCount, 'percent': percent}
                }
                quStatOptions.push(quStatOption)
              }
            }
            this.questions[i].quStatOptions = quStatOptions
          }
          this.loading = false
        })
      }
    }
  }
  
  </script>
  