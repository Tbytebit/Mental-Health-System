<template>
  <view class="search-container">
    <!-- 顶部搜索栏 -->
    <view class="header">
      <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
      <view class="search-bar">
        <view class="back-btn" @click="goBack">
          <uni-icons type="back" size="20" color="#666"></uni-icons>
        </view>
        <view class="search-input-box">
          <uni-icons type="search" size="18" color="#999"></uni-icons>
          <input 
            class="search-input" 
            type="text" 
            v-model="keyword" 
            placeholder="搜索心理知识..."
            confirm-type="search"
            @confirm="search"
            auto-focus
          />
          <uni-icons v-if="keyword" type="clear" size="18" color="#999" @click="clearSearch"></uni-icons>
        </view>
        <view class="search-btn" @click="search">搜索</view>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view scroll-y class="content-area" @scrolltolower="loadMore">
      <!-- 搜索历史 -->
      <view class="history-section" v-if="!searched && searchHistory.length > 0">
        <view class="section-header">
          <text class="section-title">搜索历史</text>
          <text class="clear-history" @click="clearHistory">清空</text>
        </view>
        <view class="history-list">
          <view 
            class="history-item" 
            v-for="(item, index) in searchHistory" 
            :key="index"
            @click="searchWithHistory(item)"
          >
            <uni-icons type="search" size="14" color="#999"></uni-icons>
            <text class="history-text">{{ item }}</text>
          </view>
        </view>
      </view>

      <!-- 热门搜索 -->
      <view class="hot-section" v-if="!searched">
        <view class="section-header">
          <text class="section-title">热门搜索</text>
        </view>
        <view class="hot-tags">
          <view 
            class="tag-item" 
            v-for="(item, index) in hotSearches" 
            :key="index"
            @click="searchWithHistory(item)"
          >
            <text class="tag-text">{{ item }}</text>
          </view>
        </view>
      </view>

      <!-- 搜索结果 -->
      <block v-if="searched">
        <view class="result-header">
          <text class="result-title">搜索结果: {{ keyword }}</text>
          <text class="result-count">共 {{ totalResults }} 条结果</text>
        </view>

        <!-- 结果列表 -->
        <view class="result-list" v-if="searchList.length > 0">
          <view class="result-item" v-for="(item, index) in searchList" :key="index" @click="goToDetail(item)">
            <image class="result-image" :src="item.coverImage" mode="aspectFill"></image>
            <view class="result-info">
              <text class="result-title">{{ item.title }}</text>
              <text class="result-desc">{{ item.description }}</text>
              <view class="result-meta">
                <text class="result-category">{{ item.categoryName }}</text>
                <text class="result-date">{{ formatDate(item.createTime) }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view class="empty-state" v-if="searchList.length === 0 && !loading">
          <image class="empty-image" src="/static/resource/empty-search.png" mode="aspectFit"></image>
          <text class="empty-text">未找到与"{{ keyword }}"相关的内容</text>
          <text class="empty-tips">换个关键词试试</text>
        </view>
        
        <!-- 加载更多 -->
        <view class="loading-more" v-if="loading">
          <uni-load-more status="loading" :contentText="loadMoreText"></uni-load-more>
        </view>
        <view class="no-more" v-if="noMoreData && searchList.length > 0">
          <uni-load-more status="noMore" :contentText="loadMoreText"></uni-load-more>
        </view>
      </block>
    </scroll-view>
  </view>
</template>

<script>
import { searchResources } from '@/api/resource';

export default {
  data() {
    return {
      keyword: '',
      statusBarHeight: 20,
      searchParams: {
        pageNum: 1,
        pageSize: 10,
        keyword: ''
      },
      searchList: [],
      searched: false,
      loading: false,
      noMoreData: false,
      searchHistory: [],
      hotSearches: [
        '焦虑症状',
        '情绪管理',
        '人际关系',
        '心理压力',
        '抑郁自测',
        '认知行为疗法',
        '亲密关系',
        '冥想放松'
      ],
      totalResults: 0,
      loadMoreText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      }
    }
  },
  onLoad() {
    this.getStatusBarHeight();
    this.loadSearchHistory();
  },
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight;
    },
    
    // 搜索
    search() {
      if (!this.keyword.trim()) {
        return;
      }
      
      this.loading = true;
      this.searchParams.pageNum = 1;
      this.searchParams.keyword = this.keyword;
      this.searchList = [];
      this.noMoreData = false;
      
      this.fetchSearchResults();
    },
    
    // 获取搜索结果
    fetchSearchResults() {
      const params = {
        keyword: this.searchParams.keyword,
        pageNum: this.searchParams.pageNum,
        pageSize: this.searchParams.pageSize
      };
      
      searchResources(params).then(response => {
        if (response.code === 200) {
          const data = response.data;
          
          if (this.searchParams.pageNum === 1) {
            this.searchList = data;
          } else {
            this.searchList = [...this.searchList, ...data];
          }
          
          this.searched = true;
          
          // 判断是否还有更多数据
          if (data.length < this.searchParams.pageSize) {
            this.noMoreData = true;
          }
        }
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    
    // 加载更多
    loadMore() {
      if (!this.loading && !this.noMoreData) {
        this.searchParams.pageNum++;
        this.fetchSearchResults();
      }
    },
    
    // 使用历史记录搜索
    searchWithHistory(keyword) {
      this.keyword = keyword;
      this.search();
    },
    
    // 清空搜索内容
    clearSearch() {
      this.keyword = '';
      this.searched = false;
    },
    
    // 保存搜索历史
    saveSearchHistory(keyword) {
      // 先从本地获取历史
      let history = [...this.searchHistory];
      
      // 如果已存在，则移除旧的
      const index = history.indexOf(keyword);
      if (index !== -1) {
        history.splice(index, 1);
      }
      
      // 添加到最前面
      history.unshift(keyword);
      
      // 最多保存10条
      if (history.length > 10) {
        history = history.slice(0, 10);
      }
      
      // 更新状态和本地存储
      this.searchHistory = history;
      uni.setStorageSync('resource_search_history', JSON.stringify(history));
    },
    
    // 加载搜索历史
    loadSearchHistory() {
      const history = uni.getStorageSync('resource_search_history');
      if (history) {
        try {
          this.searchHistory = JSON.parse(history);
        } catch (e) {
          this.searchHistory = [];
        }
      }
    },
    
    // 清空搜索历史
    clearHistory() {
      uni.showModal({
        title: '提示',
        content: '确定要清空搜索历史吗？',
        success: (res) => {
          if (res.confirm) {
            this.searchHistory = [];
            uni.removeStorageSync('resource_search_history');
            uni.showToast({
              title: '已清空',
              icon: 'success'
            });
          }
        }
      });
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
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
    
    // 格式化日期
    formatDate(dateStr) {
      return dateStr;
    }
  }
}
</script>

<style lang="scss">
.search-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f8f8f8;
}

.header {
  background-color: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
  
  .search-bar {
    height: 56px;
    display: flex;
    align-items: center;
    padding: 0 15px;
    
    .back-btn {
      width: 30px;
      height: 30px;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 10px;
    }
    
    .search-input-box {
      flex: 1;
      height: 36px;
      background-color: #f5f5f5;
      border-radius: 18px;
      display: flex;
      align-items: center;
      padding: 0 12px;
      
      .search-input {
        flex: 1;
        height: 36px;
        font-size: 15px;
        margin: 0 8px;
      }
    }
    
    .search-btn {
      padding: 0 15px;
      font-size: 16px;
      color: #3949ab;
    }
  }
}

.content-area {
  flex: 1;
  position: relative;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
  }
  
  .clear-history {
    font-size: 14px;
    color: #999;
  }
}

