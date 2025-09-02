import Vue from 'vue'
import App from './App'
import store from './store' // store
import plugins from './plugins' // plugins
import './permission' // permission



Vue.use(plugins)

Vue.config.productionTip = false 
Vue.prototype.$fileUrl = 'http://localhost:80/dev-api';
Vue.prototype.$store = store 


App.mpType = 'app'

const app = new Vue({
  ...App
})
 

app.$mount()
