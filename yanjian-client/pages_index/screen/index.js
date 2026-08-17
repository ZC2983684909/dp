// pages_index/screen/index.js

import { provinceList, allCity, isVipFn } from "../../pages/api/common";
import { personalCenter } from "../../pages/api/index";
import { recursiveTransform, renderData } from "../../utils/utils";
Page({
    data: {
        form: {
            isIdAuth: false,
            similarityText: "",
        },
        fieldNames: {
            text: "regionName",
            value: "regionName",
            children: "children",
        },
        // 用来标识现居地是否被修改过
        cityChange: false,
        show1: false,
        show1Options: [],
        show2: false,
        actions2: [],
        year1Idx: 25,
        year2Idx: 29,
        show3: false,
        similarityColumns: ["不限", "60%以上", "70%以上", "80%以上", "90%以上"],
        similarityVals: ["", "60", "70", "80", "90"],
        isVip: false,
        showVipDialog: false,
        actionsSex: [
            {
                name: "不限",
                value: "不限",
            },
            {
                name: "男",
                value: "男",
            },
            {
                name: "女",
                value: "女",
            },
        ],
        showSex: false,
    },
    onLoad(options) {
        let userInfo = wx.getStorageSync("userInfo");
        let city = wx.getStorageSync("city");
        this.setData({
            form: {
                ...this.data.form,
                city: city || "北京市",
            },
        });
        let searchData = wx.getStorageSync("searchData");
        if (searchData) {
            if (searchData.similarity) {
                searchData.similarityText = searchData.similarity + "%以上";
            }

            this.setData({ form: searchData });
        }

        if (options.sex) {
            this.setData({
                form: {
                    ...this.data.form,
                    sex: options.sex,
                },
            });
        }
        this.setActions2();
        this.getProvinceList();
        this.getIsVip();
    },
    onShow() {},
    closeMoreFn() {
        let temp = { ...this.data.form };
        this.setData({
            form: temp,
        });
    },
    changeshow1() {
        if (!this.data.isVip) {
            this.showVipDialog();
            return;
        }
        this.setData({
            show1: true,
        });
    },
    async onChange1(e) {
        const { value, selectedOptions } = e.detail;
        if (selectedOptions.length == 1) {
            let temp = await renderData(
                this.data.show1Options,
                value,
                selectedOptions
            );
            this.setData({
                show1Options: temp,
            });
        }
    },
    onClose1() {
        this.setData({
            show1: false,
        });
    },
    onFinish1(e) {
        const { selectedOptions, value } = e.detail;
        if (selectedOptions.length < 2) return;
        // const fieldValue = selectedOptions[0].regionName+selectedOptions[1].regionName
        const fieldValue = selectedOptions[1].regionName;
        this.setData({
            form: {
                ...this.data.form,
                city: fieldValue,
            },
            cityChange: true,
        });
        this.onClose1();
    },
    changeshow2() {
        if (!this.data.isVip) {
            this.showVipDialog();
            return;
        }
        this.setData({
            show2: true,
        });
    },
    onClose2() {
        this.setData({
            form: {
                ...this.data.form,
                minAge: this.data.actions2[this.data.year1Idx],
                maxAge: this.data.actions2[this.data.year2Idx],
                yearStr:
                    this.data.actions2[this.data.year1Idx] +
                    " - " +
                    this.data.actions2[this.data.year2Idx],
            },
        });
        this.setData({
            show2: false,
        });
    },
    onChangeYear1(value) {
        this.setData({
            year1Idx: value.detail.index,
            year2Idx: value.detail.index + 5,
        });
    },
    onChangeYear2(value) {
        this.setData({
            year2Idx: value.detail.index,
        });
    },
    changeshow3() {
        if (!this.data.isVip) {
            this.showVipDialog();
            return;
        }
        this.setData({
            show3: true,
        });
    },
    similarityConfirm(e) {
        this.setData({
            form: {
                ...this.data.form,
                similarityText: e.detail.value,
                similarity: this.data.similarityVals[e.detail.index],
            },
            show3: false,
        });
    },
    onClose3() {
        this.setData({
            show3: false,
        });
    },
    getProvinceList() {
        provinceList().then((res) => {
            this.setData({
                show1Options: res.data,
            });
        });
    },
    setActions2() {
        let tempArr = [];
        for (let index = 1970; index <= 2020; index++) {
            tempArr.push(index);
        }
        tempArr.unshift("不限");
        this.setData({
            actions2: tempArr,
        });
    },
    onClickLeft() {
        wx.navigateBack();
    },
    changeSwitch(e) {
        if (!this.data.isVip) {
            this.showVipDialog();
            return;
        }
        this.setData({
            form: {
                ...this.data.form,
                isIdAuth: e.detail,
            },
        });
    },
    changeSwitch2(e) {
        if (!this.data.isVip) {
            this.showVipDialog();
            return;
        }
        this.setData({
            form: {
                ...this.data.form,
                isOriginalCamera: e.detail,
            },
        });
    },
    changeshowsex() {
        if (!this.data.isVip) {
            this.showVipDialog();
            return;
        }
        this.setData({
            showSex: true,
        });
    },
    onClosesex() {
        this.setData({
            showSex: false,
        });
    },
    onSelectsex(value) {
        this.setData({
            form: {
                ...this.data.form,
                sex: value.detail.value,
            },
        });
    },
    changeshow5() {
        if (!this.data.isVip) {
            this.showVipDialog();
            return;
        }
        wx.navigateTo({
            url:
                "/pages_register/select_tag/index?type=3&otherLabel=" +
                this.data.form.otherLabel,
        });
    },
    // 判断是否是vip。只有开通会员后才能进行筛选操作
    getIsVip() {
        isVipFn().then((res) => {
            this.setData({
                isVip: res.data.isVip,
            });
        });
    },
    // 开启vip弹窗
    showVipDialog() {
        this.setData({
            showVipDialog: true,
        });
    },
    closeVipDialog() {
        this.setData({
            showVipDialog: false,
        });
    },
    submit(event) {
        const pages = getCurrentPages();
        const prePage = pages[pages.length - 2];
        let data = {
            ...prePage.data.params,
            pageIndex: 1,
            ...this.data.form,
        };
        if (data.minAge !== "不限" && data.maxAge !== "不限") {
            const minAgeNum = Number(data.minAge);
            const maxAgeNum = Number(data.maxAge);
            // 确保都是有效数字且最小年龄不大于最大年龄
            if (
                !isNaN(minAgeNum) &&
                !isNaN(maxAgeNum) &&
                minAgeNum > maxAgeNum
            ) {
                wx.showToast({
                    title: "年龄格式错误",
                    icon: "none",
                });
                return;
            }
        }
        personalCenter().then((res) => {
            delete data.similarityText;
            if (this.data.form.city != prePage.data.params.city) {
                delete data.county;
            }
            wx.setStorageSync("searchData", data);
            wx.navigateBack();
        });
    },
});
