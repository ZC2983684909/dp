// components/DynamicItem/index.js
import { env } from "../../env";
import { previewMedia,throttle } from "../../utils/utils";
import {
    praiseArticle,
    cancelPraiseArticle,
    delArticle,
    personalCenter,
} from "../../pages/api/index";
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        info: {
            type: Object,
        },
        showMsgAndArt: {
            type: Boolean,
            value: true,
        },
        bgColor: {
            type: String,
            value: "#F6F6F6",
        },
        // 是否是在列表中显示，只有在列表中才需要跳转到详情
        isList: {
            type: Boolean,
            value: true,
        },
        // 是否显示全文按钮
        isShowMore: {
            type: Boolean,
            value: true,
        },
    },
    lifetimes: {
        attached: function () {
            this.setData({
                imgBaseURL: env.imgBaseURL,
            });
            let userInfo = wx.getStorageSync("userInfo");
            if (userInfo) {
                this.setData({
                    userId: userInfo.id,
                });
            } else {
                this.setData({
                    userId: "",
                });
            }
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
        userId: "",
    },

    /**
     * 组件的方法列表
     */
    methods: {
        share() {
            this.triggerEvent("share");
        },
        edit() {
            // 判断是否是自己的评论来，true：显示删除按钮 false：显示举报按钮
            this.triggerEvent("edit", this.data.info.isDelete);
        },
        preview(event) {
            const dataset = event.currentTarget.dataset;
            let urls = dataset.imgs.map((item) => {
                return {
                    url: item,
                    type: dataset.type,
                };
            });
            previewMedia(urls, dataset.index);
        },
        goPage(event) {
          let token  = wx.getStorageSync('token')
            const tp = event.currentTarget.dataset.path;
            const paramName = tp === "/pages_index/dynamic_detail/index"
              ? "articleId"
              : "userId";
            const paramValue = paramName === "articleId"
              ? this.data.info.id
              : this.data.info.userId;
            const path = `${tp}?${paramName}=${paramValue}`;

            if(token){
              wx.navigateTo({
                url: path,
            });
            }else{
                personalCenter().then((res) => {
              
              })
            }
        },
        topicDetail(event) {
            const id = event.detail && event.detail.id;
            if (!id) {
                return;
            }
            wx.navigateTo({
                url: `/pages_dynamic/topic_detail/index?id=${id}`,
            });
        },
        article() {
            if (this.data.info.isLike) {
                cancelPraiseArticle({
                    id: this.data.info.id,
                    praiseType: "ARTICLE",
                }).then((res) => {
                    this.setData({
                        info: {
                            ...this.data.info,
                            isLike: false,
                            likeCount: this.data.info.likeCount - 1,
                        },
                    });
                    this.triggerEvent("refresh");
                });
            } else {
                praiseArticle({
                    id: this.data.info.id,
                    praiseType: "ARTICLE",
                }).then((res) => {
                    this.setData({
                        info: {
                            ...this.data.info,
                            isLike: true,
                            likeCount: this.data.info.likeCount + 1,
                        },
                    });
                    this.triggerEvent("refresh");
                });
            }
        },
    },
});
