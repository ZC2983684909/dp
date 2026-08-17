export function renderData(data,currentName, level) {
  let result;
  let idx;
  // 根据层级查找对应的数据
  if (level === 1) {
      // 查找省
      idx = data.findIndex(province => province.label === currentName);
      let level1 = data.map(item=>{
        return{
          label:item.label,
          value:item.value
        }
      })
      level1.splice(idx,1,data[idx])
      result = level1
  } 
  return result
}

// 定义一个递归函数来转换字段
export function recursiveTransform(items) {
  return items.map(item => {
    const newItem = {
      ...item,
      text: item.label, // 将 label 改为 text
      id: item.label, // 将 value 改为 text
      value: item.label
    };
    // 如果有子项，则递归处理
    if (item.children) {
      newItem.children = recursiveTransform(item.children);
    }
    return newItem;
  });
}