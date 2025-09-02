<template>
    <div class="textArea-question">
      <el-form-item :label="question.questionTitle" prop="question">
        <el-input type="textarea" v-model="textareaInput" :placeholder="question.remark" clearable :style="{width: '100%'}"></el-input>
      </el-form-item>
    </div>
  </template>
  
  <script>
  export default {
    name: 'TextAreaQuestion',
    props: {
      question: {
        remark:'',
        questionId:'',
        questionnaireId:'',
        questionType:'',
        questionTitle:'',
        questionOrder:'',
        required:''
      }
    },
    data() {
      return {
        textareaInput: ''
      }
    },
    watch: {
    textareaInput(val) {
      this.$emit('update:textareaInput', val)
    }
  }
  }
  </script>
  
  <style scoped>
  .textArea-question {
    margin-bottom: 10px;
  }
  </style>
  