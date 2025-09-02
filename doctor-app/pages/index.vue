<template>
	<view class="content">
		<!-- <image class="logo" src="@/static/logo.png"></image> -->
		<view class="text-area">
			<!-- 轮播图已移除 -->
		</view>
		<!-- 菜单 -->
		<view class="grid-body" style="margin-bottom: 10rpx;border-bottom: 6rpx solid #f1f1f1;">
			<uni-grid :column="4" :showBorder="false"> 
				<uni-grid-item  >
					<view class="grid-item-box" @click="notice">
						<uni-icons type="chat-filled" size="30"></uni-icons>
						<text class="text">公告</text>
					</view>
				</uni-grid-item>  
				<uni-grid-item>
					<view class="grid-item-box"  @click="room">
						<uni-icons type="shop-filled" size="30"></uni-icons>
						<text class="text">治疗屋</text>
					</view>
				</uni-grid-item>
				 
				<uni-grid-item>
					<view class="grid-item-box" @click="appointment">
						<uni-icons type="heart-filled" size="30"></uni-icons>
						<text class="text">心理咨询</text>
					</view>
				</uni-grid-item>
				<uni-grid-item>
					<view class="grid-item-box" @click="diary"> 
						<uni-icons type="compose" size="30"></uni-icons>
						<text class="text">心理日记</text>
					</view>
				</uni-grid-item>
				<uni-grid-item>
					<view class="grid-item-box" @click="forum"> 
						<uni-icons type="chatboxes-filled" size="30"></uni-icons>
						<text class="text">心理论坛</text>
					</view>
				</uni-grid-item>
				<uni-grid-item>
					<view class="grid-item-box" @click="moodScale"> 
						<uni-icons type="bars" size="30"></uni-icons>
						<text class="text">心理情绪量表</text>
					</view>
				</uni-grid-item>
				<uni-grid-item>
					<view class="grid-item-box" @click="resource">
						<image src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzMzMzMzMyI+PHBhdGggZD0iTTIxIDVjLTEuMTEtLjM1LTIuMzMtLjUtMy41LS41LTEuOTUgMC00LjA1LjQtNS41IDEuNS0xLjQ1LTEuMS0zLjU1LTEuNS01LjUtMS41UzIuNDUgNC45IDEgNnYxNC41QzIuNDUgMTkuOSA0LjU1IDE5IDYuNSAxOXMzLjk1LjkgNS41IDIuMVYxNi44MWMxLjc1LS45NyAzLjQ0LTEuNjcgNS41LTEuODEgMSAuMTQgMS45MS40NyAyLjUuODF2Ny4zNGMuNjEtLjM1IDEuMjgtLjYzIDItLjgxVjVabTAgMTNjLS44My40LS44My4zNy0yIC44M3YtMi44M2MuODUuMjQgMS44LjQzIDIgLjQzdjEuNTdabS04LTMuOTRjLjItLjA0IDEuNTktLjMgMi4wNC0uMzYuMjUuOC40LjE1LjY2LjI2VjE4Yy0xLjEtLjQzLTIuMjQtLjcyLTMuNS0uNzNsLS4wMS0uMjFjLjI4LS4wNS41My0uMS43Ni0uMTRsLjA1LTMuN1ptLTIuMDguMWMtLjQ2LjA5LTEuMTQuMjQtMS45Mi40N1YxOGMxLjMgMCAyLjU0LjMgMy42OC44MWwuMDItMy43OGMtLjQ5LS4xOS0xLjE5LS40IDEuNzQtLjM5TDEyLjkxIDE0bC0uOTkuMTZaTTYgMTNjLTEuOSAwLTMuOTEuNDktNCAuNzV2My40Yy42NC0uMTcgMS4yNy0uMjkgMS44NS0uMzYuMzQtLjExLjctLjIxIDEuMDYtLjI5LjMtLjA2LjYtLjEzLjktLjE5bC4xOS0uMDRWMTNaTTUgMTZjLS4zNiAwLS43MS4wMy0xLjA2LjA5QzMuMzMgMTYuMjIgMi42OSAxNi40IDIgMTYuNjhWNmMuNzYtLjU0IDIuOTgtMSA0LjUtMSAxLjUyIDAgMy43NC42IDQuNSAxLjJxLS4wMyA1LjA3IDAgMTQuMDVDLTkuNDYgMTkuMTcgNy4xIDE4LjUgNSAxNnptMTUgLjA4Yy0uNzYuMzYtMS4zNi42MS0yIC43MXMtMS40NS4xNS0yLjUuMTVjLS42NiAwLTEuMzMtLjA4LTItLjE4di0uOTNjLjYyLjExIDEuMjkuMTkgMiAuMTkuODQgMCAxLjU2LS4xMyAyLjExLS4yOXMxLjExLS4zMiAxLjU3LS41bC44Mi0uMzNWMTVoLjAxeiIvPjxjaXJjbGUgY3g9IjgiIGN5PSI5IiByPSIyIi8+PC9zdmc+" style="width: 30px; height: 30px;" />
						<text class="text">心理资料库</text>
					</view>
				</uni-grid-item>
				<uni-grid-item>
					<view class="grid-item-box" @click="aiChat">
						<image src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzMzMzMzMyI+PHBhdGggZD0iTTIwIDJIMTUuNHYyLjJsMS44IDEuOEgxNS4yYy0xLjQ0IDAtMi42OSAxLjA3LTIuOTIgMi41NEg5LjU1QzkuMzMgNy4wNyA4LjA3IDYgNi42IDZDNC45NSA2IDMuNiA3LjM1IDMuNiA5czEuMzUgMyAzIDNjMS40NyAwIDIuNzItMS4wNyAyLjk1LTIuNTNoMi43QzEyLjQ4IDEwLjkzIDEzLjc0IDEyIDE1LjIgMTJoMi4xbC0xLjggMS44VjE2SDIwYzEuMTEgMCAyLS44OSAyLTJWNGMwLTEuMTEtLjg5LTItMi0yem0wIDE0aC0yLjZ2LTIuNmwyLjYtMi42aDAgMCAwVjE2ek02LjYgMTBjLS41NSAwLTEtLjQ1LTEtMXMuNDUtMSAxLTEgMSAuNDUgMSAxLS40NSAxLTEgMXptMi44NS00LjI3YzEuMTktLjY5IDIuNTUtMS4wOCA0LS4wOEgxNCBWMkg4Ljg1Yy4zLS44My44LTEuNTQgMS40OS0ySDR2MTloNi43NWMtLjM4LS40Ni0uNjctLjk3LS44NS0xLjVINS4xNFY0LjloLjcxYy40MS41IDEuMDcuODEgMS44NC44M3YwaDB6Ii8+PC9zdmc+" style="width: 30px; height: 30px;" />
						<text class="text">AI助手</text>
					</view>
				</uni-grid-item>
