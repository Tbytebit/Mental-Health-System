<template>
  <view class="category-container">
    <!-- 顶部导航栏 -->
    <view class="header">
      <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
      <view class="navbar">
        <view class="left-btn" @click="goBack">
          <uni-icons type="back" size="20" color="#fff"></uni-icons>
        </view>
        <view class="title">{{ pageTitle }}</view>
        <view class="right-btn">
          <uni-icons type="search" size="20" color="#fff" @click="goToSearch"></uni-icons>
        </view>
      </view>
      
      <!-- 分类选项卡 -->
      <scroll-view scroll-x class="category-tabs" show-scrollbar="false">
        <view class="tabs-list">
          <view 
            v-for="(item, index) in categories" 
            :key="index" 
            class="tab-item" 
            :class="{ active: currentCategory === item.id }"
            @click="selectCategory(item.id)"
          >
            <text class="tab-text">{{ item.name }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 内容区域 -->
    <scroll-view 
      scroll-y 
      class="content-area" 
      @scrolltolower="loadMore" 
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 文章列表 -->
      <view class="article-list" v-if="articleList.length > 0">
        <view class="article-item" v-for="(item, index) in articleList" :key="index" @click="goToDetail(item)">
          <image class="article-image" :src="item.coverImage" mode="aspectFill"></image>
          <view class="article-info">
            <text class="article-title">{{ item.title }}</text>
            <text class="article-desc">{{ item.description }}</text>
            <view class="article-meta">
              <view class="meta-left">
                <text class="article-category">{{ getCategoryName(item.categoryId) }}</text>
                <text class="article-date">{{ formatDate(item.createTime) }}</text>
              </view>
              <view class="meta-right">
                <view class="stat-item">
                  <uni-icons type="eye" size="14" color="#999"></uni-icons>
                  <text>{{ item.viewCount }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view class="empty-state" v-if="articleList.length === 0 && !loading">
        <image class="empty-image" src="/static/resource/empty.png" mode="aspectFit"></image>
        <text class="empty-text">暂无相关内容</text>
      </view>
      
      <!-- 加载更多 -->
      <view class="loading-more" v-if="loading">
        <uni-load-more status="loading" :contentText="loadMoreText"></uni-load-more>
      </view>
      <view class="no-more" v-if="noMoreData && articleList.length > 0">
        <uni-load-more status="noMore" :contentText="loadMoreText"></uni-load-more>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getResourceList, getResourceCategories, getFeaturedResourceList } from '@/api/resource';

export default {
  data() {
    return {
      statusBarHeight: 20,
      pageTitle: '分类浏览',
      type: '', // 'featured' 表示推荐文章
      currentCategory: 0, // 当前选中的分类ID
      categories: [
        { id: 0, name: '全部' }
      ],
      articleList: [],
      loading: false,
      refreshing: false,
      noMoreData: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        categoryId: 0,
        type: ''
      },
      loadMoreText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      }
    }
  },
  onLoad(options) {
    this.getStatusBarHeight();
    this.loadCategories();
    
    if (options.categoryId) {
      this.currentCategory = parseInt(options.categoryId);
      // 确保categoryId=0转为null
      this.queryParams.categoryId = this.currentCategory === 0 ? null : this.currentCategory;
      
      // 设置标题
      const category = this.categories.find(item => item.id === this.currentCategory);
      if (category) {
        this.pageTitle = category.name;
      }
    }
    
    if (options.type) {
      this.type = options.type;
      this.queryParams.type = this.type;
      
      if (this.type === 'featured') {
        this.pageTitle = '推荐阅读';
      }
    }
    
    this.loadArticleList();
  },
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight;
    },
    
    // 加载文章列表
    loadArticleList() {
      this.loading = true;
      
      // 准备查询参数
      const params = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize,
        categoryId: this.queryParams.categoryId
      };
      
      // 根据类型判断是否需要调用推荐接口
      if (this.type === 'featured') {
        // 调用推荐文章接口
        getFeaturedResourceList(params).then(response => {
          const { list, total } = response.data;
          
          if (this.queryParams.pageNum === 1) {
            this.articleList = list;
          } else {
            this.articleList = [...this.articleList, ...list];
          }
          
          this.noMoreData = this.articleList.length >= total;
          this.loading = false;
          this.refreshing = false;
        }).catch(error => {
          console.error('加载推荐文章失败', error);
          this.loading = false;
          this.refreshing = false;
          uni.showToast({
            title: '加载失败，请重试',
            icon: 'none'
          });
        });
      } else {
        // 调用普通文章列表接口
        getResourceList(params).then(response => {
          if (response.code === 200) {
            const { records, total } = response.data;
            
            if (this.queryParams.pageNum === 1) {
              this.articleList = records;
            } else {
              this.articleList = [...this.articleList, ...records];
            }
            
            this.noMoreData = records.length < this.queryParams.pageSize;
            this.loading = false;
            this.refreshing = false;
          } else {
            console.error('加载文章列表失败', response.msg);
            this.loading = false;
            this.refreshing = false;
            uni.showToast({
              title: '加载失败，请重试',
              icon: 'none'
            });
          }
        }).catch(error => {
          console.error('加载文章列表失败', error);
          this.loading = false;
          this.refreshing = false;
          uni.showToast({
            title: '加载失败，请重试',
            icon: 'none'
          });
        });
      }
    },
    
    // 选择分类
    selectCategory(categoryId) {
      if (this.currentCategory === categoryId) return;
      
      this.currentCategory = categoryId;
      this.queryParams.pageNum = 1;
      this.queryParams.categoryId = categoryId === 0 ? null : categoryId;
      this.noMoreData = false;
      
      // 更新页面标题
      if (this.type !== 'featured') {
        const category = this.categories.find(item => item.id === categoryId);
        this.pageTitle = category ? category.name : '分类浏览';
      }
      
      this.loadArticleList();
    },
    
    // 加载更多
    loadMore() {
      if (!this.loading && !this.noMoreData) {
        this.queryParams.pageNum++;
        this.loadArticleList();
      }
    },
    
    // 下拉刷新
    onRefresh() {
      this.refreshing = true;
      this.queryParams.pageNum = 1;
      this.noMoreData = false;
      this.loadArticleList();
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
    
    // 前往搜索页
    goToSearch() {
      uni.navigateTo({
        url: './search'
      });
    },
    
    // 前往详情页
    goToDetail(item) {
      if (!item || !item.id) {
        uni.showToast({
          title: '文章ID不能为空',
          icon: 'none'
        });
        return;
      }
      
      uni.navigateTo({
        url: `./detail?id=${item.id}`
      });
    },
    
    // 获取分类名称
    getCategoryName(categoryId) {
      const category = this.categories.find(item => item.id === categoryId);
      return category ? category.name : '';
    },
    
    // 格式化日期
    formatDate(dateStr) {
      return dateStr;
    },
    
    // 加载分类列表
    loadCategories() {
      getResourceCategories().then(response => {
        const allCategory = { id: 0, name: '全部' };
        this.categories = [allCategory, ...response.data];
      }).catch(error => {
        console.error('加载分类失败', error);
      });
    }
  }
}
</script>

