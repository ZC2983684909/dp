Component({
  properties: {
      text: {
          type: String,
          value: "",
      },
      // 是否显示展开全文按钮
      isShowMore: {
          type: Boolean,
          value: true,
      },
      expandText: {
          type: String,
          value: "展开全文",
      },
      collapseText: {
          type: String,
          value: "收起",
      },
      // 按钮背景色，用来遮盖文字
      bgColor: {
          type: String,
          value: "#F6F6F6",
      },
      // 字体大小
      size: {
          type: String,
          value: "30rpx",
      },
  },
  data: {
      isExpand: false,
      isMore: false,
  },
  observers: {
      text: function (newVal) {
          this.checkOverflow();
      },
  },
  methods: {
      expansionClick() {
          this.setData({
              isExpand: !this.data.isExpand,
          });
      },
      checkOverflow() {
          const query = this.createSelectorQuery();
          query
              .select(".textCon")
              .boundingClientRect((rect) => {
                  const lineHeight = 24; // 每行高度
                  const maxHeight = lineHeight * 4; // 最大高度（4行）
                  this.setData({
                      isMore: rect.height > maxHeight, // 判断是否超出
                  });
              })
              .exec();
      },
  },
});
