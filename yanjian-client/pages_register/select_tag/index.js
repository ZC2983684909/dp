// pages_register/register/select_tag/index.js
import { sysconfigValue } from "../../pages/api/common";
import { env } from "../../env";
Page({
    data: {
        imgBaseURL: "",
        type: 1,
        actLeft: 0,
        tags: [],
        childTags: [],
        myTags: [],
        myTagsId: [],
        form: {},
        isAll: false,
        maxNum: 20,
    },
    onLoad(options) {
        this.setData({
            type: options.type,
            imgBaseURL: env.imgBaseURL,
        });
        if (options.type == 3) {
            this.setData({
                maxNum: 10,
            });
            if (options.otherLabel && options.otherLabel !== "undefined") {
                this.setData({
                    myTagsId: options.otherLabel.split(","),
                });
            }
        } else {
            this.setData({
                maxNum: 20,
            });
        }
        this.getRootData();
    },
    onShow() {},
    onClickLeft() {
        wx.navigateBack();
    },
    findChildsByName(list, targetName) {
        let self = this;
        for (const item of list) {
            if (item.name === targetName && item.imgUrl) {
                // 当前对象本身就是子对象
                return item;
            }
            if (item.childs && item.childs.length > 0) {
                const result = self.findChildsByName(item.childs, targetName);
                if (result) {
                    return result;
                }
            }
        }
        return null;
    },
    moreFn() {
        this.setData({
            isAll: !this.data.isAll,
        });
    },
    getSelectTag() {
        const pages = getCurrentPages();
        const prePage = pages[pages.length - 2];
        let temp = prePage.data.userInfo;
        if (temp && temp.fondTagsList) {
            this.setData({
                myTagsId:
                    (temp.fondTags &&
                        temp.fondTags.myLabel &&
                        temp.fondTags.myLabel.split(",")) ||
                    [],
            });
            if (this.data.myTagsId.length) {
                let arr = temp.fondTagsList;
                this.setData({
                    myTags: arr,
                });
            }
        }
    },
    changeRoot(event) {
        this.setData({
            actLeft: event.currentTarget.dataset.id,
        });
        this.getChildData(this.data.actLeft);
    },
    changeChild(event) {
        const item = event.currentTarget.dataset.item;
        let idx = this.data.myTags.findIndex((el) => el.name == item.name);
        const arr = this.data.myTags;
        const arr2 = this.data.myTagsId;
        if (idx == -1) {
            if (this.data.myTags.length >= this.data.maxNum) {
                return;
            }
            arr.push(item);
            arr2.push(item.name);
            this.setData({
                myTags: arr,
                myTagsId: arr2,
            });
        } else {
            arr.splice(idx, 1);
            arr2.splice(idx, 1);
            this.setData({
                myTags: arr,
                myTagsId: arr2,
            });
        }
    },
    changeMy(event) {
        const item = event.currentTarget.dataset.item;
    },
    delMy(event) {
        const id = event.currentTarget.dataset.id;
        let arr1 = this.data.myTags;
        let arr2 = this.data.myTagsId;
        let idx1 = arr1.findIndex((el) => el.name == id);
        arr1.splice(idx1, 1);
        let idx2 = arr2.findIndex((el) => el == id);
        arr2.splice(idx2, 1);
        this.setData({
            myTags: arr1,
            myTagsId: arr2,
        });
        setTimeout(() => {
            this.delImportant(event);
        }, 0);
    },
    delImportant(event) {
        const id = event.currentTarget.dataset.id;
    },
    submit() {
        const pages = getCurrentPages();
        const prePage = pages[pages.length - 2];
        let temp = {
            myLabel: this.data.myTagsId.join(","),
        };
        if (this.data.type == 3) {
            prePage.setData({
                form: {
                    ...prePage.data.form,
                    otherLabel: this.data.myTagsId.join(","),
                },
            });
        } else {
            prePage.setData({
                userInfo: {
                    ...prePage.data.userInfo,
                    fondTags: temp,
                },
            });
            if (prePage.setIsValidate && this.data.type == 2) {
                prePage.setIsValidate();
            }
        }

        wx.navigateBack();
    },
    getRootData() {
        sysconfigValue({
            code: "label_json",
        }).then((res) => {
            let temp = JSON.parse(res.data);

            this.setData({
                tags: temp.label,
                actLeft: temp.label[0].name,
            });
            if (this.data.type != 3) {
                this.getSelectTag();
            } else {
                if (this.data.myTagsId.length) {
                    let temp = [];
                    this.data.myTagsId.forEach((item) => {
                        let t = this.findChildsByName(this.data.tags, item);
                        temp.push(t);
                    });
                    this.setData({
                        myTags: temp,
                    });
                }
            }
            this.getChildData(this.data.actLeft);
        });
    },
    getChildData(id) {
        let temp = this.data.tags.find((item) => item.name == id);
        this.setData({
            childTags: temp.childs,
        });
    },
});
