<template>
	<view class="diary-container">
		<!-- 顶部操作栏 -->
		<view class="header-actions">
			<view class="back-btn" @click="goBack">
				<text class="back-icon">←</text>
			</view>
			<view class="action-btns">
				<view class="action-btn delete-btn" v-if="id" @click="confirmDelete">
					<text class="delete-icon">🗑️</text>
				</view>
			</view>
		</view>
		
		<!-- 心情选择区 -->
		<view class="mood-selection">
			<view class="mood-title">今天的心情</view>
			<view class="mood-icons">
				<mood-icon-base64 
					v-for="moodType in moodTypes" 
					:key="moodType" 
					:mood="moodType" 
					:selected="mood === moodType"
					:showLabel="true"
					:size="60"
					@click="selectMood(moodType)"
				/>
			</view>
		</view>
		
		<!-- 日记标题 -->
		<view class="diary-title-box" :class="{'diary-title-active': titleFocused}">
			<input
				class="diary-title-input"
				v-model="title"
				placeholder="请输入日记标题"
				@focus="titleFocused = true"
				@blur="titleFocused = false"
			/>
		</view>
		
		<!-- 日记内容 -->
		<view class="diary-content-box">
			<textarea
				class="diary-content-input"
				v-model="content"
				placeholder="今天发生了什么..."
				:auto-height="true"
				:show-confirm-bar="false"
			></textarea>
		</view>
		
		<!-- 添加图片按钮 -->
		<view class="add-image-btn" @click="showImageOptions">
			<text class="add-image-icon">📷</text>
			<text class="add-image-text">添加图片</text>
		</view>
		
		<!-- 图片预览区 -->
		<view class="image-preview" v-if="images.length > 0">
			<view class="image-preview-item" v-for="(image, index) in images" :key="index">
				<image class="preview-image" 
					:src="image" 
					mode="aspectFill" 
					@click="previewImage(index)" 
					:show-menu-by-longpress="true"
					@error="handleImageError(index)"
					@load="handleImageLoad(index)"
				></image>
				<view class="image-loading" v-if="imageStatus[index] === 'loading'">
					<text class="loading-text">加载中...</text>
				</view>
				<view class="image-error" v-if="imageStatus[index] === 'error'">
					<text class="error-text">加载失败</text>
					<view class="retry-btn" @click.stop="retryLoadImage(index)">重试</view>
					<text class="error-details">{{imageError[index] || ''}}</text>
				</view>
				<view class="delete-image-btn" @click.stop="deleteImage(index)">×</view>
			</view>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="diary-footer">
			<view class="diary-date">{{ formattedDate }}</view>
			<button class="diary-save-btn" @tap.stop="saveDiary" hover-class="diary-save-btn-hover">保存</button>
		</view>
	</view>
</template>

