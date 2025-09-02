<template>
  <view class="debug-container">
    <view class="section">
      <view class="title">帖子调试工具</view>
      <view class="form-item">
        <text class="label">帖子ID:</text>
        <input type="number" v-model="postId" placeholder="输入帖子ID" class="input" />
        <button @click="fetchPost" class="button">查询</button>
      </view>
    </view>

    <view class="section" v-if="loading">
      <view class="loading">加载中...</view>
    </view>

    <view class="section" v-else-if="error">
      <view class="error">{{ error }}</view>
    </view>

    <view class="section" v-else-if="post">
      <view class="title">帖子信息</view>
      <view class="post-info">
        <view class="info-item">
          <text class="info-label">ID:</text>
          <text class="info-value">{{ post.id }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">标题:</text>
          <text class="info-value">{{ post.title }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">内容:</text>
          <text class="info-value">{{ post.content }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">用户ID:</text>
          <text class="info-value">{{ post.userId }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">用户名:</text>
          <text class="info-value">{{ post.username }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">分类ID:</text>
          <text class="info-value">{{ post.categoryId }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">分类名称:</text>
          <text class="info-value">{{ post.categoryName }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">创建时间:</text>
          <text class="info-value">{{ post.createTime }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">更新时间:</text>
          <text class="info-value">{{ post.updateTime }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">是否删除:</text>
          <text class="info-value">{{ post.isDeleted ? '是' : '否' }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <view class="title">列表查询参数</view>
      <view class="form-item">
        <text class="label">分类ID:</text>
        <input type="text" v-model="categoryId" placeholder="分类ID" class="input" />
      </view>
      <view class="form-item">
        <text class="label">页码:</text>
        <input type="number" v-model="pageNum" placeholder="页码" class="input" />
      </view>
      <view class="form-item">
        <text class="label">每页大小:</text>
        <input type="number" v-model="pageSize" placeholder="每页大小" class="input" />
      </view>
      <button @click="fetchPostList" class="button full">查询帖子列表</button>
    </view>

    <view class="section" v-if="postList.length > 0">
      <view class="title">列表结果 ({{ postList.length }}条)</view>
      <view class="post-item" v-for="(item, index) in postList" :key="index">
        ID: {{ item.id }} - {{ item.title }} - 分类: {{ item.categoryId }}
      </view>
    </view>
  </view>
</template>

<script>
import { getPostById, getForumPostList } from '@/api/forum/forum.js';

export default {
  data() {
    return {
      postId: '',
      post: null,
      loading: false,
      error: null,
      categoryId: 'anxiety',
      pageNum: 1,
      pageSize: 10,
      postList: []
    };
  },
  methods: {
    fetchPost() {
      if (!this.postId) {
        this.error = '请输入帖子ID';
        return;
      }

      this.loading = true;
      this.error = null;
      this.post = null;

      getPostById(this.postId)
        .then(response => {
          console.log('帖子查询结果:', response);
          if (response.data) {
            this.post = response.data;
          } else {
            this.error = '未找到该帖子';
          }
        })
        .catch(error => {
          console.error('查询帖子失败', error);
          this.error = '查询失败: ' + (error.message || '未知错误');
        })
        .finally(() => {
          this.loading = false;
        });
    },
    fetchPostList() {
      this.loading = true;
      this.postList = [];

      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize
      };

      if (this.categoryId && this.categoryId !== 'all') {
        params.categoryId = this.categoryId;
      }

      console.log('查询列表参数:', params);

      getForumPostList(params)
        .then(response => {
          console.log('列表查询结果:', response);
          
          let data = response.data;
          if (Array.isArray(data)) {
            this.postList = data;
          } else if (data && data.rows && Array.isArray(data.rows)) {
            this.postList = data.rows;
          } else {
            this.error = '返回数据格式有误';
          }
        })
        .catch(error => {
          console.error('查询列表失败', error);
          this.error = '查询失败: ' + (error.message || '未知错误');
        })
        .finally(() => {
          this.loading = false;
        });
    }
  }
};
</script>

<style>
.debug-container {
  padding: 30rpx;
}

.section {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.label {
  width: 150rpx;
  font-size: 28rpx;
}

.input {
  flex: 1;
  height: 70rpx;
  border: 1px solid #ddd;
  border-radius: 6rpx;
  padding: 0 20rpx;
}

.button {
  width: 150rpx;
  height: 70rpx;
  line-height: 70rpx;
  text-align: center;
  background-color: #007AFF;
  color: #fff;
  border-radius: 6rpx;
  margin-left: 20rpx;
  font-size: 26rpx;
}

.button.full {
  width: 100%;
  margin-left: 0;
  margin-top: 20rpx;
}

.loading {
  text-align: center;
  padding: 20rpx;
  color: #666;
}

.error {
  text-align: center;
  padding: 20rpx;
  color: #ff4d4f;
}

.info-item {
  display: flex;
  margin-bottom: 10rpx;
}

.info-label {
  width: 150rpx;
  color: #666;
  font-size: 28rpx;
}

.info-value {
  flex: 1;
  font-size: 28rpx;
}

.post-item {
  padding: 20rpx 0;
  border-bottom: 1px solid #eee;
  font-size: 28rpx;
}
</style> 