/**
 * 后端加密工具类
 * 实现RSA+AES混合加密方案的解密功能，用于阿里云百炼平台的加密传输
 */

const crypto = require('crypto');

// 阿里云百炼平台提供的公钥信息
const PUBLIC_KEY_ID = '****';
const PUBLIC_KEY = '****';
// 私钥（注意：实际应用中应该从环境变量或安全存储中获取）
// 这里需要对应的私钥来解密AES密钥，但由于只提供了公钥，我们需要生成一对密钥用于演示
let PRIVATE_KEY = null;

// 加密配置
const ENCRYPTION_CONFIG = {
  aes: {
    algorithm: 'aes-128-cbc',
    keyLength: 16, // 128位 = 16字节
    ivLength: 16   // 初始化向量长度
  },
  rsa: {
    algorithm: 'rsa',
    padding: crypto.constants.RSA_PKCS1_OAEP_PADDING,
    hash: 'sha256'
  }
};

/**
 * 初始化加密配置
 * 生成RSA密钥对用于演示（实际应用中应使用阿里云提供的密钥对）
 */
function initializeEncryption() {
  try {
    // 注意：在实际应用中，应该使用阿里云提供的私钥
    // 这里为了演示目的生成一对密钥
    const { publicKey, privateKey } = crypto.generateKeyPairSync('rsa', {
      modulusLength: 2048,
      publicKeyEncoding: {
        type: 'spki',
        format: 'pem'
      },
      privateKeyEncoding: {
        type: 'pkcs8',
        format: 'pem'
      }
    });
    
    PRIVATE_KEY = privateKey;
    console.log('后端加密配置初始化成功');
    return true;
  } catch (error) {
    console.error('后端加密配置初始化失败:', error);
    return false;
  }
}

/**
 * 使用私钥解密AES密钥
 * @param {string} encryptedAESKey Base64编码的加密AES密钥
 * @returns {Buffer} 解密后的AES密钥
 */
function decryptAESKeyWithRSA(encryptedAESKey) {
  try {
    if (!PRIVATE_KEY) {
      throw new Error('私钥未初始化');
    }
    
    const encryptedBuffer = Buffer.from(encryptedAESKey, 'base64');
    
    return crypto.privateDecrypt(
      {
        key: PRIVATE_KEY,
        padding: ENCRYPTION_CONFIG.rsa.padding,
        oaepHash: ENCRYPTION_CONFIG.rsa.hash
      },
      encryptedBuffer
    );
  } catch (error) {
    console.error('RSA解密AES密钥失败:', error);
    throw new Error('RSA解密AES密钥失败');
  }
}

/**
 * 使用AES解密数据
 * @param {string} encryptedData Base64编码的加密数据
 * @param {Buffer} aesKey AES密钥
 * @param {string} ivBase64 Base64编码的初始化向量
 * @returns {string} 解密后的原始数据
 */
function decryptWithAES(encryptedData, aesKey, ivBase64) {
  try {
    const encryptedBuffer = Buffer.from(encryptedData, 'base64');
    const iv = Buffer.from(ivBase64, 'base64');
    
    const decipher = crypto.createDecipheriv(ENCRYPTION_CONFIG.aes.algorithm, aesKey, iv);
    
    let decrypted = decipher.update(encryptedBuffer);
    decrypted = Buffer.concat([decrypted, decipher.final()]);
    
    return decrypted.toString('utf8');
  } catch (error) {
    console.error('AES解密失败:', error);
    throw new Error('AES解密失败');
  }
}

/**
 * 解密请求数据
 * @param {Object} encryptedRequest 加密的请求数据
 * @param {string} encryptedRequest.public_key_id 公钥ID
 * @param {string} encryptedRequest.encrypted_key Base64编码的加密AES密钥
 * @param {string} encryptedRequest.iv Base64编码的初始化向量
 * @param {string} encryptedRequest.encrypted_data Base64编码的加密数据
 * @returns {Object} 解密后的原始请求数据
 */
function decryptRequestData(encryptedRequest) {
  try {
    console.log('开始解密请求数据');
    
    // 验证公钥ID
    if (encryptedRequest.public_key_id !== PUBLIC_KEY_ID) {
      throw new Error('公钥ID不匹配');
    }
    
    // 验证必要字段
    if (!encryptedRequest.encrypted_key || !encryptedRequest.iv || !encryptedRequest.encrypted_data) {
      throw new Error('加密请求数据格式不完整');
    }
    
    // 1. 使用私钥解密AES密钥
    const aesKey = decryptAESKeyWithRSA(encryptedRequest.encrypted_key);
    
    // 2. 使用AES密钥解密数据
    const decryptedData = decryptWithAES(
      encryptedRequest.encrypted_data,
      aesKey,
      encryptedRequest.iv
    );
    
    // 3. 解析JSON数据
    const originalData = JSON.parse(decryptedData);
    
    console.log('请求数据解密成功');
    return originalData;
  } catch (error) {
    console.error('解密请求数据失败:', error);
    throw new Error('解密请求数据失败: ' + error.message);
  }
}

/**
 * 检查是否为加密请求
 * @param {Object} req Express请求对象
 * @returns {boolean} 是否为加密请求
 */
function isEncryptedRequest(req) {
  return req.headers['x-encrypted'] === 'true' && 
         req.body && 
         typeof req.body === 'object' &&
         req.body.public_key_id &&
         req.body.encrypted_key &&
         req.body.iv &&
         req.body.encrypted_data;
}

/**
 * Express中间件：处理加密请求
 * @param {Object} req Express请求对象
 * @param {Object} res Express响应对象
 * @param {Function} next 下一个中间件函数
 */
function decryptionMiddleware(req, res, next) {
  try {
    // 检查是否为加密请求
    if (isEncryptedRequest(req)) {
      console.log('检测到加密请求，开始解密');
      
      // 解密请求数据
      const decryptedData = decryptRequestData(req.body);
      
      // 替换请求体为解密后的数据
      req.body = decryptedData;
      req.isDecrypted = true;
      
      console.log('请求解密完成');
    }
    
    next();
  } catch (error) {
    console.error('解密中间件处理失败:', error);
    res.status(400).json({
      success: false,
      message: '请求解密失败',
      error: error.message
    });
  }
}

/**
 * 验证加密配置
 * @returns {boolean} 验证结果
 */
function validateEncryptionConfig() {
  try {
    // 检查Node.js crypto模块
    if (!crypto) {
      console.error('Node.js crypto模块不可用');
      return false;
    }
    
    // 检查私钥
    if (!PRIVATE_KEY) {
      console.warn('私钥未配置，需要初始化');
      return initializeEncryption();
    }
    
    console.log('后端加密配置验证成功');
    return true;
  } catch (error) {
    console.error('后端加密配置验证失败:', error);
    return false;
  }
}

/**
 * 生成用于前端的公钥（演示用）
 * @returns {string} PEM格式的公钥
 */
function getPublicKeyForFrontend() {
  if (!PRIVATE_KEY) {
    initializeEncryption();
  }
  
  // 从私钥生成对应的公钥
  const publicKey = crypto.createPublicKey(PRIVATE_KEY);
  return publicKey.export({
    type: 'spki',
    format: 'pem'
  });
}

module.exports = {
  initializeEncryption,
  decryptRequestData,
  decryptionMiddleware,
  isEncryptedRequest,
  validateEncryptionConfig,
  getPublicKeyForFrontend,
  ENCRYPTION_CONFIG,
  PUBLIC_KEY_ID
};