.history-section {
  background-color: #fff;
  margin-bottom: 10px;
  
  .history-list {
    padding: 0 15px 15px;
  }
  
  .history-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .history-text {
      font-size: 15px;
      color: #333;
      margin-left: 10px;
    }
  }
}

.hot-section {
  background-color: #fff;
  
  .hot-tags {
    display: flex;
    flex-wrap: wrap;
    padding: 0 15px 20px;
    
    .tag-item {
      background-color: #f5f5f5;
      border-radius: 16px;
      padding: 8px 16px;
      margin: 0 10px 10px 0;
      
      .tag-text {
        font-size: 14px;
        color: #666;
      }
    }
  }
}

.result-header {
  padding: 15px;
  background-color: #fff;
  margin-bottom: 10px;
  
  .result-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 5px;
    display: block;
  }
  
  .result-count {
    font-size: 14px;
    color: #999;
  }
}

.result-list {
  background-color: #fff;
  padding: 0 15px;
}

.result-item {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
  }
  
  .result-image {
    width: 100px;
    height: 75px;
    border-radius: 6px;
    margin-right: 12px;
    flex-shrink: 0;
  }
  
  .result-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    
    .result-title {
      font-size: 16px;
      font-weight: 500;
      color: #333;
      margin-bottom: 6px;
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .result-desc {
      font-size: 14px;
      color: #666;
      margin-bottom: 8px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .result-meta {
      display: flex;
      align-items: center;
      
      .result-category {
        font-size: 12px;
        color: #3949ab;
        margin-right: 10px;
      }
      
      .result-date {
        font-size: 12px;
        color: #999;
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
    font-size: 16px;
    color: #666;
    margin-bottom: 8px;
  }
  
  .empty-tips {
    font-size: 14px;
    color: #999;
  }
}

.loading-more, .no-more {
  padding: 15px 0;
  text-align: center;
}
</style> 