/**
 * SVG 转 PNG 工具
 * 
 * 此工具用于辅助开发者将 SVG 代码转换为 PNG 图像
 * 用法：在浏览器控制台中执行以下函数，传入 SVG 代码，将返回一个 data URL
 * 然后可以将 data URL 保存为 PNG 文件
 */

/**
 * 将SVG代码转换为PNG图像的data URL
 * @param {string} svgString SVG代码
 * @param {number} width 输出图像宽度
 * @param {number} height 输出图像高度
 * @returns {Promise<string>} 返回data URL
 */
function svgToPng(svgString, width, height) {
  return new Promise((resolve, reject) => {
    try {
      // 创建SVG Blob
      const svgBlob = new Blob([svgString], { type: 'image/svg+xml;charset=utf-8' });
      const URL = window.URL || window.webkitURL || window;
      const svgUrl = URL.createObjectURL(svgBlob);
      
      // 创建Image对象
      const img = new Image();
      img.onload = () => {
        // 创建Canvas
        const canvas = document.createElement('canvas');
        canvas.width = width || img.width;
        canvas.height = height || img.height;
        
        // 绘制SVG到Canvas
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
        
        // 转换为data URL
        const pngUrl = canvas.toDataURL('image/png');
        
        // 释放资源
        URL.revokeObjectURL(svgUrl);
        
        resolve(pngUrl);
      };
      
      img.onerror = (err) => {
        URL.revokeObjectURL(svgUrl);
        reject(err);
      };
      
      img.src = svgUrl;
    } catch (err) {
      reject(err);
    }
  });
}

/**
 * 批量将多个SVG转换为PNG
 * @param {Object} svgMap 包含多个SVG代码的对象
 * @param {number} width 输出图像宽度
 * @param {number} height 输出图像高度
 * @returns {Promise<Object>} 返回包含data URL的对象
 */
function batchSvgToPng(svgMap, width, height) {
  const promises = {};
  
  for (const key in svgMap) {
    promises[key] = svgToPng(svgMap[key], width, height);
  }
  
  return Promise.all(Object.entries(promises).map(async ([key, promise]) => {
    return [key, await promise];
  })).then(results => {
    return Object.fromEntries(results);
  });
}

/**
 * 示例用法：
 * 
 * 1. 在浏览器控制台中导入mood_icons.js
 * 2. 执行以下代码：
 * 
 * import { moodIcons, emptyDiary } from './mood_icons.js';
 * import { batchSvgToPng } from './svgToPng.js';
 * 
 * // 转换心情图标
 * batchSvgToPng(moodIcons, 60, 60).then(results => {
 *   console.log('心情图标转换结果：', results);
 * });
 * 
 * // 转换空状态图片
 * svgToPng(emptyDiary, 200, 200).then(dataUrl => {
 *   console.log('空状态图片：', dataUrl);
 * });
 */

export {
  svgToPng,
  batchSvgToPng
}; 