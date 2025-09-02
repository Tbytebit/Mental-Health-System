<template>
	<view class="mood-icon" :class="{ 'mood-selected': selected }">
		<image v-if="moodType === 'happy'" class="icon" src="/static/images/mood/happy.png"></image>
		<image v-else-if="moodType === 'neutral'" class="icon" src="/static/images/mood/neutral.png"></image>
		<image v-else-if="moodType === 'sad'" class="icon" src="/static/images/mood/sad.png"></image>
		<image v-else-if="moodType === 'angry'" class="icon" src="/static/images/mood/angry.png"></image>
		<image v-else-if="moodType === 'anxious'" class="icon" src="/static/images/mood/anxious.png"></image>
		<image v-else class="icon" src="/static/images/mood/happy.png"></image>
		<text v-if="showLabel" class="mood-text">{{ moodLabel }}</text>
	</view>
</template>

<script>
	export default {
		name: "mood-icon",
		props: {
			moodType: {
				type: String,
				default: 'happy',
				validator: (value) => {
					return ['happy', 'neutral', 'sad', 'angry', 'anxious'].includes(value);
				}
			},
			showLabel: {
				type: Boolean,
				default: false
			},
			selected: {
				type: Boolean,
				default: false
			},
			size: {
				type: String,
				default: 'medium', // small, medium, large
				validator: (value) => {
					return ['small', 'medium', 'large'].includes(value);
				}
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
				
				return labels[this.moodType] || '开心';
			}
		}
	}
</script>

<style>
	.mood-icon {
		display: flex;
		flex-direction: column;
		align-items: center;
		opacity: 0.6;
	}
	
	.mood-selected {
		opacity: 1;
	}
	
	.icon {
		width: 60rpx;
		height: 60rpx;
	}
	
	.mood-text {
		font-size: 24rpx;
		color: #666;
		margin-top: 8rpx;
	}
</style> 