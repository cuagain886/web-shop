<template>
  <footer class="jd-footer">
    <div class="footer-container">
      <div class="footer-info">
        <div class="footer-section">
          <h4>购物指南</h4>
          <a href="#">购物流程</a>
          <a href="#">会员介绍</a>
          <a href="#">常见问题</a>
        </div>
        <div class="footer-section">
          <h4>配送方式</h4>
          <a href="#">上门自提</a>
          <a href="#">配送服务</a>
          <a href="#">配送范围</a>
        </div>
        <div class="footer-section">
          <h4>支付方式</h4>
          <a href="#">在线支付</a>
          <a href="#">货到付款</a>
          <a href="#">分期付款</a>
        </div>
        <div class="footer-section">
          <h4>售后服务</h4>
          <a href="#">退换货政策</a>
          <a href="#">退款说明</a>
          <a href="#">联系客服</a>
        </div>
      </div>
      <div class="footer-copy">
        <p>&copy; 2025 京东风格购物平台 | 仅供学习演示使用</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
console.log('📌 Footer组件加载')
</script>

<style scoped>
.jd-footer {
  background: #333;
  color: #999;
  margin-top: 50px;
}

.footer-container {
  width: 1200px;
  margin: 0 auto;
  padding: 40px 20px 20px;
}

.footer-info {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #444;
}

.footer-section h4 {
  color: #fff;
  margin: 0 0 15px;
  font-size: 16px;
}

.footer-section a {
  display: block;
  color: #999;
  text-decoration: none;
  margin-bottom: 10px;
  font-size: 14px;
  transition: color 0.2s;
}

.footer-section a:hover {
  color: #e4393c;
}

.footer-copy {
  text-align: center;
  font-size: 12px;
}

.footer-copy p {
  margin: 0;
}

@media (max-width: 1200px) {
  .footer-container {
    width: 100%;
  }
  
  .footer-info {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
