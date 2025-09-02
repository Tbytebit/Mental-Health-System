<template>
  <view class="resource-container">
    <!-- 顶部导航栏 -->
    <view class="header">
      <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
      <view class="navbar">
        <view class="back-btn" @click="goBack">
          <uni-icons type="left" size="18" color="#ffffff"></uni-icons>
        </view>
        <view class="title">
          <image class="title-icon" src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzMzMzMzMyI+PHBhdGggZD0iTTIxIDVjLTEuMTEtLjM1LTIuMzMtLjUtMy41LS41LTEuOTUgMC00LjA1LjQtNS41IDEuNS0xLjQ1LTEuMS0zLjU1LTEuNS01LjUtMS41UzIuNDUgNC45IDEgNnYxNC41QzIuNDUgMTkuOSA0LjU1IDE5IDYuNSAxOXMzLjk1LjkgNS41IDIuMVYxNi44MWMxLjc1LS45NyAzLjQ0LTEuNjcgNS41LTEuODEgMSAuMTQgMS45MS40NyAyLjUuODF2Ny4zNGMuNjEtLjM1IDEuMjgtLjYzIDItLjgxVjVabTAgMTNjLS44My40LS44My4zNy0yIC44M3YtMi44M2MuODUuMjQgMS44LjQzIDIgLjQzdjEuNTdabS04LTMuOTRjLjItLjA0IDEuNTktLjMgMi4wNC0uMzYuMjUuOC40LjE1LjY2LjI2VjE4Yy0xLjEtLjQzLTIuMjQtLjcyLTMuNS0uNzNsLS4wMS0uMjFjLjI4LS4wNS41My0uMS43Ni0uMTRsLjA1LTMuN1ptLTIuMDguMWMtLjQ2LjA5LTEuMTQuMjQtMS45Mi40N1YxOGMxLjMgMCAyLjU0LjMgMy42OC44MWwuMDItMy43OGMtLjQ5LS4xOS0xLjE5LS40IDEuNzQtLjM5TDEyLjkxIDE0bC0uOTkuMTZaTTYgMTNjLTEuOSAwLTMuOTEuNDktNCAuNzV2My40Yy42NC0uMTcgMS4yNy0uMjkgMS44NS0uMzYuMzQtLjExLjctLjIxIDEuMDYtLjI5LjMtLjA2LjYtLjEzLjktLjE5bC4xOS0uMDRWMTNaTTUgMTZjLS4zNiAwLS43MS4wMy0xLjA2LjA5QzMuMzMgMTYuMjIgMi42OSAxNi40IDIgMTYuNjhWNmMuNzYtLjU0IDIuOTgtMSA0LjUtMSAxLjUyIDAgMy43NC42IDQuNSAxLjJxLS4wMyA1LjA3IDAgMTQuMDVDLTkuNDYgMTkuMTcgNy4xIDE4LjUgNSAxNnptMTUgLjA4Yy0uNzYuMzYtMS4zNi42MS0yIC43MXMtMS40NS4xNS0yLjUuMTVjLS42NiAwLTEuMzMtLjA4LTItLjE4di0uOTNjLjYyLjExIDEuMjkuMTkgMiAuMTkuODQgMCAxLjU2LS4xMyAyLjExLS4yOXMxLjExLS4zMiAxLjU3LS41bC44Mi0uMzNWMTVoLjAxeiIvPjxjaXJjbGUgY3g9IjgiIGN5PSI5IiByPSIyIi8+PC9zdmc+" />
          心理资料库
        </view>
        <view class="search-box" @click="goToSearch">
          <uni-icons type="search" size="16" color="#ffffff"></uni-icons>
          <text class="search-placeholder">搜索心理知识...</text>
        </view>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view scroll-y class="content-area" @scrolltolower="loadMore">
      <!-- 分类导航 -->
      <view class="category-section">
        <scroll-view scroll-x class="category-scroll" show-scrollbar="false">
          <view class="category-list">
            <view 
              v-for="(item, index) in categories" 
              :key="index" 
              class="category-item" 
              :class="{ active: currentCategory === item.id }"
              @click="selectCategory(item.id)"
            >
              <image class="category-icon" :src="categoryIcons[index]" mode="aspectFill"></image>
              <text class="category-name">{{ item.name }}</text>
            </view>
          </view>
        </scroll-view>
      </view>

      <!-- 推荐文章 -->
      <view class="featured-section" v-if="featuredList.length > 0">
        <view class="section-header">
          <text class="section-title">推荐阅读</text>
          <text class="view-more" @click="viewMoreFeatured">查看更多</text>
        </view>
        <view class="featured-content">
          <swiper class="featured-swiper" :indicator-dots="true" indicator-color="rgba(255, 255, 255, .4)" indicator-active-color="#ffffff" :autoplay="true" :interval="3000" :duration="500">
            <swiper-item v-for="(item, index) in featuredList" :key="index" @click="goToDetail(item)">
              <view class="featured-item">
                <image class="featured-image" :src="item.coverImage" mode="aspectFill"></image>
                <view class="featured-info">
                  <text class="featured-title">{{ item.title }}</text>
                  <text class="featured-desc">{{ item.description }}</text>
                </view>
              </view>
            </swiper-item>
          </swiper>
        </view>
      </view>

      <!-- 最新文章 -->
      <view class="latest-section">
        <view class="section-header">
          <text class="section-title">最新文章</text>
          <text class="view-more" @click="viewMoreLatest">查看更多</text>
        </view>
        <view class="article-list">
          <view class="article-item" v-for="(item, index) in articleList" :key="index" @click="goToDetail(item)">
            <image class="article-image" :src="item.coverImage" mode="aspectFill"></image>
            <view class="article-info">
              <text class="article-title">{{ item.title }}</text>
              <text class="article-desc">{{ item.description }}</text>
              <view class="article-meta">
                <text class="article-category">{{ getCategoryName(item.categoryId) }}</text>
                <text class="article-date">{{ formatDate(item.createTime) }}</text>
                <view class="article-stats">
                  <view class="stat-item">
                    <uni-icons type="eye" size="14" color="#999"></uni-icons>
                    <text>{{ item.viewCount }}</text>
                  </view>
                  <view class="stat-item">
                    <uni-icons type="heart" size="14" color="#999"></uni-icons>
                    <text>{{ item.likeCount }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
        
        <!-- 加载更多 -->
        <view class="loading-more" v-if="loading">
          <uni-load-more status="loading" :contentText="loadMoreText"></uni-load-more>
        </view>
        <view class="no-more" v-if="noMoreData">
          <uni-load-more status="noMore" :contentText="loadMoreText"></uni-load-more>
        </view>
      </view>
    </scroll-view>

    <!-- 底部tab占位 -->
    <view class="bottom-space"></view>
  </view>
</template>

<script>
import { getResourceCategories, getResourceList, getFeaturedResourceList } from '@/api/resource';

export default {
  data() {
    return {
      statusBarHeight: 20,
      currentCategory: 0, // 当前选中的分类ID
      categories: [
        { id: 0, name: '全部' },
        { id: 1, name: '情绪管理' },
        { id: 2, name: '人际关系' },
        { id: 3, name: '压力应对' },
        { id: 4, name: '认知治疗' },
        { id: 5, name: '自我提升' },
        { id: 6, name: '心理健康' }
      ],
      categoryIcons: [
        'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzQyNTVmNCI+PHBhdGggZD0iTTQgMTFoNFY1SDR2NnptMCA4aDRWMTNoNHY2SDR6bTYtOGg0VjVoLTR2NnptNiAwaDRWNWgtNHY2em0wIDhoNHYtNmgtNHY2em0tNiAwaDRWMTNoLTR2NnoiLz48L3N2Zz4=',
        'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iI2Y0NDMzNiI+PHBhdGggZD0iTTExLjk5IDE4LjU0bC03LjM3LTUuNzNMMyAxNC4wN2w5IDcgOS03LTEuNjMtMS4yN3oiLz48cGF0aCBkPSJNMTIgMTZsNy4zNi01LjczTDIxIDlsLTktNy05IDcgMS42MyAxLjI3TDEyIDE2eiIvPjwvc3ZnPg==',
        'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzAwOTY4OCI+PHBhdGggZD0iTTE2IDExYzEuNjYgMCAyLjk5LTEuMzQgMi45OS0zUzE3LjY2IDUgMTYgNWMtMS42NiAwLTMgMS4zNC0zIDNzMS4zNCAzIDMgM3ptLTggMGMxLjY2IDAgMi45OS0xLjM0IDIuOTktM1M5LjY2IDUgOCA1QzYuMzQgNSA1IDYuMzQgNSA4czEuMzQgMyAzIDN6bTAgMmMtMi4zMyAwLTcgMS4xNy03IDMuNVYxOWgxNHYtMi41YzAtMi4zMy00LjY3LTMuNS03LTMuNXptOCAwYy0uMjkgMC0uNjIuMDItLjk3LjA1IDEuMTYuODQgMS45NyAxLjk3IDEuOTcgMy40NVYxOWg2di0yLjVjMC0yLjMzLTQuNjctMy41LTctMy41eiIvPjwvc3ZnPg==',
        'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iI2ZmOTgwMCI+PHBhdGggZD0iTTIxIDEwLjVjLjk2IDAgMS43NS43OCAxLjc1IDEuNzVzLS43OSAxLjc1LTEuNzUgMS43NUgxMHYtMy41aDExek0xMCAxNnYzLjVoMi41YzEuNSAwIDIuNS0xLjI1IDIuNS0xLjc1cy0xLTEuNzUtMi41LTEuNzVIMTB6TTEwIDYuNVYxMGgzLjVjMS41IDAgMS43NS0xLjc1IDEuNzUtMS43NVMxNSA2LjUgMTMuNSA2LjVIMTB6TTMgNi41QzIuMDQgNi41IDEuMjUgNy4yOCAxLjI1IDguMjVTMi4wNCAxMCAzIDEwaDQuNVY2LjVIM3pNMyAxM2MtLjk2IDAtMS43NS43OS0xLjc1IDEuNzVTMi4wNCAxNi41IDMgMTYuNWgzLjVWMTNIM3pNMyAxOS41Yy0uOTYgMC0xLjc1Ljc5LTEuNzUgMS43NVMyLjA0IDIzIDMgMjNoNy41di0zLjVIM3oiLz48L3N2Zz4=',
        'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzY3M2FiNyI+PHBhdGggZD0iTTIwLjU3IDEyLjA5YzAgLjMyLS4wMy42NC0uMDkuOTVoMS4xM2MuMDUtLjMxLjA5LS42Mi4wOS0uOTUgMC0zLjM3LTIuNjktNi4xLTYuMDQtNi4xOHY5Ljk1aDE0LjQ4YzAgMS43MS0xLjM5IDMuMS0zLjExIDMuMWgtMTVjLTEuNzEgMC0zLjEtMS4zOS0zLjEtMy4xVjkuODJjMC0zLjQyIDIuNzItNi4yMyA2LjA5LTYuMzF2LjEzQzExLjUgMy42NCA4LjkgNi41MyA4LjkgMTAuMDljMCAuNTkuMDkgMS4xNS4yNSAxLjY5bC43NiAxLjVoMmMuNTUgMCAuOTkuNDUuOTkgMXYyLjU3YzAgLjU1LS40NSAxLS45OSAxSDguMTdsLTQuOTMtMS41NEM0LjExIDIxLjY2IDcuNjggMjUgMTIgMjVjNS41MiAwIDEwLTQuNDggMTAtMTAgMC0uOTktLjE0LTEuOTQtLjQxLTIuODRsLS41OC0xLjU4aDIuMTljLjM2IDAgLjY5LjE5Ljg2LjVsLjg2IDEuNTVoMS4yMWwtLjg2LTEuNTVjLS4zOC0uNjktMS4xMS0xLjEyLTEuODktMS4xMmgtNC43OGMuNDItLjQ1LjcyLS45OC44Ni0xLjU2bC42LTIuNGgtMS4yOWwtLjYgMi40Yy0uMTkuOC0uOS0uMTItMS42Ni0uMTItLjI2IDAtLjUyLjA0LS43Ny4xM2wtLjAgMS41SDEzLjU2Yy0uMjQtLjY4LS42NS0xLjI4LTEuMTktMS43N3YtLjY4YzEuNTUtLjE0IDMuMjUuMzUgNC40NyAxLjN2LTEuMTdjLTEuNy0xLjAyLTMuNjUtMS4yNi01LjQzLS45OS0uOTMuMTQtMS44NS0uMzQtMi4zMi0xLjJ2MS43NmMuNjQtLjI2IDEuMzQtLjI2IDEuOTctLjA2djIuMTZjMCAxLjg0IDEuNDkgMy4zMyAzLjMzIDMuMzMgMS44NCAwIDMuMzMtMS40OSAzLjMzLTMuMzN2LTMuNjdjLTEuMDEtLjcxLTIuMjgtLjg5LTMuNDQtLjU5eiIvPjwvc3ZnPg==',
        'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzRjYWY1MCI+PHBhdGggZD0iTTEzIDRsMi4yOS0yLjI5TDE3IDMuNDJWMWgydjUuMDVDMTggNi44MyAxNy4xNiA5IDEzIDlWMzFsNSA1VjI3LjU5bDIuMjkgMi4yOSAxLjQyLTEuNDJMMjAgMjdoNnYtMmgtNS4yMWwyLjIxLTIuMjEtMS40Mi0xLjQyTDE5IDI0aDZWM0MxOCAzIDE3IDMgMTYgNGgtM3pNMTIgMTBIMi42MmwtMi42NyA1LjAybC0uODQgMS40Ni0uMTEuMTh2LjM0aDEuMzVsLjMzLS4wN2MuMTMtLjAzLjI2LS4wNi4zOS0uMDZoMy4yNFYyMUgySCAwdjFoMnYuMDJoM3YtNi40MmMwLS4xMy4wMy0uMjUuMDYtLjM4TC42MyAxMGgxMS4yNnYtMkgxMnoiLz48L3N2Zz4=',
        'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iI2U5MWU2MyI+PHBhdGggZD0iTTEyIDIxLjM1bC0xLjQ1LTEuMzJDNS40IDE1LjM2IDIgMTIuMjggMiA4LjUgMiA1LjQyIDQuNDIgMyA3LjUgM2MxLjc4IDAgMy40MS44MSA0LjUgMi4wOUMxMy4wOSAzLjgxIDE0Ljc2IDMgMTYuNSAzIDE5LjU4IDMgMjIgNS40MiAyMiA4LjVjMCAzLjc4LTMuNCA2Ljg2LTguNTUgMTEuNTRMMTIgMjEuMzV6Ii8+PC9zdmc+'
      ],
      featuredList: [],
      articleList: [],
      loading: false,
      noMoreData: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        categoryId: 0
      },
      loadMoreText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      }
    }
  },
  onLoad() {
    this.getStatusBarHeight();
    this.loadFeaturedArticles();
    this.loadArticleList();
  },
  methods: {
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
    
    // 获取状态栏高度
    getStatusBarHeight() {
      this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight;
    },
    
    // 加载推荐文章
    loadFeaturedArticles() {
      this.loading = true;
      
      getFeaturedResourceList({ limit: 3 }).then(response => {
        if (response.code === 200) {
          this.featuredList = response.data;
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    
    // 加载文章列表
    loadArticleList() {
      this.loading = true;
      
      const params = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize,
        categoryId: this.queryParams.categoryId
      };
      
      getResourceList(params).then(response => {
        if (response.code === 200) {
          const data = response.data;
          
          if (this.queryParams.pageNum === 1) {
            this.articleList = data;
          } else {
            this.articleList = [...this.articleList, ...data];
          }
          
          // 判断是否还有更多数据
          if (data.length < this.queryParams.pageSize) {
            this.noMoreData = true;
          }
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    
    // 选择分类
    selectCategory(categoryId) {
      this.currentCategory = categoryId;
      this.queryParams.pageNum = 1;
      this.queryParams.categoryId = categoryId;
      this.noMoreData = false;
      this.loadArticleList();
    },
    
    // 加载更多
    loadMore() {
      if (!this.loading && !this.noMoreData) {
        this.queryParams.pageNum++;
        this.loadArticleList();
      }
    },
    
    // 查看更多推荐
    viewMoreFeatured() {
      uni.navigateTo({
        url: './category?type=featured'
      });
    },
    
    // 查看更多最新
    viewMoreLatest() {
      uni.navigateTo({
        url: './category'
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
    
    // 跳转到搜索页
    goToSearch() {
      uni.navigateTo({
        url: './search'
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
    
    // 下拉刷新
    onPullDownRefresh() {
      this.queryParams.pageNum = 1;
      this.noMoreData = false;
      this.loadFeaturedArticles();
      this.loadArticleList();
      setTimeout(() => {
        uni.stopPullDownRefresh();
      }, 1000);
    }
  }
}
</script>

<style lang="scss">
.resource-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f8f8f8;
}

.header {
  background: linear-gradient(to right, #5c6bc0, #3949ab);
  color: #fff;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.back-btn {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.navbar {
  height: 104px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  padding: 0 15px;
  padding-top: var(--status-bar-height, 20px);
  
  .title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .title-icon {
      width: 24px;
      height: 24px;
      margin-right: 8px;
    }
  }
  
  .search-box {
    display: flex;
    align-items: center;
    background-color: rgba(255, 255, 255, 0.3);
    border-radius: 20px;
    padding: 8px 16px;
    width: 90%;
    margin-bottom: 10px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    
    .search-placeholder {
      color: #ffffff;
      font-size: 14px;
      margin-left: 10px;
    }
  }
}

.content-area {
  flex: 1;
  position: relative;
  margin-top: calc(104px + var(--status-bar-height, 20px));
  padding-bottom: 50px;
}

.category-section {
  background-color: #fff;
  padding: 10px 0;
  margin-top: 5px;
  margin-bottom: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  
  .category-scroll {
    white-space: nowrap;
  }
  
  .category-list {
    display: flex;
    padding: 0 10px;
  }
  
  .category-item {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    margin: 0 10px;
    width: 70px;
    
    &.active {
      .category-name {
        color: #3949ab;
        font-weight: 600;
      }
    }
    
    .category-icon {
      width: 42px;
      height: 42px;
      border-radius: 21px;
      margin-bottom: 6px;
      background-color: rgba(57, 73, 171, 0.1);
      padding: 8px;
    }
    
    .category-name {
      font-size: 14px;
      color: #333;
    }
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    position: relative;
    padding-left: 10px;
    
    &:before {
      content: '';
      position: absolute;
      left: 0;
      top: 4px;
      height: 16px;
      width: 4px;
      background-color: #3949ab;
      border-radius: 2px;
    }
  }
  
  .view-more {
    font-size: 14px;
    color: #666;
  }
}

.featured-section {
  background-color: #fff;
  margin-bottom: 10px;
  padding-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  
  .featured-swiper {
    height: 180px;
  }
  
  .featured-item {
    position: relative;
    height: 100%;
    
    .featured-image {
      width: 100%;
      height: 100%;
    }
    
    .featured-info {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      padding: 15px;
      background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
      
      .featured-title {
        font-size: 18px;
        font-weight: 600;
        color: #fff;
        margin-bottom: 5px;
        display: block;
      }
      
      .featured-desc {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
        display: block;
      }
    }
  }
}

.latest-section {
  background-color: #fff;
  margin-bottom: 10px;
  padding-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  
  .article-list {
    padding: 0 15px;
  }
  
  .article-item {
    display: flex;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .article-image {
      width: 120px;
      height: 90px;
      border-radius: 8px;
      margin-right: 12px;
      flex-shrink: 0;
    }
    
    .article-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      
      .article-title {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 6px;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
      
      .article-desc {
        font-size: 14px;
        color: #666;
        margin-bottom: 8px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
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
          padding: 2px 6px;
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
  }
}

.loading-more, .no-more {
  padding: 15px 0;
  text-align: center;
}

.bottom-space {
  height: 50px;
}
</style>