<script>
	import MoodIconBase64 from '@/components/mood-icon-base64.vue';
	import { getDiary, addDiary, updateDiary, deleteDiary, uploadDiaryImage } from '@/api/diary/index.js';
	import config from '@/config';
	
	export default {
		components: {
			MoodIconBase64
		},
		data() {
			return {
				id: null,
				title: '',
				content: '',
				mood: 'happy',
				date: new Date(),
				titleFocused: false,
				moodTypes: ['happy', 'neutral', 'sad', 'angry', 'anxious'],
				images: [], // 存储图片路径
				isNew: true,
				originalData: null, // 用于存储原始数据，比较是否有修改
				imageStatus: {}, // 用于存储图片加载状态
				showTokenExpired: false,
				isLoading: false,
				token: null,
				imageError: {} // 用于存储图片加载错误信息
			};
		},
		computed: {
			formattedDate() {
				const date = new Date(this.date);
				return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
			}
		},
		onLoad(options) {
			// 优先检查和恢复token
			this.checkAndRestoreToken();
			
			// 确保用户已登录
			const token = uni.getStorageSync('token');
			if (!token) {
				console.log('未找到有效token，请先登录');
				uni.showModal({
					title: '提示',
					content: '请先登录后再操作',
					showCancel: false,
					success: () => {
						uni.navigateTo({
							url: '/pages/login'
						});
					}
				});
				return;
			}
			
			console.log('使用token:', token.substring(0, 10) + '...');
			
			// 如果是编辑现有日记
			if (options.id) {
				this.id = options.id;
				this.isNew = false;
				this.loadDiary(options.id);
			}
		},
		methods: {
			goBack() {
				// 如果是新建日记且有内容，或者是编辑日记且有修改，才提示
				if (this.hasChanges()) {
					uni.showModal({
						title: '提示',
						content: '有未保存的内容，确定要离开吗？',
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
			
			// 检查是否有未保存的更改
			hasChanges() {
				// 如果是新建日记，检查是否有实质性内容
				if (this.isNew) {
					return this.title.trim() !== '' || this.content.trim() !== '' || this.images.length > 0;
				}
				
				// 如果是编辑现有日记，检查是否与原始数据不同
				if (!this.originalData) return false;
				
				// 检查标题、内容和心情是否有变化
				if (this.title.trim() !== this.originalData.title.trim() || 
					this.content.trim() !== this.originalData.content.trim() || 
					this.mood !== this.originalData.mood) {
					return true;
				}
				
				// 检查图片数组是否有变化
				if (this.images.length !== this.originalData.images.length) {
					return true;
				}
				
				// 检查每张图片是否相同（考虑URL格式差异）
				const normalizeUrl = (url) => {
					if (!url) return '';
					
					// 提取URL的关键部分进行比较
					// 移除可能的域名前缀和查询参数
					let normalizedUrl = url;
					
					// 处理blob URL和base64
					if (url.startsWith('blob:') || url.startsWith('data:')) {
						return url;
					}
					
					// 移除协议和域名部分
					const urlParts = url.split('/');
					if (urlParts.length > 2 && (url.startsWith('http://') || url.startsWith('https://'))) {
						// 只保留路径部分
						normalizedUrl = '/' + urlParts.slice(3).join('/');
					}
					
					// 移除查询参数
					return normalizedUrl.split('?')[0];
				};
				
				for (let i = 0; i < this.images.length; i++) {
					const currentUrl = normalizeUrl(this.images[i]);
					const originalUrl = normalizeUrl(this.originalData.images[i]);
					
					if (currentUrl !== originalUrl) {
						return true;
					}
				}
				
				return false;
			},
			selectMood(mood) {
				this.mood = mood;
			},
			loadDiary(id) {
				// 强制使用实际API而非模拟数据
				const apiCall = getDiary;
				
				uni.showLoading({
					title: '加载中...'
				});
				
				apiCall(id).then(res => {
					uni.hideLoading();
					console.log('获取日记响应:', JSON.stringify(res));
					
					if ((res.code === 0 || res.code === 200) && res.data) {
						const diary = res.data;
						// 数据适配，处理可能的字段名差异
						this.title = diary.diaryName || diary.name || '';
						this.content = diary.diaryContent || diary.details || diary.content || '';
						this.mood = diary.mood || 'happy';
						this.date = new Date(diary.createTime || diary.updateTime);
						
						// 处理图片数据
						this.processImageData(diary.images);
						
						// 保存原始数据，用于比较是否有修改
						this.originalData = {
							title: this.title,
							content: this.content,
							mood: this.mood,
							images: [...this.images]
						};
					} else {
						const errMsg = res.msg || '加载日记失败';
						console.error(errMsg, res);
						uni.showToast({
							title: errMsg,
							icon: 'none'
						});
					}
				}).catch(err => {
					uni.hideLoading();
					console.error('加载日记失败', err);
					uni.showToast({
						title: '加载日记失败',
						icon: 'none'
					});
				});
			},
			
			// 处理图片数据
			processImageData(imageData) {
				try {
					if (!imageData) {
						this.images = [];
						this.initImageStatus([]);
						return;
					}
					
					// 尝试将字符串解析为JSON数组
					let imageArray = [];
					
					if (typeof imageData === 'string') {
						try {
							// 尝试解析JSON字符串
							imageArray = JSON.parse(imageData);
							console.log('成功解析图片JSON数据:', imageArray);
						} catch (e) {
							console.error('解析图片JSON数据失败:', e);
							// 如果不是有效的JSON，可能是历史数据（单个URL或逗号分隔）
							imageArray = imageData.split(',').filter(img => img.trim());
							console.log('使用逗号分隔图片数据:', imageArray);
						}
					} else if (Array.isArray(imageData)) {
						// 如果已经是数组（可能来自前端状态），直接使用
						imageArray = imageData;
						console.log('图片数据已是数组格式:', imageArray);
					}
					
					console.log('从后端获取的原始图片数组:', imageArray);
					
					// 将后端存储的路径转换为前端可显示的完整URL
					this.images = imageArray.map(imagePath => {
						// 确保路径不为空且是字符串
						if (!imagePath || typeof imagePath !== 'string') return '';
						
						// 去除首尾引号和空格
						const path = imagePath.replace(/^["']|["']$/g, '').trim();
						console.log('处理图片路径:', path);
						
						// OSS图片处理 - 确保完整路径格式正确
						if (path.includes('oss-cn-beijing.aliyuncs.com')) {
							console.log('检测到OSS图片:', path);
							// 检查是否需要修复OSS URL
							if (path.includes('"') || path.includes("'")) {
								return path.replace(/['"]/g, '');
							}
							return path;
						}
						
						// 处理未完整的OSS路径
						if (path.includes('tbytebit') || (path.startsWith('diary/') && !path.startsWith('http'))) {
							console.log('检测到不完整的OSS路径:', path);
							const ossBaseUrl = 'https://tbytebit.oss-cn-beijing.aliyuncs.com/';
							// 移除可能的前缀
							const ossPath = path.replace(/^[/"']+diary\//, 'diary/').replace(/^[/"']+/, '');
							return ossBaseUrl + ossPath;
						}
						
						// 如果已经是完整URL或base64，直接使用
						if (path.startsWith('http') || path.startsWith('data:')) {
							return path;
						}
						
						// 确保相对路径以 / 开头
						const formattedPath = path.startsWith('/') ? path : '/' + path;
						
						// 处理带有/profile前缀的图片路径
						if (formattedPath.startsWith('/profile/')) {
							return config.baseUrl + formattedPath;
						}
						
						// 拼接baseUrl与profile路径
						return config.baseUrl + '/profile' + formattedPath;
						
					}).filter(img => img); // 过滤空图片
					
					console.log('处理后用于显示的图片数组:', this.images);
					
					// 初始化每张图片的加载状态
					this.initImageStatus(this.images);
				} catch (e) {
					console.error('解析或处理图片数据失败', e);
					this.images = [];
					this.initImageStatus([]);
				}
			},
			
			// 初始化图片加载状态
			initImageStatus(images) {
				const status = {};
				images.forEach((_, index) => {
					status[index] = 'loading';
				});
				this.imageStatus = status;
			},
			formValid() {
				console.log('检查表单有效性');
				console.log('标题:', this.title.trim() ? '有效' : '无效');
				console.log('内容:', this.content.trim() ? '有效' : '无效');
				
				if (!this.title || !this.title.trim()) {
					uni.showToast({
						title: '请输入日记标题',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.content || !this.content.trim()) {
					uni.showToast({
						title: '请输入日记内容',
						icon: 'none'
					});
					return false;
				}
				
				console.log('表单验证通过');
				return true;
			},
			saveDiary() {
				// 验证表单
				if (this.title.trim() === '') {
					uni.showToast({
						title: '请输入日记标题',
						icon: 'none'
					});
					return;
				}
				
				if (this.content.trim() === '') {
					uni.showToast({
						title: '请输入日记内容',
						icon: 'none'
					});
					return;
				}
				
				// 检查token是否有效
				const token = uni.getStorageSync('token');
				if (!token) {
					uni.showToast({
						title: '未找到登录凭证，请先登录',
						icon: 'none'
					});
					return;
				}
				
				// 显示加载中
				uni.showLoading({
					title: '保存中...'
				});
				
				// 准备提交的数据
				const diaryData = {
					title: this.title,
					content: this.content,
					mood: this.mood,
					// 过滤并整理图片路径
					images: this.processImagesForSaving(this.images)
				};
				
				console.log('保存的日记数据:', diaryData);
				
				// 区分新增还是更新
				if (this.isNew) {
					// 新增日记
					addDiary(diaryData)
						.then(res => {
							console.log('新增日记成功:', res);
							uni.hideLoading();
							
							if (res.code === 401) {
								uni.showToast({
									title: '登录已过期，请重新登录',
									icon: 'none'
								});
								return;
							}
							
							uni.showToast({
								title: '保存成功',
								icon: 'success',
								success: () => {
									// 返回日记列表页
									setTimeout(() => {
										uni.navigateBack();
									}, 1000);
								}
							});
						})
						.catch(err => {
							console.error('新增日记失败:', err);
							uni.hideLoading();
							
							if (err.code === 401) {
								uni.showToast({
									title: '登录已过期，请重新登录',
									icon: 'none'
								});
								return;
							}
							
							uni.showToast({
								title: '保存失败，请重试',
								icon: 'none'
							});
						});
				} else {
					// 更新已有日记
					updateDiary(this.id, diaryData)
						.then(res => {
							console.log('更新日记成功:', res);
							uni.hideLoading();
							
							if (res.code === 401) {
								uni.showToast({
									title: '登录已过期，请重新登录',
									icon: 'none'
								});
								return;
							}
							
							uni.showToast({
								title: '更新成功',
								icon: 'success',
								success: () => {
									// 返回日记列表页
									setTimeout(() => {
										uni.navigateBack();
									}, 1000);
								}
							});
						})
						.catch(err => {
							console.error('更新日记失败:', err);
							uni.hideLoading();
							
							if (err.code === 401) {
								uni.showToast({
									title: '登录已过期，请重新登录',
									icon: 'none'
								});
								return;
							}
							
							uni.showToast({
								title: '更新失败，请重试',
								icon: 'none'
							});
						});
				}
			},
			confirmDelete() {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这篇日记吗？此操作不可恢复。',
					confirmColor: '#FF3B30',
					success: (res) => {
						if (res.confirm) {
							this.deleteDiary();
						}
					}
				});
			},
			deleteDiary() {
				if (!this.id) return;
				
				// 强制使用实际API而非模拟数据
				const apiCall = deleteDiary;
				
				uni.showLoading({
					title: '删除中...'
				});
				
				apiCall(this.id).then(res => {
					uni.hideLoading();
					// 修改成功判断条件
					if (res.code === 0 || res.code === 200) { 
						uni.showToast({
							title: '删除成功',
							icon: 'success'
						});
						
						// 返回列表页
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					} else {
						// 显示后端返回的错误信息
						uni.showToast({
							title: res.msg || '删除失败',
							icon: 'none'
						});
					}
				}).catch(err => {
					uni.hideLoading();
					console.error('删除日记失败', err);
					uni.showToast({
						title: '删除失败',
						icon: 'none'
					});
				});
			},
			showImageOptions() {
				if (this.images.length >= 9) {
					uni.showToast({
						title: '最多添加9张图片',
						icon: 'none'
					});
					return;
				}
				
				uni.showActionSheet({
					itemList: ['从相册选择', '拍照'],
					success: (res) => {
						if (res.tapIndex === 0) {
							// 从相册选择
							this.chooseImage();
						} else if (res.tapIndex === 1) {
							// 拍照
							this.takePhoto();
						}
					}
				});
			},
			// 从相册选择图片
			chooseImage() {
				uni.chooseImage({
					count: 9 - this.images.length,
					sizeType: ['compressed'],
					sourceType: ['album'],
					success: (res) => {
						this.uploadImages(res.tempFilePaths);
					},
					fail: (err) => {
						console.error('选择图片失败', err);
					}
				});
			},
			// 拍照
			takePhoto() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['camera'],
					success: (res) => {
						this.uploadImages(res.tempFilePaths);
					},
					fail: (err) => {
						console.error('拍照失败', err);
					}
				});
			},
			// 上传图片到服务器
			uploadImages(tempFilePaths) {
				if (!tempFilePaths || tempFilePaths.length === 0) return;
				
				// 显示上传中状态
				uni.showLoading({
					title: '上传中...'
				});
				
				// 获取token
				const token = uni.getStorageSync('token');
				if (!token) {
					uni.hideLoading();
					uni.showToast({
						title: '未找到登录凭证，请先登录',
						icon: 'none'
					});
					return;
				}
				
				console.log('开始上传图片，使用token:', token.substring(0, 10) + '...');
				
				const uploadPromises = tempFilePaths.map((filePath, index) => {
					return new Promise((resolve, reject) => {
						const imageIndex = this.images.length + index;
						
						// 先添加一个占位图片
						this.images.push(filePath);
						this.imageStatus[imageIndex] = 'loading';
						
						// 使用封装好的上传图片API
						uploadDiaryImage(filePath)
							.then(result => {
								console.log('图片上传成功:', result);
								// 上传成功，替换临时路径为OSS路径
								const ossUrl = result.data.url;
								this.$set(this.images, imageIndex, ossUrl);
								this.$set(this.imageStatus, imageIndex, 'success');
								console.log('OSS图片URL:', ossUrl);
								resolve(ossUrl);
							})
							.catch(err => {
								console.error('图片上传失败:', err);
								this.$set(this.imageStatus, imageIndex, 'error');
								
								// 处理认证失败情况
								if (err && err.code === 401) {
									uni.showToast({
										title: '登录已过期，请重新登录',
										icon: 'none'
									});
								}
								
								reject(err.msg || '上传失败');
							});
					});
				});
				
				// 处理所有上传
				Promise.all(uploadPromises)
					.then(urls => {
						console.log('所有图片上传完成', urls);
						uni.hideLoading();
						uni.showToast({
							title: '上传成功',
							icon: 'success'
						});
					})
					.catch(err => {
						console.error('部分图片上传失败', err);
						uni.hideLoading();
						uni.showToast({
							title: '部分图片上传失败',
							icon: 'none'
						});
					});
			},
			previewImage(index) {
				if (this.imageStatus[index] === 'error') {
					this.retryLoadImage(index);
					return;
				}
				
				uni.previewImage({
					urls: this.images,
					current: this.images[index],
					loop: true,
					indicator: 'number'
				});
			},
			
			retryLoadImage(index) {
				this.$set(this.imageStatus, index, 'loading');
				
				// 获取当前图片URL
				const currentImage = this.images[index];
				console.log('尝试重新加载图片:', currentImage);
				
				// 测试资源是否存在
				this.testImageUrl(currentImage, index);
				
				// 尝试其他可能的URL格式
				if (currentImage && typeof currentImage === 'string') {
					const baseUrl = config.baseUrl;
					
					// 提取路径部分（如果有baseUrl）
					let path = currentImage;
					if (currentImage.includes(baseUrl)) {
						path = currentImage.replace(baseUrl, '');
					}
					
					// 构建多种可能的URL格式
					const testUrls = [];
					
					// 如果是相对路径，尝试不同的前缀组合
					if (!currentImage.startsWith('http')) {
						testUrls.push(
							baseUrl + (path.startsWith('/') ? '' : '/') + path,
							baseUrl + '/profile/' + path.replace(/^\//, '').replace(/^profile\//, ''),
							baseUrl + '/profile' + (path.startsWith('/') ? path : '/' + path)
						);
					} else {
						// 如果是完整URL，尝试不同的路径变体
						testUrls.push(
							baseUrl + path,
							baseUrl + '/profile' + path,
							baseUrl + '/profile/' + path.replace(/^\//, '')
						);
					}
					
					// 尝试所有可能的URL
					if (testUrls.length > 0) {
						console.log('尝试多种图片URL格式:', testUrls);
						this.testAlternativeUrls([...testUrls], index);
					}
				}
				
				// 强制重新加载图片
				const tempImage = this.images[index];
				this.$set(this.images, index, '');  // 先清空
				setTimeout(() => {
					this.$set(this.images, index, tempImage);  // 然后重新设置
				}, 100);
			},

			// 测试替代URL
			testAlternativeUrls(urls, index) {
				if (!urls || urls.length === 0) return;
				
				const url = urls.shift();
				if (!url) {
					if (urls.length > 0) {
						this.testAlternativeUrls(urls, index);
					}
					return;
				}

				// 避免重复测试相同的URL
				if (this.images[index] === url) {
					if (urls.length > 0) {
						this.testAlternativeUrls(urls, index);
					}
					return;
				}
				
				uni.request({
					url: url,
					method: 'HEAD',
					success: (res) => {
						if (res.statusCode === 200) {
							console.log('找到有效的替代URL:', url);
							// 替换当前图片URL
							this.$set(this.images, index, url);
							// 重置状态为加载中
							this.$set(this.imageStatus, index, 'loading');
						} else if (urls.length > 0) {
							// 继续测试下一个URL
							this.testAlternativeUrls(urls, index);
						}
					},
					fail: () => {
						if (urls.length > 0) {
							// 继续测试下一个URL
							this.testAlternativeUrls(urls, index);
						}
					},
					complete: () => {
						if (urls.length === 0 && this.imageStatus[index] === 'error') {
							console.log('所有替代URL均无效，保持错误状态');
						}
					}
				});
			},
			deleteImage(index) {
				uni.showModal({
					title: '提示',
					content: '确定要删除这张图片吗？',
					success: (res) => {
						if (res.confirm) {
							this.images.splice(index, 1);
						}
					}
				});
			},
			handleImageError(index) {
				console.error('图片加载失败:', this.images[index]);
				this.imageStatus[index] = 'error';
				
				// 诊断图片错误
				const img = this.images[index];
				let errorInfo = '';
				
				if (!img) {
					errorInfo = '图片URL为空';
				} else if (img.startsWith('data:')) {
					errorInfo = 'Base64图片格式可能不正确';
				} else if (img.startsWith('blob:')) {
					errorInfo = 'Blob URL无法直接访问';
				} else if (img.includes('oss-cn-beijing.aliyuncs.com')) {
					// OSS图片加载失败特殊处理
					errorInfo = 'OSS图片加载失败';
					
					// 检查是否有双引号问题
					if (img.includes('"')) {
						const fixedUrl = img.replace(/"/g, '');
						console.log('修复OSS图片URL中的引号:', fixedUrl);
						this.$set(this.images, index, fixedUrl);
						this.$set(this.imageStatus, index, 'loading');
						return;
					}
					
					// 编码URL中的特殊字符
					try {
						const parsed = new URL(img);
						const pathname = parsed.pathname;
						// 重新构建URL，确保路径部分正确编码
						const encodedPath = pathname.split('/').map(segment => 
							segment ? encodeURIComponent(decodeURIComponent(segment)) : ''
						).join('/');
						const fixedUrl = `${parsed.protocol}//${parsed.host}${encodedPath}${parsed.search}${parsed.hash}`;
						
						if (fixedUrl !== img) {
							console.log('修复编码后的OSS URL:', fixedUrl);
							this.$set(this.images, index, fixedUrl);
							this.$set(this.imageStatus, index, 'loading');
							return;
						}
					} catch (e) {
						console.error('尝试解析和编码URL失败:', e);
					}
				} else if (img.includes(config.baseUrl)) {
					errorInfo = '完整URL路径可能错误，服务器可能无法找到资源';
					
					// 自动尝试修复链接
					const path = img.replace(config.baseUrl, '');
					
					// 尝试不同的路径格式
					const testUrls = [
						config.baseUrl + '/profile' + (path.startsWith('/') ? path : '/' + path),
						config.baseUrl + path,
						config.baseUrl + '/profile/' + path.replace(/^\//, '').replace(/^profile\//, '')
					];
					
					console.log('自动尝试替代URL:', testUrls);
					this.testAlternativeUrls(testUrls, index);
					
					if (!path.includes('/profile')) {
						errorInfo += '，缺少/profile前缀';
					}
				} else {
					errorInfo = '图片路径格式不正确';
					
					// 检查是否为未处理的OSS路径
					if (img.includes('oss-cn-beijing') || img.includes('aliyuncs.com')) {
						errorInfo = '可能是未正确处理的OSS路径';
						// 尝试构建OSS URL
						const ossBaseUrl = 'https://tbytebit.oss-cn-beijing.aliyuncs.com/';
						// 提取可能的相对路径
						let ossPath = img;
						if (img.includes('/diary/')) {
							ossPath = 'diary/' + img.substring(img.indexOf('/diary/') + 7);
						}
						const ossUrl = ossBaseUrl + ossPath.replace(/^\//, '');
						console.log('尝试构建OSS URL:', ossUrl);
						this.$set(this.images, index, ossUrl);
						this.$set(this.imageStatus, index, 'loading');
						return;
					}
					
					// 尝试添加baseUrl
					const baseUrl = config.baseUrl;
					
					// 构建多种可能的URL格式进行尝试
					const testUrls = [
						baseUrl + (img.startsWith('/') ? '' : '/') + img,
						baseUrl + '/profile/' + (img.startsWith('/') ? img.substr(1) : img),
						baseUrl + '/profile' + (img.startsWith('/') ? img : '/' + img)
					];
					console.log('尝试构建完整URL:', testUrls);
					this.testAlternativeUrls([...testUrls], index);
				}
				
				console.error('图片加载诊断:', errorInfo, img);
				
				// 存储错误信息
				this.imageError[index] = errorInfo;
			},
			handleImageLoad(index) {
				this.imageStatus[index] = 'loaded';
			},
			// 测试图片URL是否有效
			testImageUrl(url, callback) {
				if (!url) {
					callback(false);
					return;
				}
				
				// 如果是base64或blob，认为是有效的
				if (url.startsWith('data:') || url.startsWith('blob:')) {
					callback(true);
					return;
				}
				
				console.log('[testImageUrl] 测试图片URL:', url);
				uni.request({
					url: url,
					method: 'HEAD',
					success: (res) => {
						const isValid = res.statusCode === 200;
						console.log(`[testImageUrl] URL测试结果: ${isValid ? '有效' : '无效'}, 状态码: ${res.statusCode}`);
						callback(isValid);
					},
					fail: (err) => {
						console.error('[testImageUrl] URL测试失败:', err);
						callback(false);
					}
				});
			},
			
			// 生成替代URL
			generateAlternateUrl(url) {
				if (!url) return '';
				
				const baseUrl = config.baseUrl;
				
				// 如果URL不包含baseUrl，添加它
				if (!url.includes(baseUrl)) {
					return baseUrl + (url.startsWith('/') ? '' : '/') + url;
				}
				
				// 如果URL不包含/profile但应该包含
				if (!url.includes('/profile')) {
					// 从baseUrl后提取路径
					const path = url.replace(baseUrl, '');
					return baseUrl + '/profile' + (path.startsWith('/') ? path : '/' + path);
				}
				
				return url;
			},
			
			// 处理图片URL，确保格式正确
			processImageUrl(url) {
				if (!url) return '';
				
				const baseUrl = config.baseUrl;
				console.log('处理图片URL:', url, '基础URL:', baseUrl);
				
				// 如果已经是完整URL或base64，直接使用
				if (url.startsWith('http') || url.startsWith('data:')) {
					return url;
				}
				
				// 处理带有/profile前缀的图片路径
				if (url.startsWith('/profile/') || url === '/profile') {
					return baseUrl + url;
				}
				
				// 检查是否包含profile但不是以/profile开头
				if (url.includes('profile/') && !url.startsWith('/profile/')) {
					return baseUrl + '/profile/' + url.substring(url.indexOf('profile/') + 8);
				}
				
				// 如果是其他路径，确保添加/profile前缀
				if (url.startsWith('/')) {
					return baseUrl + '/profile' + url;
				}
				
				// 其他情况，确保路径正确
				return baseUrl + '/profile/' + url;
			},
			// 检查并恢复token
			checkAndRestoreToken() {
				// 获取当前token
				let token = uni.getStorageSync('token');
				console.log('当前token状态:', token ? '已存在' : '不存在');
				
				if (!token) {
					console.log('尝试从localStorage恢复token...');
					try {
						// 尝试从localStorage中获取token（H5环境）
						token = localStorage.getItem('token') || '';
						if (token) {
							uni.setStorageSync('token', token);
							console.log('从localStorage恢复token成功, token长度:', token.length);
						} else {
							console.warn('未找到可用的token');
							
							// 检查是否有其他可能的存储位置
							const tokenFromStorage = uni.getStorageSync('Authorization');
							if (tokenFromStorage) {
								token = tokenFromStorage.replace('Bearer ', '');
								uni.setStorageSync('token', token);
								console.log('从Authorization存储中恢复token成功');
							}
						}
					} catch (e) {
						console.error('恢复token失败:', e);
					}
				} else {
					console.log('已有可用token, token长度:', token.length);
				}
				
				// 验证token格式
				if (token && (token.startsWith('Bearer ') || token.includes('.'))) {
					if (token.startsWith('Bearer ')) {
						// 如果token已经包含Bearer前缀，去除前缀
						token = token.replace('Bearer ', '');
						uni.setStorageSync('token', token);
						console.log('已处理token格式，移除Bearer前缀');
					}
					
					console.log('token格式合法，可能是有效JWT');
					this.token = token;
					return true;
				} else if (token) {
					console.warn('token格式可能不正确:', token.substring(0, 10) + '...');
				}
				
				return !!token;
			},
			// 新增方法 - 处理保存时的图片路径
			processImagesForSaving(images) {
				if (!images || !images.length) return [];
				
				// 过滤有效图片并处理路径
				return images.filter(img => {
					// 过滤空路径
					return img && typeof img === 'string' && img.trim().length > 0;
				}).map(img => {
					// 确保OSS图片使用完整URL
					if (img.includes('oss-cn-beijing.aliyuncs.com')) {
						// 移除可能的引号
						return img.replace(/['"]/g, '').trim();
					}
					return img;
				});
			}
		}
	}
</script>

<style>
	.diary-container {
		padding: 30rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}
	
	.header-actions {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
	}
	
	.back-btn, .action-btn {
		width: 70rpx;
		height: 70rpx;
		border-radius: 50%;
		background-color: #fff;
		display: flex;
		justify-content: center;
		align-items: center;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}
	
	.back-icon, .delete-icon {
		font-size: 36rpx;
		color: #666;
	}
	
	.delete-btn .delete-icon {
		color: #FF3B30;
	}
	
	.action-btns {
		display: flex;
		gap: 20rpx;
	}
	
	.mood-selection {
		margin-bottom: 40rpx;
	}
	
	.mood-title {
		font-size: 28rpx;
		color: #666;
		margin-bottom: 20rpx;
	}
	
	.mood-icons {
		display: flex;
		justify-content: space-between;
		padding: 0 40rpx;
	}
	
	.diary-title-box {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 20rpx 30rpx;
		margin-bottom: 30rpx;
		border: 2rpx solid #eee;
	}
	
	.diary-title-active {
		border-color: #007AFF;
		background-color: #f0f8ff;
	}
	
	.diary-title-input {
		font-size: 36rpx;
		font-weight: 500;
		color: #333;
		width: 100%;
	}
	
	.diary-content-box {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 40rpx;
		min-height: 300rpx;
		border: 2rpx solid #eee;
	}
	
	.diary-content-input {
		font-size: 30rpx;
		color: #333;
		width: 100%;
		line-height: 1.6;
	}
	
	.add-image-btn {
		display: flex;
		align-items: center;
		background-color: #fff;
		padding: 20rpx 30rpx;
		border-radius: 12rpx;
		margin-bottom: 30rpx;
		border: 2rpx solid #eee;
	}
	
	.add-image-icon {
		font-size: 36rpx;
		margin-right: 20rpx;
		color: #666;
	}
	
	.add-image-text {
		font-size: 28rpx;
		color: #666;
	}
	
	.image-preview {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
		margin-bottom: 40rpx;
	}
	
	.image-preview-item {
		position: relative;
		width: 200rpx;
		height: 200rpx;
		border-radius: 12rpx;
		overflow: hidden;
		border: 1px solid #eee;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
		background-color: #f5f5f5;
	}
	
	.preview-image {
		width: 100%;
		height: 100%;
		object-fit: cover;
	}
	
	/* 图片加载中状态 */
	.image-loading {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		background-color: rgba(245, 245, 245, 0.8);
		flex-direction: column;
	}
	
	.loading-text {
		font-size: 24rpx;
		color: #999;
	}
	
	/* 图片加载错误状态 */
	.image-error {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
		background-color: rgba(245, 245, 245, 0.8);
	}
	
	.error-text {
		font-size: 24rpx;
		color: #ff6b6b;
		margin-bottom: 10rpx;
	}
	
	.error-details {
		font-size: 20rpx;
		color: #999;
		margin-top: 6rpx;
		max-width: 90%;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
	
	.retry-btn {
		font-size: 24rpx;
		color: #007AFF;
		background-color: rgba(255, 255, 255, 0.8);
		padding: 6rpx 20rpx;
		border-radius: 20rpx;
	}
	
	.delete-image-btn {
		position: absolute;
		top: 10rpx;
		right: 10rpx;
		width: 40rpx;
		height: 40rpx;
		background-color: rgba(0, 0, 0, 0.6);
		color: #fff;
		font-size: 28rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		border-radius: 50%;
	}
	
	.diary-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-top: 60rpx;
	}
	
	.diary-date {
		font-size: 28rpx;
		color: #999;
	}
	
	.diary-save-btn {
		background-color: #007AFF;
		color: #fff;
		font-size: 28rpx;
		padding: 16rpx 40rpx;
		border-radius: 30rpx;
		position: relative;
		overflow: hidden;
		border: none;
		margin: 0;
		display: inline-block;
		line-height: normal;
		font-weight: normal;
	}
	
	.diary-save-btn::after {
		border: none;
	}
	
	.diary-save-btn-hover {
		background-color: #0060c8;
		opacity: 0.9;
	}
</style>