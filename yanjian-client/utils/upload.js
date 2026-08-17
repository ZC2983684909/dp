import {
  env
} from "../env.js"
import {filedEncry} from "../utils/filedEncry"

//是否调用持久化接口 hold 
export default function uploadFilePromise(file,hold=false) {
  return new Promise((resolve, reject) => {
    let arr = []
    let url = ''
    if(file.url){
     arr= file.url.split('.')
     url = file.url
    }else if(file.tempFilePath){
      arr= file.tempFilePath.split('.')
      url = file.tempFilePath
    }
    let path = hold?'/file/lasting/upload':'/file/upload'
    wx.uploadFile({
      url: env.baseURL + path,
      filePath: url,
      timeout:600000,
      header: {
        Authorization: wx.getStorageSync("token"),
        ...filedEncry({})
      },
      name: 'file',
      success: (uploadFileRes) => {
        let data = JSON.parse(uploadFileRes.data)
        // 后端的200状态也可能包含错误
        if (data.code == 10000) {
          reject(new Error(data.message));
        }
        resolve(data.data.url)
      },
      fail: (err)=>{
        console.log(err);
        reject(err)
      }
    });
  })
}