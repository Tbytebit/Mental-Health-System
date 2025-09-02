<template>
	<view>
		<view style="margin: 20px;font-weight: 600;line-height: 1.5em;font-size: 32rpx;">
			{{title}}
		</view>
		<view style="margin: 20px;">
			<view v-html="content"></view>
			 <!-- <rich-text nodes="{{content}}"></rich-text> -->
			<!-- <rich-text :nodes="content"></rich-text> -->
		</view>
	</view>
</template>

<script>
	import { getArticleInfo } from '@/api/maincode/advertisements' 
	export default {
		data() {
			return {
				title: '',
				content: '',
				articleId:""
			}
		},
		onLoad(options) {
			this.articleId = options.articleId 
			let then = this
			
			getArticleInfo(this.articleId).then(res =>{
				 
					then.title = res.data.title;
					then.content = res.data.content 
			}) 
		}
	}
</script>

<style scoped> 
	page {
		background-color: #ffffff;
	}

	p {
		text-indent: 2em;
		/* 或者使用20px */
	}

	.view-title {
		font-weight: bold;
	}

	.view-content {
		font-size: 26rpx;
		padding: 12px 5px 0;
		color: #333;
		line-height: 24px;
		font-weight: normal;
	}
</style>