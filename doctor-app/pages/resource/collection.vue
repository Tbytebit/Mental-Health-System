<template>
  <view class="collection-container">
    <!-- 顶部导航栏 -->
    <view class="header">
      <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
      <view class="navbar">
        <view class="left-btn" @click="goBack">
          <uni-icons type="back" size="20" color="#fff"></uni-icons>
        </view>
        <view class="title">我的收藏</view>
        <view class="right-btn" @click="manageCollection" v-if="collectionList.length > 0">
          <text class="manage-text">{{ isManaging ? '完成' : '管理' }}</text>
        </view>
      </view>
    </view>

    <!-- 内容区域 -->
    <scroll-view 
      scroll-y 
      class="content-area" 
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <!-- 收藏列表 -->
      <view class="collection-list" v-if="collectionList.length > 0">
        <view 
          class="collection-item" 
          v-for="(item, index) in collectionList" 
          :key="index" 
          @click="goToDetail(item)"
        >
          <view class="checkbox" v-if="isManaging" @click.stop="toggleSelect(item)">
            <uni-icons 
              :type="item.selected ? 'checkbox-filled' : 'circle'" 
              :color="item.selected ? '#3949ab' : '#d1d1d1'" 
              size="20"
            ></uni-icons>
          </view>
          <image class="collection-image" :src="item.coverImage" mode="aspectFill"></image>
          <view class="collection-info">
            <text class="collection-title">{{ item.title }}</text>
            <text class="collection-desc">{{ item.description }}</text>
            <view class="collection-meta">
              <text class="collection-category">{{ item.categoryName }}</text>
              <text class="collection-date">{{ formatDate(item.collectTime) }}</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view class="empty-state" v-if="collectionList.length === 0">
        <image class="empty-image" src="/static/resource/empty-collection.png" mode="aspectFit"></image>
        <text class="empty-text">暂无收藏内容</text>
        <button class="explore-btn" @click="exploreResource">去探索</button>
      </view>
    </scroll-view>

    <!-- 底部操作栏 - 仅在管理模式显示 -->
    <view class="bottom-bar" v-if="isManaging && collectionList.length > 0">
      <view class="select-all" @click="toggleSelectAll">
        <uni-icons 
          :type="isAllSelected ? 'checkbox-filled' : 'circle'" 
          :color="isAllSelected ? '#3949ab' : '#d1d1d1'" 
          size="20"
        ></uni-icons>
        <text class="select-text">全选</text>
      </view>
      <view class="delete-btn" @click="deleteSelected" :class="{ disabled: !hasSelected }">
        <text class="delete-text">删除</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      statusBarHeight: 20,
      collectionList: [],
      isManaging: false,
      refreshing: false
    }
  },
  computed: {
    // 是否全选
    isAllSelected() {
      return this.collectionList.length > 0 && this.collectionList.every(item => item.selected);
    },
    
    // 是否有选中项
    hasSelected() {
      return this.collectionList.some(item => item.selected);
    }
  },
  onLoad() {
    this.getStatusBarHeight();
    this.loadCollectionList();
  },
  methods: {
    // 获取状态栏高度
    getStatusBarHeight() {
      this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight;
    },
    
    // 加载收藏列表
    loadCollectionList() {
      // 模拟API请求
      setTimeout(() => {
        // 模拟数据
        this.collectionList = [
          {
            id: 3,
            title: '认知行为疗法实用指南',
            description: '探索如何通过改变思维模式来影响情绪和行为，提高生活质量',
            coverImage: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
            categoryId: 4,
            categoryName: '认知治疗',
            collectTime: '2023-09-25',
            selected: false
          },
          {
            id: 1,
            title: '如何有效管理焦虑情绪',
            description: '本文将介绍几种有效的焦虑管理技巧，帮助你在压力情境下保持冷静',
            coverImage: 'https://images.unsplash.com/photo-1499209974431-9dddcece7f88?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
            categoryId: 1,
            categoryName: '情绪管理',
            collectTime: '2023-09-20',
            selected: false
          },
          {
            id: 5,
            title: '应对社交焦虑的实用策略',
            description: '社交场合的紧张和不适感困扰着很多人，本文分享克服社交焦虑的有效方法',
            coverImage: 'https://images.unsplash.com/photo-1520642413789-2bd6770d59e3?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
            categoryId: 3,
            categoryName: '压力应对',
            collectTime: '2023-09-18',
            selected: false
          },
          {
            id: 6,
            title: '正念生活：当下体验的艺术',
            description: '学习如何通过正念练习，减少对过去的执着和对未来的忧虑，活在当下',
            coverImage: 'https://images.unsplash.com/photo-1476900966873-ab290e38e3f7?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80',
            categoryId: 6,
            categoryName: '心理健康',
            collectTime: '2023-09-10',
            selected: false
          }
        ];
        
        this.refreshing = false;
      }, 800);
    },
    
    // 进入管理模式
    manageCollection() {
      this.isManaging = !this.isManaging;
      
      // 退出管理模式时，清除所有选择
      if (!this.isManaging) {
        this.collectionList.forEach(item => {
          item.selected = false;
        });
      }
    },
    
    // 切换选择状态
    toggleSelect(item) {
      item.selected = !item.selected;
    },
    
    // 全选/取消全选
    toggleSelectAll() {
      const newState = !this.isAllSelected;
      this.collectionList.forEach(item => {
        item.selected = newState;
      });
    },
    
    // 删除选中项
    deleteSelected() {
      if (!this.hasSelected) return;
      
      uni.showModal({
        title: '提示',
        content: '确定要删除选中的收藏吗？',
        success: (res) => {
          if (res.confirm) {
            // 过滤掉被选中的项
            this.collectionList = this.collectionList.filter(item => !item.selected);
            
            // 如果列表为空，退出管理模式
            if (this.collectionList.length === 0) {
              this.isManaging = false;
            }
            
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            });
          }
        }
      });
    },
    
    // 下拉刷新
    onRefresh() {
      this.refreshing = true;
      this.isManaging = false;
      this.loadCollectionList();
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack();
    },
    
    // 前往详情页
    goToDetail(item) {
      if (this.isManaging) return;
      
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
    
    // 去探索资源
    exploreResource() {
      uni.navigateTo({
        url: './index'
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
.collection-container {
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
    justify-content: space-between;
    padding: 0 15px;
    
    .left-btn, .right-btn {
      height: 44px;
      display: flex;
      align-items: center;
    }
    
    .title {
      font-size: 17px;
      font-weight: 500;
    }
    
    .manage-text {
      font-size: 15px;
      color: #fff;
    }
  }
}

.content-area {
  flex: 1;
  position: relative;
  padding-bottom: 60px;
}

.collection-list {
  padding: 10px 15px;
}

.collection-item {
  display: flex;
  align-items: center;
  background-color: #fff;
  margin-bottom: 10px;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  
  .checkbox {
    margin-right: 10px;
  }
  
  .collection-image {
    width: 100px;
    height: 75px;
    border-radius: 6px;
    margin-right: 12px;
    flex-shrink: 0;
  }
  
  .collection-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    
    .collection-title {
      font-size: 16px;
      font-weight: 500;
      color: #333;
      margin-bottom: 6px;
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .collection-desc {
      font-size: 14px;
      color: #666;
      margin-bottom: 6px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .collection-meta {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      
      .collection-category {
        display: inline-block;
        background-color: rgba(57, 73, 171, 0.1);
        color: #3949ab;
        font-size: 12px;
        padding: 2px 6px;
        border-radius: 4px;
        margin-right: 10px;
      }
      
      .collection-date {
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
  padding: 80px 30px;
  
  .empty-image {
    width: 180px;
    height: 180px;
    margin-bottom: 20px;
  }
  
  .empty-text {
    font-size: 16px;
    color: #666;
    margin-bottom: 30px;
  }
  
  .explore-btn {
    background: linear-gradient(to right, #5c6bc0, #3949ab);
    color: #fff;
    padding: 10px 30px;
    border-radius: 25px;
    font-size: 16px;
    border: none;
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 54px;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px;
  z-index: 90;
  
  .select-all {
    display: flex;
    align-items: center;
    
    .select-text {
      font-size: 15px;
      color: #333;
      margin-left: 8px;
    }
  }
  
  .delete-btn {
    background-color: #ff5a5f;
    padding: 8px 20px;
    border-radius: 20px;
    
    &.disabled {
      background-color: #ccc;
    }
    
    .delete-text {
      color: #fff;
      font-size: 15px;
    }
  }
}
</style> 