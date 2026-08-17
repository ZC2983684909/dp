import { locationAnalysis } from "../pages/api/common";

// 获取位置详细信息（返回 Promise）
export function getAddress() {
    return new Promise(async (resolve, reject) => {
        wx.getLocation({
            type: "wgs84",
            success(res) {
                const { latitude, longitude } = res;
                locationAnalysis({
                    latitude,
                    longitude,
                }).then((res2) => {
                    wx.setStorageSync(
                        "userLocation",
                        `${res2.data.lon},${res2.data.lat}`
                    );
                    resolve(res2.data);
                });
            },
            fail(err) {
                reject(err);
            },
        });
    });
}
