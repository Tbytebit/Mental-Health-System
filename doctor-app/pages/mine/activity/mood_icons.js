/**
 * 心理日记心情图标和空状态图像
 * 这些心情图标需要转换为png并保存到 static/images/mood 目录下
 * 空状态图像需要保存到 static/images 目录下
 */

// 以下是用于生成心情图标的SVG代码
const moodIcons = {
  // 开心图标 - 保存为 static/images/mood/happy.png
  happy: `<svg width="60" height="60" viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="30" cy="30" r="28" fill="#FFDD67"/>
    <path d="M30 52C18.954 52 10 43.046 10 32H50C50 43.046 41.046 52 30 52Z" fill="#664E27"/>
    <path d="M42.7 35.4H17.3C16.58 35.4 16 36.07 16 36.9C16 44.21 22.27 50 30 50C37.73 50 44 44.21 44 36.9C44 36.07 43.42 35.4 42.7 35.4Z" fill="white"/>
    <path d="M25.75 30C25.75 32.76 22.61 35 18.75 35C14.89 35 11.75 32.76 11.75 30C11.75 27.24 14.89 25 18.75 25C22.61 25 25.75 27.24 25.75 30Z" fill="#664E27"/>
    <path d="M48.25 30C48.25 32.76 45.11 35 41.25 35C37.39 35 34.25 32.76 34.25 30C34.25 27.24 37.39 25 41.25 25C45.11 25 48.25 27.24 48.25 30Z" fill="#664E27"/>
  </svg>`,
  
  // 平静图标 - 保存为 static/images/mood/neutral.png
  neutral: `<svg width="60" height="60" viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="30" cy="30" r="28" fill="#FFDD67"/>
    <path d="M25.75 30C25.75 32.76 22.61 35 18.75 35C14.89 35 11.75 32.76 11.75 30C11.75 27.24 14.89 25 18.75 25C22.61 25 25.75 27.24 25.75 30Z" fill="#664E27"/>
    <path d="M48.25 30C48.25 32.76 45.11 35 41.25 35C37.39 35 34.25 32.76 34.25 30C34.25 27.24 37.39 25 41.25 25C45.11 25 48.25 27.24 48.25 30Z" fill="#664E27"/>
    <path d="M17 44H43" stroke="#664E27" stroke-width="4" stroke-linecap="round"/>
  </svg>`,
  
  // 难过图标 - 保存为 static/images/mood/sad.png
  sad: `<svg width="60" height="60" viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="30" cy="30" r="28" fill="#FFDD67"/>
    <path d="M30 46C38.284 46 45 42.4183 45 38H15C15 42.4183 21.716 46 30 46Z" fill="#664E27"/>
    <path d="M20 34C20 32.895 19.105 32 18 32C16.895 32 16 32.895 16 34" stroke="#664E27" stroke-width="2" stroke-linecap="round"/>
    <path d="M44 34C44 32.895 43.105 32 42 32C40.895 32 40 32.895 40 34" stroke="#664E27" stroke-width="2" stroke-linecap="round"/>
    <path d="M25.75 30C25.75 32.76 22.61 35 18.75 35C14.89 35 11.75 32.76 11.75 30C11.75 27.24 14.89 25 18.75 25C22.61 25 25.75 27.24 25.75 30Z" fill="#664E27"/>
    <path d="M48.25 30C48.25 32.76 45.11 35 41.25 35C37.39 35 34.25 32.76 34.25 30C34.25 27.24 37.39 25 41.25 25C45.11 25 48.25 27.24 48.25 30Z" fill="#664E27"/>
  </svg>`,
  
  // 生气图标 - 保存为 static/images/mood/angry.png
  angry: `<svg width="60" height="60" viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="30" cy="30" r="28" fill="#FFDD67"/>
    <path d="M30 46C38.284 46 45 42.4183 45 38H15C15 42.4183 21.716 46 30 46Z" fill="#664E27"/>
    <path d="M11 23L20 26" stroke="#664E27" stroke-width="2" stroke-linecap="round"/>
    <path d="M49 23L40 26" stroke="#664E27" stroke-width="2" stroke-linecap="round"/>
    <path d="M25.75 30C25.75 32.76 22.61 35 18.75 35C14.89 35 11.75 32.76 11.75 30C11.75 27.24 14.89 25 18.75 25C22.61 25 25.75 27.24 25.75 30Z" fill="#664E27"/>
    <path d="M48.25 30C48.25 32.76 45.11 35 41.25 35C37.39 35 34.25 32.76 34.25 30C34.25 27.24 37.39 25 41.25 25C45.11 25 48.25 27.24 48.25 30Z" fill="#664E27"/>
  </svg>`,
  
  // 焦虑图标 - 保存为 static/images/mood/anxious.png
  anxious: `<svg width="60" height="60" viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="30" cy="30" r="28" fill="#FFDD67"/>
    <path d="M20 38C20 38 24.5 42 30 42C35.5 42 40 38 40 38" stroke="#664E27" stroke-width="3" stroke-linecap="round"/>
    <path d="M21 24L18 22M25 22L23 19" stroke="#664E27" stroke-width="2" stroke-linecap="round"/>
    <path d="M39 24L42 22M35 22L37 19" stroke="#664E27" stroke-width="2" stroke-linecap="round"/>
    <path d="M25.75 30C25.75 32.76 22.61 35 18.75 35C14.89 35 11.75 32.76 11.75 30C11.75 27.24 14.89 25 18.75 25C22.61 25 25.75 27.24 25.75 30Z" fill="#664E27"/>
    <path d="M48.25 30C48.25 32.76 45.11 35 41.25 35C37.39 35 34.25 32.76 34.25 30C34.25 27.24 37.39 25 41.25 25C45.11 25 48.25 27.24 48.25 30Z" fill="#664E27"/>
  </svg>`
};

