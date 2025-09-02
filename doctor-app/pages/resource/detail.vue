<template>
  <view class="detail-container">
    <!-- 顶部导航栏 -->
    <view class="header">
      <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
      <view class="navbar">
        <view class="nav-btn left-btn" @click="goBack">
          <uni-icons type="back" size="22" color="#fff"></uni-icons>
        </view>
        <view class="title">文章详情</view>
        <view class="nav-btn right-btn"></view>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view scroll-y class="content-area">
      <!-- 文章封面 -->
      <view class="article-cover">
        <image class="cover-image" :src="article.coverImage" mode="aspectFill"></image>
      </view>

      <!-- 文章标题和信息 -->
      <view class="article-header">
        <text class="article-title">{{ article.title }}</text>
        <view class="article-meta">
          <text class="article-category">{{ article.categoryName }}</text>
          <text class="article-date">{{ formatDate(article.createTime) }}</text>
          <view class="article-stats">
            <view class="stat-item">
              <uni-icons type="eye" size="14" color="#999"></uni-icons>
              <text>{{ article.viewCount }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 作者信息 -->
      <view class="author-info" v-if="article.authorName">
        <image class="author-avatar" :src="article.authorAvatar" mode="aspectFill"></image>
        <view class="author-detail">
          <text class="author-name">{{ article.authorName }}</text>
          <text class="author-desc">{{ article.authorDesc }}</text>
        </view>
      </view>

      <!-- 文章内容 -->
      <view class="article-content">
        <rich-text :nodes="article.content"></rich-text>
      </view>

      <!-- 相关推荐 -->
      <view class="related-section" v-if="relatedList.length > 0">
        <view class="section-header">
          <text class="section-title">相关推荐</text>
        </view>
        <view class="related-list">
          <view class="related-item" v-for="(item, index) in relatedList" :key="index" @click="goToDetail(item)">
            <image class="related-image" :src="item.coverImage" mode="aspectFill"></image>
            <view class="related-info">
              <text class="related-title">{{ item.title }}</text>
              <view class="related-meta">
                <text class="related-category">{{ item.categoryName }}</text>
                <text class="related-views">{{ item.viewCount }} 阅读</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏，只保留查看更多按钮 -->
    <view class="bottom-bar">
      <view class="action-group right">
        <view class="related-button" @click="viewMoreArticles">
          查看更多
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getResourceDetail, getRelatedResources, increaseViewCount } from '@/api/resource';

export default {
  data() {
    return {
      statusBarHeight: 20,
      id: null,
      article: {
        id: 0,
        title: '',
        description: '',
        content: '',
        coverImage: '',
        categoryId: 0,
        categoryName: '',
        authorName: '',
        authorAvatar: '',
        authorDesc: '',
        viewCount: 0,
        createTime: ''
      },
      relatedList: [],
    }
  },
  onLoad(options) {
    this.getStatusBarHeight();
    if (options.id) {
      this.id = options.id;
      this.getArticleDetail();
    } else {
      uni.showToast({
        title: '文章ID不能为空',
        icon: 'none'
      });
      setTimeout(() => {
        this.goBack();
      }, 1500);
    }
  },
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight;
    },
    
    // 获取文章详情
    getArticleDetail() {
      if (!this.id) {
        uni.showToast({
          title: '文章ID不能为空',
          icon: 'none'
        });
        return;
      }
      
      getResourceDetail(this.id).then(response => {
        if (response.code === 200) {
          this.article = response.data;
          
          // 增加浏览次数
          this.increaseArticleViewCount();
          
          // 获取详情后再获取相关文章
          this.getRelatedArticles();
        }
      }).catch(error => {
        console.error('获取文章详情失败:', error);
        uni.showToast({
          title: '获取文章详情失败',
          icon: 'none'
        });
      });
    },
    
    // 增加文章浏览量
    increaseArticleViewCount() {
      if (!this.id) {
        console.error('增加浏览次数失败: 文章ID不能为空');
        return;
      }
      
      increaseViewCount(this.id).catch(error => {
        console.error('增加浏览次数失败:', error);
      });
    },
    
    // 获取相关文章
    getRelatedArticles() {
      if (!this.id || !this.article.categoryId) {
        return;
      }
      getRelatedResources(this.id, this.article.categoryId).then(response => {
        if (response.code === 200) {
          this.relatedList = response.data;
        }
      });
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack({
        fail: () => {
          uni.switchTab({
            url: '/pages/index/index'
          });
        }
      });
    },
    
    // 查看更多文章
    viewMoreArticles() {
      uni.navigateTo({
        url: './category?categoryId=' + this.article.categoryId
      });
    },
    
    // 跳转到详情页
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
    
    // 格式化日期
    formatDate(dateStr) {
      return dateStr;
    }
  }
}
</script>

<style lang="scss">
.detail-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #fff;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: linear-gradient(to right, #5c6bc0, #3949ab);
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 44px;
  position: relative;
  
  .nav-btn {
    width: 48px;
    height: 44px;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 102;
  }
  
  .title {
    position: absolute;
    left: 0;
    right: 0;
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    color: #fff;
    z-index: 101;
  }
}

.content-area {
  flex: 1;
  margin-top: calc(44px + var(--status-bar-height, 20px));
  padding-bottom: 60px;
}

.article-cover {
  width: 100%;
  height: 220px;
  
  .cover-image {
    width: 100%;
    height: 100%;
  }
}

.article-header {
  padding: 20px 15px;
  
  .article-title {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    line-height: 1.4;
    margin-bottom: 15px;
  }
  
  .article-meta {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    
    .article-category {
      display: inline-block;
      background-color: rgba(57, 73, 171, 0.1);
      color: #3949ab;
      font-size: 12px;
      padding: 3px 8px;
      border-radius: 4px;
      margin-right: 10px;
    }
    
    .article-date {
      font-size: 12px;
      color: #999;
      margin-right: 10px;
    }
    
    .article-stats {
      display: flex;
      align-items: center;
      
      .stat-item {
        display: flex;
        align-items: center;
        margin-right: 10px;
        
        text {
          font-size: 12px;
          color: #999;
          margin-left: 4px;
        }
      }
    }
  }
}

.author-info {
  padding: 15px;
  background-color: #f8f8f8;
  display: flex;
  align-items: center;
  margin: 0 15px 15px;
  border-radius: 8px;
  
  .author-avatar {
    width: 50px;
    height: 50px;
    border-radius: 25px;
    margin-right: 12px;
  }
  
  .author-detail {
    flex: 1;
    
    .author-name {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 4px;
    }
    
    .author-desc {
      font-size: 12px;
      color: #666;
    }
  }
}

.article-content {
  padding: 15px;
  background-color: #fff;
}

.related-section {
  padding: 15px 0;
  border-top: 10px solid #f5f5f5;
  
  .section-header {
    padding: 0 15px 10px;
    font-size: 18px;
    font-weight: 600;
    color: #333;
  }
  
  .related-list {
    padding: 0 15px;
  }
  
  .related-item {
    display: flex;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .related-image {
      width: 100px;
      height: 70px;
      border-radius: 6px;
      margin-right: 12px;
      flex-shrink: 0;
    }
    
    .related-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      
      .related-title {
        font-size: 15px;
        font-weight: 500;
        color: #333;
        margin-bottom: 6px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
      
      .related-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .related-category {
          font-size: 12px;
          color: #3949ab;
        }
        
        .related-views {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 15px;
  z-index: 90;
  
  .action-group {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .related-button {
    background-color: #3949ab;
    color: #fff;
    padding: 8px 24px;
    border-radius: 20px;
    font-size: 14px;
  }
}
</style> 