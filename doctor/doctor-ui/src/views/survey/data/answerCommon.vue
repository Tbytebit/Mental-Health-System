<template>
    <div>
        <el-form ref="form" :model="form" label-width="80px" label-position="left">
            <div class="qu-item">
                <div class="qu-item-title">
                    <span>{{ index + 1 }}、</span>
                    <span>【{{ question.questionTitle }}】</span>
                </div>
                <div class="qu-item-body">
                    <div v-if="question.questionType === 'radio'">
                        <div>
                            <el-radio-group v-model="question.answers" disabled>
                                <el-radio v-for="(item) in question.optionList" :key="item.optionId" :label="item.optionId" >{{
                                    item.optionText }}</el-radio>
                            </el-radio-group>
                        </div>
                    </div>
                    <div v-if="question.questionType === 'checkbox'">
                        <el-checkbox v-for="(item) in question.optionList" :key="item.optionId" :label="item.optionId"
                        :checked="question.answers.includes(item.optionId)" disabled>{{ item.optionText }}</el-checkbox>
                    </div>
                    <div v-if="question.questionType === 'input'">
                        <el-row><el-col :span="12"><el-input v-model="question.answers"
                                    readonly></el-input></el-col></el-row>
                    </div>
                    <div v-if="question.questionType === 'textarea'">
                        <el-row><el-col :span="12"><el-input v-model="question.answers"
                                    readonly></el-input></el-col></el-row>
                    </div>
                </div>
            </div>
        </el-form>
    </div>
</template>
<script>

export default {
    name: 'answerInfo',
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
    data() {
        return {
            form: {
                name: '',
                region: '',
                date1: '',
                date2: '',
                delivery: false,
                type: [],
                resource: '',
                desc: ''
            },
        }
    },
    mounted() {
    },
    methods: {
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
  