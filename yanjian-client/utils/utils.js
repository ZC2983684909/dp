import { provinceSonList, getScene, sysconfigValue } from "../pages/api/common";
import { env } from "../env";

export const formatDate = function (time, format) {
    const date = new Date(time);
    var o = {
        "M+": date.getMonth() + 1, // 月份
        "d+": date.getDate(), // 日期
        "h+": date.getHours(), // 小时
        "m+": date.getMinutes(), // 分钟
        "s+": date.getSeconds(), // 秒
        "q+": Math.floor((date.getMonth() + 3) / 3), // 季度
        S: date.getMilliseconds(), // 毫秒
    };

    if (/(y+)/.test(format)) {
        format = format.replace(
            RegExp.$1,
            (date.getFullYear() + "").substr(4 - RegExp.$1.length)
        );
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(
                RegExp.$1,
                RegExp.$1.length == 1
                    ? o[k]
                    : ("00" + o[k]).substr(("" + o[k]).length)
            );
        }
    }

    return format;
};

export async function renderData(data, currentName, selectedOptions) {
    let level = selectedOptions.length;
    let result = [...data];
    // 根据层级查找对应的数据
    if (level === 1) {
        let idx = data.findIndex(
            (province) => province.regionName === currentName
        );
        let temp = data[idx];
        const res = await provinceSonList({
            parentCode: temp.regionCode,
        });
        temp.children = res.data;
        result.splice(idx, 1, temp);
        return result;
    } else {
        let idx1 = data.findIndex(
            (item) => item.regionName == selectedOptions[0].regionName
        );
        let idx2 = selectedOptions[0].children.findIndex(
            (item) => item.regionName == currentName
        );
        let temp = selectedOptions[1];
        const res = await provinceSonList({
            parentCode: temp.regionCode,
        });
        temp.children = res.data;
        let temp2 = selectedOptions[0];
        temp2.children[idx2] = temp;
        result.splice(idx1, 1, temp2);
        return result;
    }
}

// 定义一个递归函数来转换字段
export function recursiveTransform(items) {
    return items.map((item) => {
        const newItem = {
            ...item,
            text: item.regionName, // 将 label 改为 text
            id: item.regionName, // 将 value 改为 text
            value: item.regionName,
        };
        // 如果有子项，则递归处理
        if (item.children) {
            newItem.children = recursiveTransform(item.children);
        }
        return newItem;
    });
}

// 重新登录时，清楚缓存中的部分字段
export function removeStorageItem() {
    wx.removeStorageSync("userInfo");
    wx.removeStorageSync("token");
    wx.removeStorageSync("refreshToken");
    wx.removeStorageSync("tempUserInfo");
}

// 获取 refresh token（假设存在 storage 中）
export const getRefreshToken = () => {
    return wx.getStorageSync("refreshToken");
};

// 设置新的 token
export const setToken = (token) => {
    wx.removeStorageSync("token");
    wx.setStorageSync("token", token);
};
// 设置新的 refreshToken
export const setRefreshToken = (refreshToken) => {
    wx.removeStorageSync("refreshToken");
    wx.setStorageSync("refreshToken", refreshToken);
};

/**
 * 校验中国手机号格式
 * @param {string} phoneNumber - 待校验的手机号
 * @returns {boolean} - 返回是否符合中国手机号格式
 */
export function isValidChinesePhoneNumber(phoneNumber) {
    // 定义正则表达式，匹配以1开头，第二位为3-9之间的数字，总共11位
    const regex = /^1[3-9]\d{9}$/;

    // 使用正则表达式进行匹配
    return regex.test(phoneNumber);
}

// 判断是不是ios
export function phoneSys() {
    const { system } = wx.getDeviceInfo();
    if (system.indexOf("iOS") > -1) {
        return "iOS";
    } else {
        return "Android";
    }
}

// 获取缓存中经纬度
export function getLocationByStorage() {
    let userLocation = wx.getStorageSync("userLocation");
    let userLocationArr = [];
    if (userLocation) {
        userLocationArr = userLocation.split(",");
    }
    return userLocationArr;
}

// 解析通过小程序 码分享小程序时候填入的参数,并缓存，等待注册时填入
export function parseQueryString(queryString) {
    getScene(queryString).then((res) => {
        console.log(res);
        const { content } = res.data;
        // content的示例值是 "inviterId=1&xx=xx" 请解析这个数据
        const query = content.split("&").reduce((acc, cur) => {
            const [key, value] = cur.split("=");
            acc[key] = value;
            return acc;
        }, {});
        console.log(query.inviterId, "query.inviterId");
        wx.setStorageSync("inviterId", query.inviterId);
    });
}

// 预览视频和图片
export function previewMedia(mediaList, index) {
    if (mediaList[0].type == "video") {
        wx.previewMedia({
            sources: mediaList,
            current: index,
            showmenu: false,
        });
    } else {
        let imgs = mediaList.map((item) => item.url);
        let current = imgs[index];
        wx.previewImage({
            urls: imgs,
            current: current,
        });
    }
}

// 联系企业微信客服
export function contactCustomerService() {
    sysconfigValue({
        code: "wxqr",
    }).then((res) => {
        wx.openCustomerServiceChat({
            extInfo: {
                url: res.data,
            },
            corpId: env.corpId,
            success(res) {},
            fail(err) {
                console.log(err);
            },
        });
    });
}
