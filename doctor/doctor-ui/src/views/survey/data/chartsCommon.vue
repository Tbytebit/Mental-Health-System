<template>
    <div style="width: 100%;" >
      <div style="font-size: 16px;font-weight: bold;">
        <span>{{ index+1 }}、</span>
        <span v-html="question.questionTitle" ></span>
        <span>【{{ question.quTypeName }}】</span>
      </div>
      <div v-if="question.questionType === 'input' || question.questionType === 'textarea'" style="padding: 30px;">
        填写回答：{{ question.recordCount }} 份
      </div>
      <div v-if="question.questionType !== 'input' && question.questionType !== 'textarea'">
        <el-table
          :data="question.surveyDataOptionList"
          style="width: 100%">
          <el-table-column
            prop="optionText"
            label="题目选项">
          </el-table-column>
          <el-table-column
            v-if="question.quType === 'radio' || question.quType === 'checkbox' "
            label="频次"
            width="130"
            align="center">
            <template slot-scope="scope">{{ scope.row.anCount }} 次</template>
          </el-table-column>
        </el-table>
        <div>
          <el-tabs v-model="activeName" style="width: 100%;" @tab-click="handleClick" >
            <el-tab-pane label="柱状图" name="bar">
              <div class="surveyMain" style="width: 100%;height:400px;" ></div>
            </el-tab-pane>
            <el-tab-pane label="拆线图" name="line">
              <div class="surveyMain" style="width: 100%;height:400px;" ></div>
            </el-tab-pane>
            <el-tab-pane label="拼状图" name="pie">
              <div class="surveyMain" style="width: 100%;height:400px;" ></div>
            </el-tab-pane>
            <el-tab-pane label="条形图" name="barY">
              <div class="surveyMain" style="width: 100%;height:400px;" ></div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  
  import * as echarts from 'echarts/core'
  import {GridComponent, TitleComponent, TooltipComponent, LegendComponent} from 'echarts/components'
  import {LineChart, BarChart, PieChart} from 'echarts/charts'
  import {UniversalTransition, LabelLayout} from 'echarts/features'
  import {CanvasRenderer} from 'echarts/renderers'
  
  echarts.use([GridComponent, LineChart, BarChart, CanvasRenderer, UniversalTransition, TitleComponent,
    TooltipComponent,
    LegendComponent,
    PieChart,
    LabelLayout])
  
  export default {
    name: 'chartsCommon',
    props: {
      question: {
        type: Object,
        default: () => []
      },
      index: {
        type: Number,
        default: 0
      }
    },
    data () {
      return {
        activeName: 'bar'
      }
    },
    mounted () {
      console.debug('this.question')
      console.debug(this.question)

      const questionObj = document.getElementById(this.question.questionId)
      const surveyMains = questionObj.getElementsByClassName('surveyMain')
      this.loadChart(surveyMains[0], 'bar')
    },
    methods: {
      handleClick: function (tab, event) {
        console.log(tab, event)
        console.debug(this.activeName)
        tab.$el.style.display = 'block'
        const surveyMain = tab.$el.getElementsByClassName('surveyMain')
        const curChartType = this.activeName
        // 构建option data, xAxisData
        this.loadChart(surveyMain[0], curChartType)
      },
      loadChart: function (chartDom, type) {
        const myChart = echarts.init(chartDom)
        const option = this.buildOption(this.question, type)
        option && myChart.setOption(option)
      },
      buildOption: function (questionData, type) {
        const items = []
        const itemValues = []
        const itemNameValues = []
        const surveyDataOptionList = questionData.surveyDataOptionList
        for (let i=0; i< surveyDataOptionList.length; i++) {
          items.push(surveyDataOptionList[i].optionText)
          itemValues.push(surveyDataOptionList[i].anCount)
          itemNameValues.push({'value': surveyDataOptionList[i].anCount, 'name': surveyDataOptionList[i].optionText})
        }
        console.debug(items)
        let yAxisShow = true
        let option
        if (type === 'line' || type === 'bar') {
          option = {
            xAxis: {
              nameTextStyle: {
  
              },
              nameGap: 20,
              axisLabel: {
                rotate: -10,
                interval: 0
              },
              type: 'category',
              // data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
              data: items
            },
            yAxis: {
              type: 'value',
              show: yAxisShow
            },
            series: [
              {
                // data: [150, 230, 224, 218, 135, 147, 260],
                data: itemValues,
                type: type
              }
            ]
          }
        } else if (type === 'barY') {
          option = {
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
              }
            },
            legend: {},
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'value',
              boundaryGap: [0, 0.01],
              show: yAxisShow
            },
            yAxis: {
              type: 'category',
              // data: ['Brazil', 'Indonesia', 'USA', 'India', 'China', 'World']
              data: items
            },
            series: [
              {
                type: 'bar',
                // data: [18203, 23489, 29034, 104970, 131744, 630230]
                data: itemValues
              }
            ]
          }
        } else if (type === 'pie') {
          option = {
            tooltip: {
              trigger: 'item'
            },
            legend: {
              orient: 'vertical',
              left: 'left'
            },
            series: [
              {
                name: 'Access From',
                type: 'pie',
                radius: '50%',
                data: itemNameValues,
                emphasis: {
                  itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                }
              }
            ]
          }
        }
        return option
      }
    }
  }
  
  </script>
  
  <style scoped>
  
  </style>
  