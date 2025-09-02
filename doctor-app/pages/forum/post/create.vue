<template>
	<view class="post-create-container">
		<!-- 顶部导航栏 -->
		<view class="navbar">
			<view class="navbar-left" @click="goBack">
				<uni-icons type="back" size="24" color="#333"></uni-icons>
			</view>
			<view class="navbar-title">发布帖子</view>
			<view class="navbar-right" @click="publishPost">
				<text :class="['publish-text', canPublish ? 'active' : '']">发布</text>
			</view>
		</view>
		
		<!-- 内容区域 -->
		<view class="content-section">
			<!-- 标题输入 -->
			<view class="title-input-box">
				<input 
					type="text" 
					v-model="postTitle" 
					placeholder="请输入标题（必填）" 
					maxlength="50"
					class="title-input"
				/>
				<text class="title-count">{{ postTitle.length }}/50</text>
			</view>
			
			<!-- 内容输入 -->
			<view class="content-input-box">
				<textarea 
					v-model="postContent" 
					placeholder="分享你的心情、经验或问题..." 
					class="content-input"
					maxlength="2000"
					:auto-height="true"
				></textarea>
			</view>
			
			<!-- 图片上传 -->
			<view class="image-upload-section">
				<view class="image-list" v-if="imageList.length > 0">
					<view 
						class="image-item" 
						v-for="(image, index) in imageList" 
						:key="index"
					>
						<image :src="image" mode="aspectFill" class="preview-image"></image>
						<view class="delete-icon" @click="removeImage(index)">
							<uni-icons type="close" size="20" color="#fff"></uni-icons>
						</view>
					</view>
				</view>
				
				<view class="upload-button" @click="chooseImage" v-if="imageList.length < 9">
					<uni-icons type="camera" size="24" color="#999"></uni-icons>
					<text class="upload-text">添加图片</text>
					<text class="upload-hint">{{ imageList.length }}/9</text>
				</view>
			</view>
			
			<!-- 分类选择 -->
			<view class="category-section">
				<view class="section-title">选择分类</view>
				<view class="category-list">
					<view 
						v-for="category in categories" 
						:key="category.id"
						class="category-item"
						:class="{active: selectedCategory === category.id}"
						@click="selectCategory(category.id)"
					>
						{{ category.name }}
					</view>
				</view>
			</view>
			
			<!-- 匿名选项 -->
			<view class="anonymous-section">
				<view class="anonymous-option">
					<text>匿名发布</text>
					<switch 
						:checked="isAnonymous" 
						@change="toggleAnonymous" 
						color="#007AFF"
						style="transform:scale(0.8)"
					/>
				</view>
				<text class="anonymous-hint">匿名发布后，其他用户将不会看到你的用户名和头像</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { createForumPost, getForumCategories, uploadForumImage } from '@/api/forum/forum.js';

	export default {
		data() {
			return {
				postTitle: '',
				postContent: '',
				imageList: [],
				uploadedImages: [], // 保存上传后的OSS图片链接
				selectedCategory: null,
				isAnonymous: false,
				categories: [],
				uploading: false
			};
		},
		computed: {
			canPublish() {
				// 标题必填，内容必填，分类必选
				return this.postTitle.trim() && this.postContent.trim() && this.selectedCategory;
			}
		},
		onLoad() {
			// 加载分类
			this.loadCategories();
		},
		methods: {
			loadCategories() {
				getForumCategories().then(response => {
					const { data } = response;
					if (data && data.length > 0) {
						this.categories = data;
						console.log('成功加载分类:', this.categories);
					} else {
						console.warn('返回的分类数据为空');
						// 使用默认分类
						this.loadDefaultCategories();
					}
				}).catch(error => {
					console.error('获取分类失败', error);
					// 使用默认分类
					this.loadDefaultCategories();
				});
			},
			loadDefaultCategories() {
				this.categories = [
					{ id: "anxiety", name: '焦虑情绪' },
					{ id: "depression", name: '抑郁症状' },
					{ id: "stress", name: '压力管理' },
					{ id: "relationship", name: '人际关系' },
					{ id: "career", name: '职业发展' },
					{ id: "family", name: '家庭问题' },
					{ id: "other", name: '其他话题' }
				];
				console.log('使用默认分类:', this.categories);
			},
			goBack() {
				if (this.postTitle || this.postContent || this.imageList.length > 0) {
					uni.showModal({
						title: '提示',
						content: '是否放弃编辑？',
						success: (res) => {
							if (res.confirm) {
								uni.navigateBack();
							}
						}
					});
				} else {
					uni.navigateBack();
				}
			},
			chooseImage() {
				uni.chooseImage({
					count: 9 - this.imageList.length,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						this.imageList = [...this.imageList, ...res.tempFilePaths];
					}
				});
			},
			removeImage(index) {
				this.imageList.splice(index, 1);
			},
			selectCategory(categoryId) {
				console.log('选择分类:', categoryId);
				this.selectedCategory = categoryId;
			},
			toggleAnonymous(e) {
				this.isAnonymous = e.detail.value;
			},
			publishPost() {
				if (!this.canPublish) {
					if (!this.postTitle.trim()) {
						uni.showToast({
							title: '请输入标题',
							icon: 'none'
						});
					} else if (!this.postContent.trim()) {
						uni.showToast({
							title: '请输入内容',
							icon: 'none'
						});
					} else if (!this.selectedCategory) {
						uni.showToast({
							title: '请选择分类',
							icon: 'none'
						});
					}
					return;
				}
				
				if (this.uploading) {
					console.log('已经在上传中，请勿重复点击');
					return;
				}
				
				// 显示发布中提示
				uni.showLoading({
					title: '发布中...'
				});
				
				this.uploading = true;
				console.log('开始发布流程，有图片数量：', this.imageList.length);
				
				// 先上传图片（如果有）
				if (this.imageList.length > 0) {
					this.uploadImages().then(imageUrls => {
						console.log('图片上传成功，获取到URLs:', imageUrls);
						// 图片上传成功后发布帖子
						this.submitPost(imageUrls);
					}).catch(error => {
						uni.hideLoading();
						this.uploading = false;
						console.error('图片上传失败', error);
						uni.showToast({
							title: '图片上传失败',
							icon: 'none'
						});
					});
				} else {
					console.log('没有图片，直接发布帖子');
					// 没有图片，直接发布帖子
					this.submitPost([]);
				}
			},
			// 上传图片
			uploadImages() {
				return new Promise((resolve, reject) => {
					console.log('准备上传图片，共', this.imageList.length, '张');
					uni.showLoading({
						title: '上传图片中...',
						mask: true
					});
					
					const uploadPromises = this.imageList.map(imagePath => {
						return this.uploadSingleImage(imagePath);
					});
					
					Promise.all(uploadPromises)
						.then(results => {
							console.log('所有图片上传完成:', results);
							uni.hideLoading();
							
							// 过滤并获取所有成功上传的图片URL
							const imageUrls = results
								.filter(res => res && res.imageUrl)
								.map(res => res.imageUrl);
							
							console.log('成功上传的图片URL:', imageUrls);
							this.uploadedImages = imageUrls;
							resolve(imageUrls);
						})
						.catch(error => {
							console.error('图片上传过程中出错:', error);
							uni.hideLoading();
							reject(error);
						});
				});
			},
			
			// 上传单张图片
			uploadSingleImage(filePath) {
				return new Promise((resolve, reject) => {
					console.log('开始上传图片:', filePath);
					
					uploadForumImage(filePath)
						.then(response => {
							console.log('图片上传成功，响应:', response);
							
							if (response.code === 200 || response.code === 0) {
								// 成功获取图片URL
								let imageUrl = '';
								if (response.data && response.data.url) {
									imageUrl = response.data.url;
								} else if (response.url) {
									imageUrl = response.url;
								} else if (response.data && typeof response.data === 'string') {
									imageUrl = response.data;
								} else if (typeof response === 'string') {
									imageUrl = response;
								}
								
								if (imageUrl) {
									console.log('解析得到图片URL:', imageUrl);
									resolve({ success: true, imageUrl: imageUrl });
								} else {
									console.error('上传成功但未获取到图片URL');
									reject(new Error('未获取到图片URL'));
								}
							} else {
								console.error('上传失败:', response);
								reject(new Error(response.msg || '图片上传失败'));
							}
						})
						.catch(error => {
							console.error('上传请求失败:', error);
							reject(error);
						});
				});
			},
			
			// 提交帖子数据
			submitPost(imageUrls) {
				// 获取当前用户信息
				const userInfo = uni.getStorageSync('userInfo') || {};
				console.log('获取到的用户信息:', userInfo);
				
				// 如果没有用户信息，需要先登录
				if (!userInfo.id) {
					uni.hideLoading();
					this.uploading = false;
					uni.showModal({
						title: '提示',
						content: '请先登录后再发布帖子',
						success: function(res) {
							if (res.confirm) {
								uni.navigateTo({
									url: '/pages/login'
								});
							}
						}
					});
					return;
				}
				
				// 查找选中分类的完整信息
				const selectedCategoryObj = this.categories.find(c => c.id === this.selectedCategory) || {};
				console.log('选中的分类:', selectedCategoryObj);
				
				// 构建帖子数据
				const postData = {
					title: this.postTitle.trim(),
					content: this.postContent.trim(),
					categoryId: this.selectedCategory,
					categoryName: selectedCategoryObj.name || '未分类',
					userId: userInfo.id, // 不再使用默认值
					username: userInfo.username,
					avatar: userInfo.avatar,
					isAnonymous: this.isAnonymous,
					images: imageUrls
				};
				
				console.log('提交帖子数据:', JSON.stringify(postData));
				
				// 调用API发布帖子
				createForumPost(postData).then(response => {
					uni.hideLoading();
					this.uploading = false;
					
					console.log('发布帖子响应:', JSON.stringify(response));
					
					if (!response) {
						this.showErrorToast('服务器未返回响应');
						return;
					}
					
					// 处理直接返回 {success:true, postId:xxx} 的情况 (不带code)
					if (response.success === true && response.postId) {
						console.log('服务器直接返回成功响应:', response);
						this.handlePublishSuccess({
							success: true,
							postId: response.postId
						});
						return;
					}
					
					// 处理响应
					const code = response.code || 500;
					const responseData = response.data || {};
					
					console.log('处理标准响应格式 - 状态码:', code, '数据:', responseData);
					
					// 无论返回什么状态码，只要data中的success为true就认为成功
					if (responseData.success === true) {
						this.handlePublishSuccess(responseData);
					} else {
						// 发布失败
						this.showErrorToast(responseData.message || '发布失败，请重试');
					}
				}).catch(error => {
					uni.hideLoading();
					this.uploading = false;
					console.error('发布帖子失败:', error);
					this.showErrorToast(error.message || '发布失败，请重试');
				});
			},
			// 处理发布成功
			handlePublishSuccess(responseData) {
				// 设置一个标记，表示需要刷新列表
				try {
					uni.setStorageSync('forum_need_refresh', 'true');
					console.log('设置刷新标记成功');
				} catch (e) {
					console.error('设置刷新标记失败:', e);
				}
				
				uni.showToast({
					title: '发布成功',
					icon: 'success',
					duration: 2000,
					success: () => {
						setTimeout(() => {
							// 获取postId
							const postId = responseData.postId;
							console.log('发布成功，获取到帖子ID:', postId);
							
							// 刷新首页必须的处理
							uni.setStorageSync('forum_latest_created_post_id', postId);
							uni.setStorageSync('forum_last_refresh_time', new Date().getTime());
							
							// 跳转到帖子详情页或返回论坛列表
							if (postId) {
								// 选择跳转到详情页还是回到列表，此处选择直接返回论坛列表
								uni.switchTab({
									url: '/pages/forum/index'
								});
								/* 
								// 如果希望跳到详情页，可以使用下面的代码
								uni.redirectTo({
									url: `/pages/forum/post/detail?id=${postId}`
								});
								*/
							} else {
								// 如果没有返回帖子ID，跳回论坛首页
								uni.switchTab({
									url: '/pages/forum/index'
								});
							}
						}, 1500);
					}
				});
			},
			// 显示错误提示
			showErrorToast(message) {
				uni.showToast({
					title: message || '发布失败，请重试',
					icon: 'none',
					duration: 2000
				});
			}
		}
	}
