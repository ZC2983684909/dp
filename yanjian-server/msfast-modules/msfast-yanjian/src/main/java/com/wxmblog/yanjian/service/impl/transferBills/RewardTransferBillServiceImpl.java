package com.wxmblog.yanjian.service.impl.transferBills;

import com.wxmblog.base.common.web.domain.ServiceR;
import com.wxmblog.base.pay.common.rest.vo.transfer.TransferBillsPayData;
import com.wxmblog.base.pay.service.ITransferBillsService;
import com.wxmblog.yanjian.common.rest.request.front.transferBill.RewardTransferBillRequest;
import org.springframework.stereotype.Service;

@Service("RewardTransferBillServiceImpl")
public class RewardTransferBillServiceImpl extends ITransferBillsService<RewardTransferBillRequest> {
    @Override
    public ServiceR<TransferBillsPayData> pay(RewardTransferBillRequest request) {
        return null;
    }
}
