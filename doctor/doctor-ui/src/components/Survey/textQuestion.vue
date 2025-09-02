<template>
  <div class="text-question">
    <el-form-item :label="question.questionTitle" prop="question">
      <el-input v-model="textInput" :placeholder="question.remark" clearable :style="{ width: '100%' }"></el-input>
    </el-form-item>
  </div>
</template>

<script>
export default {
  name: 'TextQuestion',
  props: {
    question: {
      type: Object,
      default: () => ({
        remark: '',
        questionId: '',
        questionnaireId: '',
        questionType: '',
        questionTitle: '',
        questionOrder: '',
        required: ''
      })
    }
  },
  data() {
    return {
      textInput: ''
    }
  },
  watch: {
    textInput(val) {
      this.$emit('update:textInput', val)
    }
  }
}
</script>

<style scoped>
.text-question {
  margin-bottom: 10px;
}
</style>
  