<!-- 				<uni-grid-item>
					<view class="grid-item-box">
						<uni-icons type="calendar-filled" size="30"></uni-icons>
						<text class="text">心理评测</text>
					</view>
				</uni-grid-item> -->

 
				
			</uni-grid>
		</view>
		<!-- 列表 -->
		<uni-section  title="新闻咨询" type="line"></uni-section>
		<view >
			<view style="padding: 30rpx 20px;" v-for="(item, index) in newList" :key="index" @click="article(item)">
				<view style="height: 200px;margin: 10rpx;">
					<image :src="url + item.coverImage" style="width: 100%;height: 100%;border-radius: 6rpx;"></image>
				</view>
				<view class="title1">
					{{item.title}}
				</view> 
			</view>
		</view>
		
	
	</view>



</template>

<script> 
	import { getNewsArticlesList } from '@/api/maincode/news'
	export default {
		data() {
			return {
				newList:[],
				noData: false,
				queryParams: {
				    pageNum: 1,
				    pageSize: 10,
				},
				url:this.$fileUrl,
				menuList: [
					{
						icon: 'iconoir-music-double-note',
						text: '医学知识',
						page: '/pages/resource/index'
					},
					{
						icon: 'iconoir-chat-bubble',
						text: '聊天咨询',
						page: '/pages/chat/index'
					},
					{
						icon: 'iconoir-calendar',
						text: '工作日历',
						page: '/pages/work/calendar'
					},
					{
						icon: 'iconoir-edit-pencil',
						text: '心情记录',
						page: '/pages/mood/index'
					},
					{
						icon: 'iconoir-book',
						text: '日记',
						page: '/pages/diary/index'
					},
					{
						icon: 'iconoir-group',
						text: '论坛社区',
						page: '/pages/forum/index'
					}
				]
			}
		},
		methods: {
			article(item) {  
			  this.$tab.navigateTo(`/pages/common/article/index?articleId=${item.articleId}`)
			},
			appointment(){
				uni.switchTab({ url: './work/index' }); 
			},
			room(){
				uni.navigateTo({ url: './work/room' });
			},
			notice(){  
				uni.navigateTo({ url: './mine/notice/index' });
			},
			userinfo(){
				uni.navigateTo({ url: './mine/info/index' });
			},
			design(){ 
				uni.navigateTo({ url: './mine/setting/index' });
			},
			help(){ 
				uni.navigateTo({ url: './mine/help/index' });
			},
			diary(){
				uni.navigateTo({ url: './diary/index' });
			},
			getNews() { 
				getNewsArticlesList(this.queryParams).then(res => {
					if(res.rows == null || res.rows.length < this.queryParams.pageSize ){
						this.noData = true;
						if(this.queryParams.pageNum !=1 ){
							uni.showToast({   title: '已加载全部数据', duration: 2000 });
						}
					}
					this.newList.push(...res.rows)
				});
			},
			
			forum() {
				uni.switchTab({ url: '/pages/forum/index' });
			},
			moodScale(){
				uni.navigateTo({ url: './mood/index' });
			},
			
			resource() {
				try {
					// 预加载图标
					const resourceIcon = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0iIzMzMzMzMyI+PHBhdGggZD0iTTIxIDVjLTEuMTEtLjM1LTIuMzMtLjUtMy41LS41LTEuOTUgMC00LjA1LjQtNS41IDEuNS0xLjQ1LTEuMS0zLjU1LTEuNS01LjUtMS41UzIuNDUgNC45IDEgNnYxNC41QzIuNDUgMTkuOSA0LjU1IDE5IDYuNSAxOXMzLjk1LjkgNS41IDIuMVYxNi44MWMxLjc1LS45NyAzLjQ0LTEuNjcgNS41LTEuODEgMSAuMTQgMS45MS40NyAyLjUuODF2Ny4zNGMuNjEtLjM1IDEuMjgtLjYzIDItLjgxVjVabTAgMTNjLS44My40LS44My4zNy0yIC44M3YtMi44M2MuODUuMjQgMS44LjQzIDIgLjQzdjEuNTdabS04LTMuOTRjLjItLjA0IDEuNTktLjMgMi4wNC0uMzYuMjUuOC40LjE1LjY2LjI2VjE4Yy0xLjEtLjQzLTIuMjQtLjcyLTMuNS0uNzNsLS4wMS0uMjFjLjI4LS4wNS41My0uMS43Ni0uMTRsLjA1LTMuN1ptLTIuMDguMWMtLjQ2LjA5LTEuMTQuMjQtMS45Mi40N1YxOGMxLjMgMCAyLjU0LjMgMy42OC44MWwuMDItMy43OGMtLjQ5LS4xOS0xLjE5LS40IDEuNzQtLjM5TDEyLjkxIDE0bC0uOTkuMTZaTTYgMTNjLTEuOSAwLTMuOTEuNDktNCAuNzV2My40Yy42NC0uMTcgMS4yNy0uMjkgMS44NS0uMzYuMzQtLjExLjctLjIxIDEuMDYtLjI5LjMtLjA2LjYtLjEzLjktLjE5bC4xOS0uMDRWMTNaTTUgMTZjLS4zNiAwLS43MS4wMy0xLjA2LjA5QzMuMzMgMTYuMjIgMi42OSAxNi40IDIgMTYuNjhWNmMuNzYtLjU0IDIuOTgtMSA0LjUtMSAxLjUyIDAgMy43NC42IDQuNSAxLjJxLS4wMyA1LjA3IDAgMTQuMDVDLTkuNDYgMTkuMTcgNy4xIDE4LjUgNSAxNnptMTUgLjA4Yy0uNzYuMzYtMS4zNi42MS0yIC43MXMtMS40NS4xNS0yLjUuMTVjLS42NiAwLTEuMzMtLjA4LTItLjE4di0uOTNjLjYyLjExIDEuMjkuMTkgMiAuMTkuODQgMCAxLjU2LS4xMyAyLjExLS4yOXMxLjExLS4zMiAxLjU3LS41bC44Mi0uMzNWMTVoLjAxeiIvPjxjaXJjbGUgY3g9IjgiIGN5PSI5IiByPSIyIi8+PC9zdmc+';
					
					uni.navigateTo({
						url: '/pages/resource/index',
						success: () => {
							console.log('跳转到心理资料库成功');
						},
						fail: (err) => {
							console.error('跳转失败:', err);
							uni.showToast({
								title: '跳转失败，请重试',
								icon: 'none'
							});
						}
					});
				} catch (e) {
					console.error(e);
				}
			},
			aiChat(){
				uni.navigateTo({ url: '/pages/ai/index' });
			},
		},
		onReachBottom() {
			if (!this.noData) {
				this.queryParams.pageNum++;
				this.getNews(); // 获取的数据列表
			}
		},
		onLoad: function() {
			this.getNews();
		}
	}