// 空状态图像 - 保存为 static/images/empty_diary.png
const emptyDiary = `<svg width="200" height="200" viewBox="0 0 200 200" fill="none" xmlns="http://www.w3.org/2000/svg">
  <rect x="35" y="28" width="130" height="160" rx="8" fill="#F0F7FF"/>
  <rect x="50" y="48" width="100" height="12" rx="6" fill="#E0E0E0"/>
  <rect x="50" y="68" width="80" height="8" rx="4" fill="#E0E0E0"/>
  <rect x="50" y="83" width="100" height="8" rx="4" fill="#E0E0E0"/>
  <rect x="50" y="98" width="70" height="8" rx="4" fill="#E0E0E0"/>
  <rect x="50" y="118" width="100" height="8" rx="4" fill="#E0E0E0"/>
  <rect x="50" y="133" width="90" height="8" rx="4" fill="#E0E0E0"/>
  <rect x="50" y="148" width="60" height="8" rx="4" fill="#E0E0E0"/>
  <path d="M148 82C152.418 82 156 78.4183 156 74C156 69.5817 152.418 66 148 66C143.582 66 140 69.5817 140 74C140 78.4183 143.582 82 148 82Z" fill="#4A7CFF"/>
  <path d="M149.5 75.5L146.5 72.5M149.5 72.5L146.5 75.5" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
  <path d="M133 161C136.314 161 139 158.314 139 155C139 151.686 136.314 149 133 149C129.686 149 127 151.686 127 155C127 158.314 129.686 161 133 161Z" fill="#4A7CFF"/>
  <path d="M134 156L132 154M134 154L132 156" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
  <path d="M92 179C96.4183 179 100 175.418 100 171C100 166.582 96.4183 163 92 163C87.5817 163 84 166.582 84 171C84 175.418 87.5817 179 92 179Z" fill="#4A7CFF"/>
  <path d="M92 171.5L93.5 173L94.5 169" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
  <path d="M60 124C64.4183 124 68 120.418 68 116C68 111.582 64.4183 108 60 108C55.5817 108 52 111.582 52 116C52 120.418 55.5817 124 60 124Z" fill="#4A7CFF"/>
  <path d="M60 116.5L61.5 118L62.5 114" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
  <path fill-rule="evenodd" clip-rule="evenodd" d="M150 44H50C46.134 44 43 40.866 43 37V35C43 31.134 46.134 28 50 28H150C153.866 28 157 31.134 157 35V37C157 40.866 153.866 44 150 44ZM115 36C117.761 36 120 33.7614 120 31C120 28.2386 117.761 26 115 26C112.239 26 110 28.2386 110 31C110 33.7614 112.239 36 115 36ZM95 36C97.7614 36 100 33.7614 100 31C100 28.2386 97.7614 26 95 26C92.2386 26 90 28.2386 90 31C90 33.7614 92.2386 36 95 36ZM80 31C80 33.7614 77.7614 36 75 36C72.2386 36 70 33.7614 70 31C70 28.2386 72.2386 26 75 26C77.7614 26 80 28.2386 80 31Z" fill="#4A7CFF"/>
  <path d="M115 31H110M95 31H90M75 31H70" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
</svg>`;

export {
  moodIcons,
  emptyDiary
}; 