<style lang="scss">
.category-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f8f8f8;
}

.header {
  background: linear-gradient(to right, #5c6bc0, #3949ab);
  color: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
  
  .navbar {
    height: 44px;
    display: flex;
    align-items: center;
    position: relative;
    padding: 0 15px;
    
    .left-btn, .right-btn {
      width: 44px;
      height: 44px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    
    .title {
      position: absolute;
      left: 0;
      right: 0;
      text-align: center;
      font-size: 17px;
      font-weight: 500;
    }
  }
  
  .category-tabs {
    width: 100%;
    white-space: nowrap;
    background-color: rgba(255, 255, 255, 0.1);
    padding: 10px 0;
    
    .tabs-list {
      display: flex;
      padding: 0 10px;
    }
    
    .tab-item {
      padding: 4px 16px;
      margin: 0 5px;
      border-radius: 16px;
      
      &.active {
        background-color: rgba(255, 255, 255, 0.2);
        
        .tab-text {
          color: #fff;
          font-weight: 500;
        }
      }
      
      .tab-text {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

.content-area {
  flex: 1;
  position: relative;
}

.article-list {
  padding: 10px 15px;
}

.article-item {
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  
  .article-image {
    width: 100%;
    height: 160px;
  }
  
  .article-info {
    padding: 12px 15px;
    
    .article-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 8px;
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .article-desc {
      font-size: 14px;
      color: #666;
      margin-bottom: 10px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      line-height: 1.4;
    }
    
    .article-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .meta-left {
        display: flex;
        align-items: center;
        
        .article-category {
          display: inline-block;
          background-color: rgba(57, 73, 171, 0.1);
          color: #3949ab;
          font-size: 12px;
          padding: 2px 6px;
          border-radius: 4px;
          margin-right: 10px;
        }
        
        .article-date {
          font-size: 12px;
          color: #999;
        }
      }
      
      .meta-right {
        .stat-item {
          display: flex;
          align-items: center;
          
          text {
            font-size: 12px;
            color: #999;
            margin-left: 4px;
          }
        }
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  
  .empty-image {
    width: 150px;
    height: 150px;
    margin-bottom: 15px;
  }
  
  .empty-text {
    font-size: 15px;
    color: #999;
  }
}

.loading-more, .no-more {
  padding: 15px 0;
  text-align: center;
}
</style> 