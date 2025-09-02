<template>
  <div class="checkbox-question">
      <el-form-item :label="question.questionTitle" prop="question" >
      <el-checkbox-group v-model="selectedOptions" clearable>
        <el-checkbox v-for="option in question.options" :label="option.optionId" :key="option.optionId" :style="{width: '100%'}">
          {{ option.optionText }}
        </el-checkbox>
      </el-checkbox-group>
    </el-form-item>
  </div>
</template>
  
<script>
export default {
  name: 'ChexkboxQuestion',
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
      selectedOptions: [],
    }
  },
  watch: {
    selectedOptions(val) {
      this.$emit('update:selectedOptions', val)
    }
  }
}

</script>
  
<style scoped>
.checkbox-question {
  margin-bottom: 10px;
}


</style>