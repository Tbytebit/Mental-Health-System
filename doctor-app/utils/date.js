/**
 * 日期格式化
 * @param {Date} date 日期对象
 * @param {String} fmt 格式字符串，默认为 'yyyy-MM-dd'
 * @returns {String} 格式化后的日期字符串
 */
export function formatDate(date, fmt = 'yyyy-MM-dd') {
  if (!date) return '';
  if (typeof date === 'string') {
    date = new Date(date.replace(/-/g, '/'));
  }
  if (typeof date === 'number') {
    date = new Date(date);
  }
  
  const o = {
    'M+': date.getMonth() + 1, // 月份
    'd+': date.getDate(), // 日
    'h+': date.getHours() % 12 === 0 ? 12 : date.getHours() % 12, // 小时
    'H+': date.getHours(), // 小时
    'm+': date.getMinutes(), // 分
    's+': date.getSeconds(), // 秒
    'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
    'S': date.getMilliseconds() // 毫秒
  };
  
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  }
  
  for (let k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)));
    }
  }
  
  return fmt;
}

/**
 * 解析日期字符串为日期对象
 * @param {String} dateStr 日期字符串
 * @returns {Date} 日期对象
 */
export function parseDate(dateStr) {
  if (!dateStr) return null;
  // 处理iOS下日期格式问题，将'-'替换为'/'
  return new Date(dateStr.replace(/-/g, '/'));
}

/**
 * 日期加天数
 * @param {Date} date 日期对象
 * @param {Number} days 天数，可为负数
 * @returns {Date} 新的日期对象
 */
export function addDays(date, days) {
  if (!date) return null;
  const result = new Date(date);
  result.setDate(result.getDate() + days);
  return result;
}

/**
 * 获取两个日期之间的天数差
 * @param {Date|String} date1 日期1
 * @param {Date|String} date2 日期2
 * @returns {Number} 天数差
 */
export function getDaysDiff(date1, date2) {
  if (!date1 || !date2) return 0;
  
  if (typeof date1 === 'string') {
    date1 = parseDate(date1);
  }
  if (typeof date2 === 'string') {
    date2 = parseDate(date2);
  }
  
  // 转换为 UTC 时间进行计算，避免时区和夏令时的影响
  const utc1 = Date.UTC(date1.getFullYear(), date1.getMonth(), date1.getDate());
  const utc2 = Date.UTC(date2.getFullYear(), date2.getMonth(), date2.getDate());
  
  // 一天的毫秒数
  const MS_PER_DAY = 1000 * 60 * 60 * 24;
  
  // 计算差值天数
  return Math.floor((utc2 - utc1) / MS_PER_DAY);
}

/**
 * 获取日期是周几
 * @param {Date} date 日期对象
 * @returns {String} 周几
 */
export function getDayOfWeek(date) {
  if (!date) return '';
  
  const weeks = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return weeks[date.getDay()];
}

/**
 * 日期是否是今天
 * @param {Date|String} date 日期
 * @returns {Boolean} 是否是今天
 */
export function isToday(date) {
  if (!date) return false;
  
  if (typeof date === 'string') {
    date = parseDate(date);
  }
  
  const today = new Date();
  return (
    date.getDate() === today.getDate() &&
    date.getMonth() === today.getMonth() &&
    date.getFullYear() === today.getFullYear()
  );
} 