</script>

<style>
	page {
		display: flex;
		flex-direction: column;
		box-sizing: border-box;
		background-color: #fff;
		min-height: 100%;
		height: auto;
	}
	
	view {
		font-size: 14px;
		line-height: inherit;
	}

	.container {
		display: flex;
		flex-direction: column;
	}

	.row {
		display: flex;
	}
	
	.text-area {
		justify-content: center;
	}
	
	.title1 {
		height: 36rpx;
		font-size: 26rpx;
		text-align: justify;
		color: rgba(0, 0, 0, 0.7);
		margin-top: 30rpx;
		font-weight: bold;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
	
	.grid-item-box {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 15px 0;
	}
	
	.text {
		font-size: 16px;
		margin-top: 10px;
		text-align: center;
	}
	
	.uni-footer-content {
		justify-content: center;
		align-items: center;
		padding-top: 20px;
		padding-bottom: 30px;
	}
	
	.uni-footer-content__navigate {
		display: flex;
		flex-direction: row;
		justify-content: space-around;
		margin-bottom: 15px;
	}
	
	.uni-footer-content__navigate-text {
		color: #fff;
		padding: 0 15px;
		font-size: 12px;
		text-decoration: none;
	}
	
	.uni-footer-content__copyright {
		color: #fff;
		font-size: 12px;
		line-height: 1.5;
	}
</style>