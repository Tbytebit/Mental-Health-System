<template>
	<view class="mood-icon-container" :class="{ selected }" @click="$emit('click')">
		<image class="mood-icon" :src="moodImageSrc" :style="{ width: size + 'rpx', height: size + 'rpx' }"></image>
		<text v-if="showLabel" class="mood-label">{{ moodLabel }}</text>
	</view>
</template>

<script>
	export default {
		name: "mood-icon-base64",
		props: {
			mood: {
				type: String,
				default: 'happy',
				validator: (value) => ['happy', 'neutral', 'sad', 'angry', 'anxious'].includes(value)
			},
			size: {
				type: Number,
				default: 40
			},
			showLabel: {
				type: Boolean,
				default: false
			},
			selected: {
				type: Boolean,
				default: false
			}
		},
		computed: {
			moodLabel() {
				const labels = {
					happy: '开心',
					neutral: '平静',
					sad: '难过',
					angry: '生气',
					anxious: '焦虑'
				};
				return labels[this.mood] || '开心';
			},
			moodImageSrc() {
				// 使用内联 Base64 编码的图像作为备用解决方案
				const base64Images = {
					// 黄色笑脸
					happy: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSIzMCIgY3k9IjMwIiByPSIyOCIgZmlsbD0iI0ZGREQwMCIvPjxwYXRoIGQ9Ik0zMCA1MkMxOC45NTQgNTIgMTAgNDMuMDQ2IDEwIDMySCAzMEg1MEMgNTAgNDMuMDQ2IDQxLjA0NiA1MiAzMCA1MloiIGZpbGw9IiM2NjRFMjciLz48cGF0aCBkPSJNNDIuNyAzNS40SDE3LjNDMTYuNTggMzUuNCAxNiAzNi4wNyAxNiAzNi45QzE2IDQ0LjIxIDIyLjI3IDUwIDMwIDUwQzM3LjczIDUwIDQ0IDQ0LjIxIDQ0IDM2LjlDNDQgMzYuMDcgNDMuNDIgMzUuNCA0Mi43IDM1LjRaIiBmaWxsPSJ3aGl0ZSIvPjxwYXRoIGQ9Ik0yNS43NSAzMEMyNS43NSAzMi43NiAyMi42MSAzNSAxOC43NSAzNUMxNC44OSAzNSAxMS43NSAzMi43NiAxMS43NSAzMEMxMS43NSAyNy4yNCAxNC44OSAyNSAxOC43NSAyNUMyMi42MSAyNSAyNS43NSAyNy4yNCAyNS43NSAzMFoiIGZpbGw9IiM2NjRFMjciLz48cGF0aCBkPSJNNDguMjUgMzBDNDguMjUgMzIuNzYgNDUuMTEgMzUgNDEuMjUgMzVDMzcuMzkgMzUgMzQuMjUgMzIuNzYgMzQuMjUgMzBDMzQuMjUgMjcuMjQgMzcuMzkgMjUgNDEuMjUgMjVDNDUuMTEgMjUgNDguMjUgMjcuMjQgNDguMjUgMzBaIiBmaWxsPSIjNjY0RTI3Ii8+PC9zdmc+',
					// 黄色平静脸
					neutral: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSIzMCIgY3k9IjMwIiByPSIyOCIgZmlsbD0iI0ZGREQwMCIvPjxwYXRoIGQ9Ik0yNS43NSAzMEMyNS43NSAzMi43NiAyMi42MSAzNSAxOC43NSAzNUMxNC44OSAzNSAxMS43NSAzMi43NiAxMS43NSAzMEMxMS43NSAyNy4yNCAxNC44OSAyNSAxOC43NSAyNUMyMi42MSAyNSAyNS43NSAyNy4yNCAyNS43NSAzMFoiIGZpbGw9IiM2NjRFMjciLz48cGF0aCBkPSJNNDguMjUgMzBDNDguMjUgMzIuNzYgNDUuMTEgMzUgNDEuMjUgMzVDMzcuMzkgMzUgMzQuMjUgMzIuNzYgMzQuMjUgMzBDMzQuMjUgMjcuMjQgMzcuMzkgMjUgNDEuMjUgMjVDNDUuMTEgMjUgNDguMjUgMjcuMjQgNDguMjUgMzBaIiBmaWxsPSIjNjY0RTI3Ii8+PHBhdGggZD0iTTE3IDQ0SDQzIiBzdHJva2U9IiM2NjRFMjciIHN0cm9rZS13aWR0aD0iNCIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIi8+PC9zdmc+',
					// 黄色悲伤脸
					sad: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSIzMCIgY3k9IjMwIiByPSIyOCIgZmlsbD0iI0ZGREQwMCIvPjxwYXRoIGQ9Ik0zMCA0NkMzOC4yODQgNDYgNDUgNDIuNDE4MyA0NSAzOEgxNUMxNSA0Mi40MTgzIDIxLjcxNiA0NiAzMCA0NloiIGZpbGw9IiM2NjRFMjciLz48cGF0aCBkPSJNMjAgMzRDMjAgMzIuODk1IDE5LjEwNSAzMiAxOCAzMkMxNi44OTUgMzIgMTYgMzIuODk1IDE2IDM0IiBzdHJva2U9IiM2NjRFMjciIHN0cm9rZS13aWR0aD0iMiIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIi8+PHBhdGggZD0iTTQ0IDM0QzQ0IDMyLjg5NSA0My4xMDUgMzIgNDIgMzJDNDAuODk1IDMyIDQwIDMyLjg5NSA0MCAzNCIgc3Ryb2tlPSIjNjY0RTI3IiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIvPjxwYXRoIGQ9Ik0yNS43NSAzMEMyNS43NSAzMi43NiAyMi42MSAzNSAxOC43NSAzNUMxNC44OSAzNSAxMS43NSAzMi43NiAxMS43NSAzMEMxMS43NSAyNy4yNCAxNC44OSAyNSAxOC43NSAyNUMyMi42MSAyNSAyNS43NSAyNy4yNCAyNS43NSAzMFoiIGZpbGw9IiM2NjRFMjciLz48cGF0aCBkPSJNNDguMjUgMzBDNDguMjUgMzIuNzYgNDUuMTEgMzUgNDEuMjUgMzVDMzcuMzkgMzUgMzQuMjUgMzIuNzYgMzQuMjUgMzBDMzQuMjUgMjcuMjQgMzcuMzkgMjUgNDEuMjUgMjVDNDUuMTEgMjUgNDguMjUgMjcuMjQgNDguMjUgMzBaIiBmaWxsPSIjNjY0RTI3Ii8+PC9zdmc+',
					// 黄色生气脸
					angry: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSIzMCIgY3k9IjMwIiByPSIyOCIgZmlsbD0iI0ZGREQwMCIvPjxwYXRoIGQ9Ik0zMCA0NkMzOC4yODQgNDYgNDUgNDIuNDE4MyA0NSAzOEgxNUMxNSA0Mi40MTgzIDIxLjcxNiA0NiAzMCA0NloiIGZpbGw9IiM2NjRFMjciLz48cGF0aCBkPSJNMTEgMjNMMjAgMjYiIHN0cm9rZT0iIzY2NEUyNyIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiLz48cGF0aCBkPSJNNDkgMjNMNDAgMjYiIHN0cm9rZT0iIzY2NEUyNyIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiLz48cGF0aCBkPSJNMjUuNzUgMzBDMjUuNzUgMzIuNzYgMjIuNjEgMzUgMTguNzUgMzVDMTQuODkgMzUgMTEuNzUgMzIuNzYgMTEuNzUgMzBDMTEuNzUgMjcuMjQgMTQuODkgMjUgMTguNzUgMjVDMjIuNjEgMjUgMjUuNzUgMjcuMjQgMjUuNzUgMzBaIiBmaWxsPSIjNjY0RTI3Ii8+PHBhdGggZD0iTTQ4LjI1IDMwQzQ4LjI1IDMyLjc2IDQ1LjExIDM1IDQxLjI1IDM1QzM3LjM5IDM1IDM0LjI1IDMyLjc2IDM0LjI1IDMwQzM0LjI1IDI3LjI0IDM3LjM5IDI1IDQxLjI1IDI1QzQ1LjExIDI1IDQ4LjI1IDI3LjI0IDQ4LjI1IDMwWiIgZmlsbD0iIzY2NEUyNyIvPjwvc3ZnPg==',
					// 黄色焦虑脸
					anxious: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSIzMCIgY3k9IjMwIiByPSIyOCIgZmlsbD0iI0ZGREQwMCIvPjxwYXRoIGQ9Ik0yMCAzOEMyMCAzOCAyNC41IDQyIDMwIDQyQzM1LjUgNDIgNDAgMzggNDAgMzgiIHN0cm9rZT0iIzY2NEUyNyIgc3Ryb2tlLXdpZHRoPSIzIiBzdHJva2UtbGluZWNhcD0icm91bmQiLz48cGF0aCBkPSJNMjEgMjRMMTggMjJNMjUgMjJMMjMgMTkiIHN0cm9rZT0iIzY2NEUyNyIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiLz48cGF0aCBkPSJNMzkgMjRMNDIgMjJNMzUgMjJMMzcgMTkiIHN0cm9rZT0iIzY2NEUyNyIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiLz48cGF0aCBkPSJNMjUuNzUgMzBDMjUuNzUgMzIuNzYgMjIuNjEgMzUgMTguNzUgMzVDMTQuODkgMzUgMTEuNzUgMzIuNzYgMTEuNzUgMzBDMTEuNzUgMjcuMjQgMTQuODkgMjUgMTguNzUgMjVDMjIuNjEgMjUgMjUuNzUgMjcuMjQgMjUuNzUgMzBaIiBmaWxsPSIjNjY0RTI3Ii8+PHBhdGggZD0iTTQ4LjI1IDMwQzQ4LjI1IDMyLjc2IDQ1LjExIDM1IDQxLjI1IDM1QzM3LjM5IDM1IDM0LjI1IDMyLjc2IDM0LjI1IDMwQzM0LjI1IDI3LjI0IDM3LjM5IDI1IDQxLjI1IDI1QzQ1LjExIDI1IDQ4LjI1IDI3LjI0IDQ4LjI1IDMwWiIgZmlsbD0iIzY2NEUyNyIvPjwvc3ZnPg=='
				};
				
				return base64Images[this.mood] || base64Images.happy;
			}
		}
	}
</script>

<style>
	.mood-icon-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		opacity: 0.6;
		transition: all 0.2s ease;
		cursor: pointer;
	}
	
	.selected {
		opacity: 1;
		transform: scale(1.1);
	}
	
	.mood-icon {
		border-radius: 50%;
		box-shadow: 0 0 0 transparent;
		transition: box-shadow 0.3s ease;
	}
	
	.selected .mood-icon {
		box-shadow: 0 0 10rpx rgba(0, 122, 255, 0.5);
	}
	
	.mood-label {
		font-size: 24rpx;
		color: #666;
		margin-top: 8rpx;
	}
	
	.selected .mood-label {
		color: #007AFF;
		font-weight: bold;
	}
</style>