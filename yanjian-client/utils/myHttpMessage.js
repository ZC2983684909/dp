// 获取当前页面实例
function getContext() {
  const pages = getCurrentPages();
  return pages[pages.length - 1];
}

// 控制弹窗显隐方法
export function showFn(opt, onConfirm, onClose) {
  const options = {
    show: true,
    dom: '.httpMessage',
    ...opt
  };
  const page = getContext();
  const c = page.selectComponent(options.dom);
  if (!c) {
    console.warn(`未找到 ${options.dom} 节点，请确认 dom 是否正确`);
    return;
  }
  c.setData(options);
  c.onConfirm = onConfirm || (() => {})
  c.onClose = onClose || (() => {})
}