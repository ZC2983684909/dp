// components/Article/index.js
import {
  env
} from "../../env";
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    isLike: {
      type: Boolean,
      value: false,
      observer(newVal,oldVal) {
        if (this.data.isInitialized && newVal) {
          this.triggerAnimation();
        }
      }
    },
    size: {
      type: String,
      value: "36rpx",
    },
  },
  lifetimes: {
    attached: function () {
      this.setData({
        imgBaseURL: env.imgBaseURL,
      });
      setTimeout(() => {
        this.setData({
          isInitialized: true
        })
      }, 200);
    },
    detached: function () {
      // 在组件实例被从页面节点树移除时执行
    },
  },

  /**
   * 组件的初始数据
   */
  data: {
    imgBaseURL: "",
    isAnimating: false,
    isInitialized: false,
  },

  /**
   * 组件的方法列表
   */
  methods: {
    triggerAnimation() {
      if (this.data.isLike) {
        this.setData({
          isAnimating: true
        });
        // 动画结束后重置状态
        setTimeout(() => {
          this.setData({
            isAnimating: false
          });
        }, 1500); // 与动画时长一致
      }
    }
  },
});