</script>

<style>
	.post-create-container {
		background-color: #f8f8f8;
		min-height: 100vh;
	}
	
	.navbar {
		display: flex;
		justify-content: space-between;
		align-items: center;
		height: 90rpx;
		background-color: #fff;
		padding: 0 30rpx;
		border-bottom: 1rpx solid #eee;
	}
	
	.navbar-left, .navbar-right {
		width: 120rpx;
		display: flex;
		align-items: center;
	}
	
	.navbar-right {
		justify-content: flex-end;
	}
	
	.navbar-title {
		font-size: 32rpx;
		font-weight: 500;
		color: #333;
	}
	
	.publish-text {
		font-size: 30rpx;
		color: #999;
	}
	
	.publish-text.active {
		color: #007AFF;
		font-weight: 500;
	}
	
	.content-section {
		padding: 30rpx;
	}
	
	.title-input-box {
		position: relative;
		background-color: #fff;
		border-radius: 12rpx;
		padding: 20rpx 30rpx;
		margin-bottom: 20rpx;
	}
	
	.title-input {
		font-size: 32rpx;
		font-weight: 500;
		color: #333;
		width: 100%;
		height: 80rpx;
		line-height: 80rpx;
	}
	
	.title-count {
		position: absolute;
		right: 30rpx;
		bottom: 20rpx;
		font-size: 24rpx;
		color: #999;
	}
	
	.content-input-box {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		min-height: 300rpx;
	}
	
	.content-input {
		font-size: 30rpx;
		color: #333;
		width: 100%;
		line-height: 1.6;
		min-height: 240rpx;
	}
	
	.image-upload-section {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}
	
	.image-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
		margin-bottom: 20rpx;
	}
	
	.image-item {
		position: relative;
		width: calc((100% - 40rpx) / 3);
		height: 200rpx;
	}
	
	.preview-image {
		width: 100%;
		height: 100%;
		border-radius: 8rpx;
	}
	
	.delete-icon {
		position: absolute;
		top: -10rpx;
		right: -10rpx;
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		background-color: rgba(0, 0, 0, 0.6);
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.upload-button {
		width: calc((100% - 40rpx) / 3);
		height: 200rpx;
		border-radius: 8rpx;
		background-color: #f5f5f5;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		border: 1rpx dashed #ddd;
	}
	
	.upload-text {
		font-size: 26rpx;
		color: #666;
		margin-top: 10rpx;
	}
	
	.upload-hint {
		font-size: 22rpx;
		color: #999;
		margin-top: 6rpx;
	}
	
	.category-section {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}
	
	.section-title {
		font-size: 30rpx;
		font-weight: 500;
		color: #333;
		margin-bottom: 20rpx;
	}
	
	.category-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}
	
	.category-item {
		padding: 15rpx 30rpx;
		border-radius: 30rpx;
		font-size: 28rpx;
		color: #666;
		background-color: #f5f5f5;
		transition: all 0.3s;
	}
	
	.category-item.active {
		background-color: #007AFF;
		color: #fff;
	}
	
	.anonymous-section {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
	}
	
	.anonymous-option {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}
	
	.anonymous-option text {
		font-size: 30rpx;
		color: #333;
	}
	
	.anonymous-hint {
		font-size: 24rpx;
		color: #999;
	}
</style>