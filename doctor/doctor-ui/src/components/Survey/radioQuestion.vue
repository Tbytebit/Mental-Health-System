<template>
  <div class="radio-question">
    <el-form-item :label="question.questionTitle" prop="question">
      <el-radio-group v-model="selectedOption" clearable :style="{width: '100%'}">
        <el-radio v-for="option in question.options" :label="option.optionId" :key="option.optionId">
          {{ option.optionText }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
  </div>
</template>
  
<script>
export default {
  name: 'RadioQuestion',
  props: {
    question: {
      remark: '',
      questionId: '',
      questionnaireId: '',
      questionType: '',
      questionTitle: '',
      questionOrder: '',
      options: [],
      required: ''
    }
  },
  data() {
    return {
      selectedOption: ''
    }
  },
  watch: {
    selectedOption(val) {
      this.$emit('update:selectedOption', val)
    }
  }
}
</script>
  
<style scoped>
.radio-question {
  margin-bottom: 10px;
}
</style>
  